package dev.datlag.dxvkotool.ui.compose.fab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.CombinedLoadFileDialog
import dev.datlag.dxvkotool.ui.compose.game.cache.dialog.CacheInfoDialog
import java.io.File

@Composable
fun LoadCacheInfoFAB() {
    val infoFile: MutableState<File?> = remember { mutableStateOf(null) }
    var isLoadDialogOpen by remember { mutableStateOf(false) }
    val isInfoDialogOpen = remember { mutableStateOf(false) }

    if (isLoadDialogOpen) {
        CombinedLoadFileDialog(StringRes.get().loadLocalDxvkCacheFile, false) { loadFile ->
            infoFile.value = loadFile
            if (loadFile != null) {
                isInfoDialogOpen.value = true
            }
            isLoadDialogOpen = false
        }
    }

    CacheInfoDialog(infoFile, isInfoDialogOpen)
    FloatingActionButton(
        onClick = {
            isLoadDialogOpen = true
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        Icon(Icons.Outlined.Info, StringRes.get().info)
    }
}
