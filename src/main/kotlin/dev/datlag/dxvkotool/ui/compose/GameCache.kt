package dev.datlag.dxvkotool.ui.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.LocalSnackbarHost
import dev.datlag.dxvkotool.common.showFromResult
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.CacheInfo
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.model.UpdateButtonInfo
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.theme.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun GameCache(game: Game, cache: DxvkStateCache) {
    val coroutineScope = rememberCoroutineScope()
    var isExportOpen by remember { mutableStateOf(false) }
    val info by cache.info.collectAsState()
    val isMenuOpen = remember { mutableStateOf(false) }
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
                coroutineScope.launch(Dispatchers.IO) {
                    val exportResult = cache.writeTo(destFile, false)
                    snackbarHost.showFromResult(exportResult, StringRes.get().exportSuccessful)
                }
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
                text = StringRes.get().versionPlaceholder.format(cache.header.version.toString()),
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
                CacheDropdownMenu(cache, isMenuOpen)
                Spacer(modifier = Modifier.padding(2.dp))
                Button(onClick = {
                    isMenuOpen.value = true
                }, shape = Shape.RightRoundedShape) {
                    Icon(Icons.Filled.ExpandMore, StringRes.get().more, modifier = Modifier.size(ButtonDefaults.IconSize))
                }
            }
            Button(onClick = {

            }, modifier = Modifier.fillMaxWidth(), enabled = false) {
                Icon(
                    Icons.Filled.SettingsBackupRestore, StringRes.get().restoreBackup, modifier = Modifier.size(
                        ButtonDefaults.IconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(StringRes.get().restoreBackup)
            }
        }
    }
}