package dev.datlag.dxvkotool.ui.compose.fab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import dev.datlag.dxvkotool.io.GameIO
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.CombinedLoadFileDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AddGameFAB() {
    val coroutineScope = rememberCoroutineScope()
    var isAddDialogOpen by remember { mutableStateOf(false) }

    if (isAddDialogOpen) {
        CombinedLoadFileDialog(StringRes.get().selectGameFolder, true) { installPathFile ->
            if (installPathFile != null) coroutineScope.launch(Dispatchers.IO) {
                GameIO.addGameFromPath(installPathFile)
            }
            isAddDialogOpen = false
        }
    }

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
