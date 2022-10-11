package dev.datlag.dxvkotool.ui.compose.game.cache

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.LocalSnackbarHost
import dev.datlag.dxvkotool.common.showFromResult
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.game.Game
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.CombinedSaveFileDialog
import dev.datlag.dxvkotool.ui.compose.game.cache.backup.BackupRestoreDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun GameCache(game: Game, cache: DxvkStateCache) {
    val coroutineScope = rememberCoroutineScope()
    val isExportOpen = remember { mutableStateOf(false) }

    val snackbarHost = LocalSnackbarHost.current
    val isBackupOpen = remember { mutableStateOf(false) }

    if (isExportOpen.value) {
        CombinedSaveFileDialog(cache.file.name) { destFile ->
            isExportOpen.value = false
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
        Column(modifier = Modifier.fillMaxWidth(Constants.HALF_PARENT_FRACTION_NUMBER).fillMaxHeight()) {
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
        GameCacheButtons(game, cache, isExportOpen, isBackupOpen)
    }
}
