package dev.datlag.dxvkotool.ui.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import dev.datlag.dxvkotool.other.StringRes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InfoDialog(isDialogOpen: MutableState<Boolean>) {

    if (isDialogOpen.value) {
        AlertDialog(
            onDismissRequest = {
                isDialogOpen.value = false
            },
            title = {
                Text(
                    text = StringRes.get().name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = StringRes.get().fullAppDescription
                )
            },
            dismissButton = {
                Button(onClick = {
                    isDialogOpen.value = false
                }) {
                    Text(StringRes.get().donate)
                }
            },
            confirmButton = {
                Button(onClick = {
                    isDialogOpen.value = false
                }) {
                    Text(StringRes.get().close)
                }
            }
        )
    }
}