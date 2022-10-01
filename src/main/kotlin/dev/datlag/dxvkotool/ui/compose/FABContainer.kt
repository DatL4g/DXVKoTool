package dev.datlag.dxvkotool.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.io.GameIO
import dev.datlag.dxvkotool.other.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun FABContainer() {
    val coroutineScope = rememberCoroutineScope()
    val infoFile: MutableState<File?> = remember { mutableStateOf(null) }
    val isInfoDialogOpen = remember { mutableStateOf(false) }
    var isLoadDialogOpen by remember { mutableStateOf(false) }
    var isAddDialogOpen by remember { mutableStateOf(false) }

    if (isLoadDialogOpen) {
        CombinedLoadFileDialog(false) { loadFile ->
            infoFile.value = loadFile
            if (loadFile != null) {
                isInfoDialogOpen.value = true
            }
            isLoadDialogOpen = false
        }
    }

    if (isAddDialogOpen) {
        CombinedLoadFileDialog(true) { installPathFile ->
            if (installPathFile != null) coroutineScope.launch(Dispatchers.IO) {
                GameIO.addGameFromPath(installPathFile)
            }
            isAddDialogOpen = false
        }
    }

    CacheInfoDialog(infoFile, isInfoDialogOpen)
    Column {
        FloatingActionButton(
            onClick = {
                isLoadDialogOpen = true
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Icon(Icons.Outlined.Info, StringRes.get().info)
        }
        Spacer(modifier = Modifier.padding(4.dp))
        FloatingActionButton(
            onClick = {
                isAddDialogOpen = true
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Icon(Icons.Filled.Add, StringRes.get().add)
        }
    }
}
