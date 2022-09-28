package dev.datlag.dxvkotool.ui.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.LocalSnackbarHost
import dev.datlag.dxvkotool.common.showFromResult
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.Game
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
    val snackbarHost = LocalSnackbarHost.current
    val updateInfo = info.toButtonInfo(cache)
    val backupFiles by cache.backupFiles.collectAsState()
    val isBackupOpen = remember { mutableStateOf(false) }

    if (isExportOpen) {
        CombinedSaveFileDialog(cache.file.name) { destFile ->
            isExportOpen = false
            if (destFile != null) {
                coroutineScope.launch(Dispatchers.IO) {
                    val exportResult = cache.writeTo(destFile, false)
                    snackbarHost.showFromResult(exportResult, StringRes.get().exportSuccessful)
                }
            }
        }
    }

    BackupRestoreDialog(cache, isBackupOpen) { restoreFile ->
        if (restoreFile != null) {
            coroutineScope.launch(Dispatchers.IO) {
                val restoreResult = game.restoreBackup(cache, restoreFile)
                snackbarHost.showFromResult(restoreResult, StringRes.get().restoreSuccessful)
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
                Button(
                    onClick = {
                        if (updateInfo.isDownload) {
                            coroutineScope.launch(Dispatchers.IO) {
                                val downloadResult = cache.downloadCache()
                                snackbarHost.showFromResult(downloadResult, String())
                            }
                        } else if (updateInfo.isMerge) {
                            coroutineScope.launch(Dispatchers.IO) {
                                val mergeResult = game.mergeCache(cache)
                                snackbarHost.showFromResult(mergeResult, String())
                            }
                        }
                    },
                    modifier = Modifier.weight(1F),
                    enabled = updateInfo.isDownload || updateInfo.isMerge,
                    shape = Shape.LeftRoundedShape
                ) {
                    Icon(updateInfo.icon, updateInfo.text, modifier = Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = updateInfo.text,
                        maxLines = 1
                    )
                }
                CacheDropdownMenu(game, cache, isMenuOpen)
                Spacer(modifier = Modifier.padding(2.dp))
                Button(onClick = {
                    isMenuOpen.value = true
                }, shape = Shape.RightRoundedShape) {
                    Icon(Icons.Filled.ExpandMore, StringRes.get().more, modifier = Modifier.size(ButtonDefaults.IconSize))
                }
            }
            Button(onClick = {
                cache.reloadBackupFiles(coroutineScope)
                isBackupOpen.value = true
            }, modifier = Modifier.fillMaxWidth(), enabled = backupFiles.isNotEmpty()) {
                Icon(
                    Icons.Filled.SettingsBackupRestore,
                    StringRes.get().restoreBackup,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(StringRes.get().restoreBackup)
            }
        }
    }
}
