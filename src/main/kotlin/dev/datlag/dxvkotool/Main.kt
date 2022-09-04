package dev.datlag.dxvkotool

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.dxvkotool.common.header
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.io.GameIO
import dev.datlag.dxvkotool.io.SteamIO
import dev.datlag.dxvkotool.model.CacheInfo
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.model.UpdateButtonInfo
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.DXVKException
import dev.datlag.dxvkotool.other.DownloadException
import dev.datlag.dxvkotool.other.ReadErrorType
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.AsyncImage
import dev.datlag.dxvkotool.ui.compose.LoadFileDialog
import dev.datlag.dxvkotool.ui.compose.SaveFileDialog
import dev.datlag.dxvkotool.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.datlag.dxvkotool.ui.theme.Shape
import kotlinx.coroutines.CoroutineScope
import java.awt.Desktop

val LocalSnackbarHost = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    SteamIO.reload(coroutineScope)
    OnlineDXVK.getDXVKCaches(coroutineScope)

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    CompositionLocalProvider(LocalSnackbarHost provides scaffoldState.snackbarHostState) {
        AppTheme {
            Scaffold(
                topBar = {
                    ToolBar()
                },
                scaffoldState = scaffoldState,
                floatingActionButton = {
                    val snackbarHost = LocalSnackbarHost.current

                    FloatingActionButton(onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            snackbarHost.showSnackbar("Adding your own games is not implemented yet")
                        }
                    },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary) {
                        Icon(Icons.Filled.Add, StringRes.get().add)
                    }
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) {
                    GameList()
                }
            }
        }
    }
}

