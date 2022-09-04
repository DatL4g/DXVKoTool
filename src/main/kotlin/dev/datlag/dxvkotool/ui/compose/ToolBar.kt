package dev.datlag.dxvkotool.ui.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import dev.datlag.dxvkotool.io.SteamIO
import dev.datlag.dxvkotool.other.StringRes

@Composable
@Preview
fun ToolBar() {
    val coroutineScope = rememberCoroutineScope()

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
            val openDialog = remember { mutableStateOf(false)  }
            IconButton(onClick = {
                openDialog.value = true
            }) {
                Icon(
                    Icons.Filled.Info,
                    StringRes.get().info,
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
            IconButton(onClick = {
                SteamIO.reload(coroutineScope)
            }) {
                Icon(
                    Icons.Filled.Refresh,
                    StringRes.get().reload,
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    )
}