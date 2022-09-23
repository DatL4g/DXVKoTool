package dev.datlag.dxvkotool.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.datlag.dxvkotool.common.*
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.StringRes
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.io.File
import kotlin.coroutines.coroutineContext

@Composable
fun BackupRestoreDialog(cache: DxvkStateCache, isDialogOpen: MutableState<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
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
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextButton(modifier= Modifier, onClick = {
                        val deleted = selectedItem.value?.deleteSafely() == true
                        if (deleted) {
                            selectedItem.value = null
                            cache.reloadBackupFiles(coroutineScope)
                        }
                    }) {
                        Text(
                            text = StringRes.get().delete,
                            color = if (selectedItem.value != null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.withAlpha(0.5F)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    TextButton(onClick = {
                        isDialogOpen.value = false
                    }) {
                        Text(
                            text = StringRes.get().cancel,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    TextButton(onClick = {
                        isDialogOpen.value = false
                    }, enabled = selectedItem.value != null) {
                        Text(
                            text = StringRes.get().restore,
                            color = if (selectedItem.value != null) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.withAlpha(0.5F)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BackupItem(file: File, selected: MutableState<File?>) {
    val lastModified = Instant.fromEpochMilliseconds(file.getLastModifiedOrCreated())
    val lastModifiedDate = lastModified.toLocalDateTime(TimeZone.currentSystemDefault())
    val isSelected = selected.value == file

    Row(modifier = Modifier.fillMaxWidth().onClick {
        selected.value = file
    }.background(if (isSelected) MaterialTheme.colorScheme.onBackground.withAlpha(0.5F) else MaterialTheme.colorScheme.background)) {
        Text(
            modifier = Modifier.weight(2F),
            text = lastModifiedDate.toJavaLocalDateTime().format(Constants.defaultDateFormatter)
        )
        Text(
            modifier = Modifier.weight(1F),
            text = file.sizeSafely().toHumanReadableBytes()
        )
    }
}