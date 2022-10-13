package dev.datlag.dxvkotool.ui.compose.toolbar

import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigateBack
import dev.datlag.dxvkotool.other.StringRes

@Composable
fun SettingsToolBar() {
    val router = Router.current

    TopAppBar(
        title = {
            Text(
                text = StringRes.get().settings,
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.Bold
            )
        },
        backgroundColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        navigationIcon = {
            IconButton(onClick = {
                router.navigateBack()
            }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = StringRes.get().back,
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
        }
    )
}
