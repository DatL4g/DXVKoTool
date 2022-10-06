package dev.datlag.dxvkotool.ui.compose.game.cache.backup

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import dev.datlag.dxvkotool.common.deleteSafely
import dev.datlag.dxvkotool.common.withAlpha
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.StringRes
import java.io.File

@Composable
fun BackupRestoreDialogButtonRow(
    cache: DxvkStateCache,
    isDialogOpen: MutableState<Boolean>,
    selectedItem: MutableState<File?>,
    onSelected: (File?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row(modifier = Modifier.fillMaxWidth()) {
        TextButton(modifier = Modifier, onClick = {
            val deleted = selectedItem.value?.deleteSafely() == true
            if (deleted) {
                selectedItem.value = null
                cache.reloadBackupFiles(coroutineScope)
            }
        }) {
            Text(
                text = StringRes.get().delete,
                color = if (selectedItem.value != null) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onBackground.withAlpha(Constants.HALF_ALPHA_NUMBER)
                }
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
            onSelected.invoke(selectedItem.value)
            isDialogOpen.value = false
        }, enabled = selectedItem.value != null) {
            Text(
                text = StringRes.get().restore,
                color = if (selectedItem.value != null) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onBackground.withAlpha(Constants.HALF_ALPHA_NUMBER)
                }
            )
        }
    }
}
