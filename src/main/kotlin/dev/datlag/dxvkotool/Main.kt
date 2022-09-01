package dev.datlag.dxvkotool

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
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
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.AsyncImage
import dev.datlag.dxvkotool.ui.compose.FileDialog
import dev.datlag.dxvkotool.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    SteamIO.reload(coroutineScope)
    OnlineDXVK.getDXVKCaches(coroutineScope)

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    AppTheme {
        Scaffold(
            topBar = {
                ToolBar()
            },
            scaffoldState = scaffoldState,
            floatingActionButton = {
                FloatingActionButton(onClick = {

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
fun GameCache(game: Game, entry: Map.Entry<DxvkStateCache, CacheInfo>) {
    val coroutineScope = rememberCoroutineScope()
    val (cache, info) = entry
    val isExportOpen = remember { mutableStateOf(false) }

    if (isExportOpen.value) {
        FileDialog(cache.file!!.name) { destFile ->
            isExportOpen.value = false
            if (destFile != null) {
                cache.writeTo(destFile, false)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.fillMaxWidth(0.5F).fillMaxHeight()) {
            Text(
                text = cache.file!!.name,
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
                is CacheInfo.None -> {
                    UpdateButtonInfo(
                        text = StringRes.get().noneFound,
                        icon = Icons.Filled.Clear,
                        isDownload = false,
                        isMerge = false
                    )
                }
                is CacheInfo.Url -> {
                    if (info.downloadUrl.isNullOrEmpty()) {
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
                is CacheInfo.Download -> {
                    UpdateButtonInfo(
                        text = StringRes.get().merge,
                        icon = Icons.Filled.MergeType,
                        isDownload = false,
                        isMerge = true
                    )
                }
                is CacheInfo.Merged -> {
                    UpdateButtonInfo(
                        text = if (info.success) StringRes.get().merged else StringRes.get().error,
                        icon = if (info.success) Icons.Filled.Check else Icons.Filled.Clear,
                        isDownload = false,
                        isMerge = false
                    )
                }
                else -> {
                    UpdateButtonInfo(
                        text = StringRes.get().unknown,
                        icon = Icons.Filled.QuestionAnswer,
                        isDownload = false,
                        isMerge = false
                    )
                }
            }

            Button(onClick = {
                isExportOpen.value = true
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.FileUpload, StringRes.get().export, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(StringRes.get().export)
            }
            Button(onClick = {
                if (updateInfo.isDownload) {
                    coroutineScope.launch(Dispatchers.IO) {
                        val downloadUrl = (info as? CacheInfo.Url?)?.downloadUrl
                        if (!downloadUrl.isNullOrEmpty()) {
                            run {
                                val caches = game.caches.value.toMutableMap()
                                caches[cache] = CacheInfo.Loading.Download
                                game.caches.emit(caches)
                            }
                            val fileResult = cache.downloadFromUrl(downloadUrl).getOrNull()
                            if (fileResult != null) {
                                val caches = game.caches.value.toMutableMap()
                                caches[cache] = CacheInfo.Download(fileResult)
                                game.caches.emit(caches)
                            }
                        }
                    }
                } else if (updateInfo.isMerge) {
                    coroutineScope.launch(Dispatchers.IO) {
                        val downloadedFile = (info as? CacheInfo.Download?)?.file
                        if (downloadedFile != null) {
                            val parsedCache = DxvkStateCache.fromFile(downloadedFile).getOrNull()
                            if (parsedCache != null) {
                                val newCache = cache.combine(parsedCache).getOrNull()
                                val finished = newCache?.writeTo(newCache.file!!, true)?.isSuccess
                                val caches = game.caches.value.toMutableMap()
                                caches[cache] = CacheInfo.Merged(finished ?: false)
                                game.caches.emit(caches)
                            }
                        }
                    }
                }
            }, modifier = Modifier.fillMaxWidth(), enabled = updateInfo.isDownload || updateInfo.isMerge) {
                Icon(updateInfo.icon, updateInfo.text, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = updateInfo.text)
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

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = StringRes.get().name
    ) {
        App()
    }
}