package dev.datlag.dxvkotool.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.onClick
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.datlag.dxvkotool.LocalSnackbarHost
import dev.datlag.dxvkotool.common.getBuildVersion
import dev.datlag.dxvkotool.common.openInBrowser
import dev.datlag.dxvkotool.common.showFromResult
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.StringRes

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun InfoDialog(isDialogOpen: MutableState<Boolean>) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHost = LocalSnackbarHost.current

    if (isDialogOpen.value) {
        AlertDialog(
            modifier = Modifier.defaultMinSize(300.dp),
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
                Column {
                    Text(
                        text = StringRes.get().fullAppDescription
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        text = StringRes.get().versionPlaceholder.format(getBuildVersion())
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Row {
                        Text(
                            text = StringRes.get().githubPlaceholder.format(String())
                        )
                        Text(
                            text = StringRes.get().name,
                            textDecoration = TextDecoration.Underline,
                            color = Color.Blue,
                            modifier = Modifier.onClick {
                                Constants.githubProjectLink.openInBrowser()
                            }
                        )
                    }
                }
            },
            dismissButton = {
                Button(onClick = {
                    isDialogOpen.value = false
                    val openDonateResult = runCatching {
                        Constants.githubSponsorLink.openInBrowser()
                    }
                    snackbarHost.showFromResult(coroutineScope, openDonateResult, String())
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
