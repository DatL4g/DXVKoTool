package dev.datlag.dxvkotool.ui.compose.game.cache.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.datlag.dxvkotool.common.deleteSafely
import dev.datlag.dxvkotool.common.withAlpha
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.StringRes
import java.io.File

@Composable
fun BackupRestoreDialog(
    cache: DxvkStateCache,
    isDialogOpen: MutableState<Boolean>,
    onSelected: (File?) -> Unit
) {
    val backupFiles by cache.backupFiles.collectAsState()
    val selectedItem: MutableState<File?> = mutableStateOf(null)

    if (backupFiles.isEmpty()) {
        isDialogOpen.value = false
    }

    if (isDialogOpen.value) {
        Dialog(onCloseRequest = {
            isDialogOpen.value = false
        }, title = StringRes.get().restoreBackupTitlePlaceholder.format(cache.file.name)) {
            Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(8.dp)) {
                LazyColumn(
                    modifier = Modifier.weight(1F)
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Text(
                                modifier = Modifier.weight(2F),
                                text = StringRes.get().backupDate
                            )
                            Text(
                                modifier = Modifier.weight(1F),
                                text = StringRes.get().size
                            )
                        }
                    }
                    items(backupFiles) {
                        BackupItem(it, selectedItem)
                    }
                }
                BackupRestoreDialogButtonRow(cache, isDialogOpen, selectedItem, onSelected)
            }
        }
    }
}
