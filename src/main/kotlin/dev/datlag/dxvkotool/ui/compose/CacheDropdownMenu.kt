package dev.datlag.dxvkotool.ui.compose

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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.LocalSnackbarHost
import dev.datlag.dxvkotool.common.showFromResult
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.other.StringRes
import java.awt.Desktop

@Composable
fun CacheDropdownMenu(
    cache: DxvkStateCache,
    isMenuOpen: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHost = LocalSnackbarHost.current

    var isLoadLocalFileOpen by remember { mutableStateOf(false) }

    if (isLoadLocalFileOpen) {
        LoadFileDialog { loadFile ->
            isLoadLocalFileOpen = false
            if (loadFile != null) {
                cache.loadLocalFile(coroutineScope, loadFile)
            }
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
        }, enabled = false) {
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