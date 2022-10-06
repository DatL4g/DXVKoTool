package dev.datlag.dxvkotool.ui.compose.toolbar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.datlag.dxvkotool.io.LegendaryIO
import dev.datlag.dxvkotool.io.SteamIO
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.InfoDialog

@Composable
@Preview
fun ToolBar(
    selectedIndex: MutableState<Int>
) {
    val coroutineScope = rememberCoroutineScope()
    val isDialogOpen = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Text(
                    text = StringRes.get().name,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontWeight = FontWeight.Bold
                )
            },
            backgroundColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
            actions = {
                InfoDialog(isDialogOpen)
                IconButton(onClick = {
                    isDialogOpen.value = true
                }) {
                    Icon(
                        Icons.Filled.Info,
                        StringRes.get().info,
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
                IconButton(onClick = {
                    SteamIO.reload(coroutineScope)
                    LegendaryIO.reload(coroutineScope)
                }) {
                    Icon(
                        Icons.Filled.Refresh,
                        StringRes.get().reload,
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        )
        ToolbarTabs(selectedIndex)
    }

}
