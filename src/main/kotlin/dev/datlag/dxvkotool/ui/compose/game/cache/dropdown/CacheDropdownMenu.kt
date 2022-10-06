package dev.datlag.dxvkotool.ui.compose.game.cache.dropdown

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.LocalSnackbarHost
import dev.datlag.dxvkotool.common.showFromResult
import dev.datlag.dxvkotool.db.DB
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.game.Game
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.CombinedLoadFileDialog
import java.awt.Desktop

@Composable
fun CacheDropdownMenu(
    game: Game,
    cache: DxvkStateCache,
    isMenuOpen: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHost = LocalSnackbarHost.current
    val isConnectDialogOpen = remember { mutableStateOf(false) }

    var isLoadLocalFileOpen by remember { mutableStateOf(false) }

    if (isLoadLocalFileOpen) {
        CombinedLoadFileDialog(StringRes.get().mergeLocalDxvkCacheFile, false) { loadFile ->
            isLoadLocalFileOpen = false
            if (loadFile != null) {
                cache.loadLocalFile(coroutineScope, loadFile)
            }
        }
    }

    ConnectDialog(isConnectDialogOpen) {
        if (it != null && game is Game.Steam) {
            DB.database.steamGameQueries.insert(game.manifest.appId.toLong(), cache.file.name, it.item.path)
        }
    }

    DropdownMenu(
        expanded = isMenuOpen.value,
        onDismissRequest = { isMenuOpen.value = false }
    ) {
        DropdownMenuItem(onClick = {
            isMenuOpen.value = false
            isLoadLocalFileOpen = true
        }, enabled = true) {
            Icon(Icons.Filled.InsertDriveFile, StringRes.get().mergeLocalFile)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(StringRes.get().mergeLocalFile)
        }
        DropdownMenuItem(onClick = {
            isMenuOpen.value = false
            isConnectDialogOpen.value = true
        }, enabled = true) {
            Icon(Icons.Filled.Link, StringRes.get().connectRepoItem)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(StringRes.get().connectRepoItem)
        }
        DropdownMenuItem(onClick = {
            isMenuOpen.value = false
            val openFolderResult = runCatching {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(cache.file.parentFile)
                } else {
                    throw IllegalStateException(StringRes.get().unsupportedSystem)
                }
            }
            snackbarHost.showFromResult(coroutineScope, openFolderResult, String())
        }, enabled = true) {
            Icon(Icons.Filled.Folder, StringRes.get().openFolder)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(StringRes.get().openFolder)
        }
    }
}