@Composable
@Preview
fun ToolBar() {
    val coroutineScope = rememberCoroutineScope()

    TopAppBar(
        title = {
            Text(
                text = StringRes.get().name,
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.Bold
            )
        },
        backgroundColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        actions = {
            val openDialog = remember { mutableStateOf(false)  }
            IconButton(onClick = {
                openDialog.value = true
            }) {
                Icon(
                    Icons.Filled.Info,
                    StringRes.get().info,
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
            IconButton(onClick = {
                SteamIO.reload(coroutineScope)
            }) {
                Icon(
                    Icons.Filled.Refresh,
                    StringRes.get().reload,
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    )
}

@Composable
@Preview
fun GameList() {
    val coroutineScope = rememberCoroutineScope()
    val gamesWithOnlineItem by GameIO.allGamesFlow.collectAsState(emptyList())
    gamesWithOnlineItem.onEach {
        it.loadCacheInfo(coroutineScope)
    }
    val (steamGames, otherGames) = gamesWithOnlineItem.partition { it is Game.Steam }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 500.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        header {
            Text(
                text = StringRes.get().steamGames,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(steamGames) {
            GameCard(it)
        }

        header {
            Text(
                text = StringRes.get().otherGames,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(otherGames) {
            GameCard(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun GameCard(game: Game) {
    val caches by game.caches.collectAsState()

    ElevatedCard(modifier = Modifier.fillMaxSize().fillMaxHeight()) {
        AsyncImage(
            game,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(0.dp, 0.dp, 0.dp, 16.dp)) {
            Text(
                text = game.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp, 8.dp),
                fontSize = 22.sp
            )
            Text(
                text = game.path.absolutePath,
                maxLines = 1,
                modifier = Modifier.padding(16.dp, 8.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
        caches.forEach {
            GameCache(game, it)
        }
    }
}

@Composable
@Preview
fun GameCache(game: Game, cache: DxvkStateCache) {
    val coroutineScope = rememberCoroutineScope()
    var isExportOpen by remember { mutableStateOf(false) }
    val info by cache.info.collectAsState()
    var isMenuOpen by remember { mutableStateOf(false) }
    var isLoadLocalFileOpen by remember { mutableStateOf(false) }
    var newEntrySize by mutableStateOf(-1)
    val snackbarHost = LocalSnackbarHost.current

    val updateInfo = when (info) {
        is CacheInfo.Loading.Url -> {
            UpdateButtonInfo(
                text = StringRes.get().loading,
                icon = Icons.Filled.HourglassBottom,
                isDownload = false,
                isMerge = false
            )
        }
        is CacheInfo.Loading.Download -> {
            UpdateButtonInfo(
                text = StringRes.get().downloading,
                icon = Icons.Filled.HourglassBottom,
                isDownload = false,
                isMerge = false
            )
        }
        is CacheInfo.Loading.Local -> {
            UpdateButtonInfo(
                text = StringRes.get().loading,
                icon = Icons.Filled.HourglassBottom,
                isDownload = false,
                isMerge = false
            )
        }
        is CacheInfo.None -> {
            UpdateButtonInfo(
                text = StringRes.get().noneFound,
                icon = Icons.Filled.Clear,
                isDownload = false,
                isMerge = false
            )
        }
        is CacheInfo.Url -> {
            if ((info as CacheInfo.Url).downloadUrl.isNullOrEmpty()) {
                UpdateButtonInfo(
                    text = StringRes.get().unavailable,
                    icon = Icons.Filled.Clear,
                    isDownload = false,
                    isMerge = false
                )
            } else {
                UpdateButtonInfo(
                    text = StringRes.get().download,
                    icon = Icons.Filled.FileDownload,
                    isDownload = true,
                    isMerge = false
                )
            }
        }
        is CacheInfo.Download.Cache -> {
            val newCache = (info as CacheInfo.Download.Cache)
            newEntrySize = newCache.combinedCache.entries.size - cache.entries.size
            if (newEntrySize > 0) {
                UpdateButtonInfo(
                    text = StringRes.get().merge,
                    icon = Icons.Filled.MergeType,
                    isDownload = false,
                    isMerge = true
                )
            } else {
                UpdateButtonInfo(
                    text = StringRes.get().upToDate,
                    icon = Icons.Filled.Check,
                    isDownload = false,
                    isMerge = false
                )
            }
        }
        is CacheInfo.Download.NoCache -> {
            UpdateButtonInfo(
                text = StringRes.get().unknown,
                icon = Icons.Filled.QuestionAnswer,
                isDownload = false,
                isMerge = false
            )
        }
        is CacheInfo.Merged -> {
            UpdateButtonInfo(
                text = if ((info as CacheInfo.Merged).success) StringRes.get().merged else StringRes.get().error,
                icon = if ((info as CacheInfo.Merged).success) Icons.Filled.Check else Icons.Filled.Clear,
                isDownload = false,
                isMerge = false
            )
        }
    }

    if (isExportOpen) {
        SaveFileDialog(cache.file.name) { destFile ->
            isExportOpen = false
            if (destFile != null) {
                val exportResult = cache.writeTo(destFile, false)
                snackbarHost.showFromResult(coroutineScope, exportResult, StringRes.get().exportSuccessful)
            }
        }
    }

    if (isLoadLocalFileOpen) {
        LoadFileDialog { loadFile ->
            isLoadLocalFileOpen = false
            if (loadFile != null) {
                cache.loadLocalFile(coroutineScope, loadFile)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth(0.5F).fillMaxHeight()) {
            Text(
                text = cache.file.name,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(0.dp, 8.dp)
            )
            Text(
                text = StringRes.get().versionPlaceholder.format(cache.header.version.toInt()),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
            Text(
                text = StringRes.get().entriesPlaceholder.format(cache.entries.size),
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Button(onClick = {
                isExportOpen = true
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.FileUpload, StringRes.get().export, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(StringRes.get().export)
            }
            Row(modifier = Modifier.fillMaxSize()) {
                Button(onClick = {
                    if (updateInfo.isDownload) {
                        val downloadResult = cache.downloadCache(coroutineScope)
                        downloadResult.onFailure {
                            snackbarHost.showFromResult(coroutineScope, downloadResult, String())
                        }
                    } else if (updateInfo.isMerge) {
                        val mergeResult = game.mergeCache(coroutineScope, cache)
                        mergeResult.onFailure {
                            snackbarHost.showFromResult(coroutineScope, mergeResult, String())
                        }
                    }
                },
                    modifier = Modifier.weight(1F),
                    enabled = updateInfo.isDownload || updateInfo.isMerge,
                    shape = Shape.LeftRoundedShape
                ) {
                    Icon(updateInfo.icon, updateInfo.text, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = updateInfo.text)
                }
                DropdownMenu(
                    expanded = isMenuOpen,
                    onDismissRequest = { isMenuOpen = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    DropdownMenuItem(onClick = {
                        isMenuOpen = false
                        isLoadLocalFileOpen = true
                    }, enabled = true) {
                        Icon(Icons.Filled.InsertDriveFile, StringRes.get().mergeLocalFile)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(StringRes.get().mergeLocalFile)
                    }
                    DropdownMenuItem(onClick = {
                        isMenuOpen = false
                    }, enabled = false) {
                        Icon(Icons.Filled.Link, StringRes.get().connectRepoItem)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(StringRes.get().connectRepoItem)
                    }
                    DropdownMenuItem(onClick = {
                        isMenuOpen = false
                        val openFolderResult = runCatching {
                            Desktop.getDesktop().open(cache.file.parentFile)
                        }
                        snackbarHost.showFromResult(coroutineScope, openFolderResult, String())
                    }, enabled = true) {
                        Icon(Icons.Filled.Folder, StringRes.get().openFolder)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(StringRes.get().openFolder)
                    }
                }
                Spacer(modifier = Modifier.padding(2.dp))
                Button(onClick = {
                    isMenuOpen = true
                }, shape = Shape.RightRoundedShape) {
                    Icon(Icons.Filled.ExpandMore, StringRes.get().more, modifier = Modifier.size(ButtonDefaults.IconSize))
                }
            }
            Button(onClick = {

            }, modifier = Modifier.fillMaxWidth(), enabled = false) {
                Icon(Icons.Filled.SettingsBackupRestore, StringRes.get().restoreBackup, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(StringRes.get().restoreBackup)
            }
        }
    }
}

fun <T> SnackbarHostState.showFromResult(scope: CoroutineScope, result: Result<T>, success: String) = scope.launch(Dispatchers.IO) {
    val message = if (result.isSuccess) {
        success
    } else when (val exception = result.exceptionOrNull()) {
        is DXVKException.ReadError -> {
            if (exception.type is ReadErrorType.MAGIC) {
                StringRes.get().fileInvalidMagic
            } else {
                StringRes.get().fileReadBytesError
            }
        }
        is DXVKException.InvalidEntry -> StringRes.get().fileInvalidEntry
        is DXVKException.UnexpectedEndOfFile -> StringRes.get().fileUnexpectedEnd
        is DXVKException.VersionMismatch -> StringRes.get().cacheVersionMismatchPlaceholder.format(exception.current.toInt(), exception.new.toInt())
        is DownloadException.NoDownloadUrl -> StringRes.get().noDownloadUrlProvided
        is DownloadException.InvalidFile -> StringRes.get().downloadFileInvalid
        else -> {
            val innerMessage = exception?.message ?: StringRes.get().unknown
            innerMessage.ifEmpty {
                StringRes.get().unknown
            }
        }
    }

    if (message.isNotEmpty()) {
        this@showFromResult.showSnackbar(message)
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = StringRes.get().name
    ) {
        App()
    }
}