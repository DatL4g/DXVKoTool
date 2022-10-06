package dev.datlag.dxvkotool.ui.compose.game.cache

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.LocalSnackbarHost
import dev.datlag.dxvkotool.common.showFromResult
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.game.Game
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.game.cache.dropdown.CacheDropdownMenu
import dev.datlag.dxvkotool.ui.theme.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GameCacheButtons(
    game: Game,
    cache: DxvkStateCache,
    isExportOpen: MutableState<Boolean>,
    isBackupOpen: MutableState<Boolean>,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHost = LocalSnackbarHost.current

    val isMenuOpen = remember { mutableStateOf(false) }
    val info by cache.info.collectAsState()
    val updateInfo = info.toButtonInfo(cache)
    val backupFiles by cache.backupFiles.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            isExportOpen.value = true
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
