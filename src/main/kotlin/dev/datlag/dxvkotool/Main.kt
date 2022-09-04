package dev.datlag.dxvkotool

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.dxvkotool.common.header
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.io.GameIO
import dev.datlag.dxvkotool.io.SteamIO
import dev.datlag.dxvkotool.model.CacheInfo
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.model.UpdateButtonInfo
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.DXVKException
import dev.datlag.dxvkotool.other.DownloadException
import dev.datlag.dxvkotool.other.ReadErrorType
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.*
import dev.datlag.dxvkotool.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.datlag.dxvkotool.ui.theme.Shape
import kotlinx.coroutines.CoroutineScope
import java.awt.Desktop

val LocalSnackbarHost = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    SteamIO.reload(coroutineScope)
    OnlineDXVK.getDXVKCaches(coroutineScope)

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    CompositionLocalProvider(LocalSnackbarHost provides scaffoldState.snackbarHostState) {
        AppTheme {
            Scaffold(
                topBar = {
                    ToolBar()
                },
                scaffoldState = scaffoldState,
                floatingActionButton = {
                    val snackbarHost = LocalSnackbarHost.current

                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch(Dispatchers.IO) {
                                snackbarHost.showSnackbar("Adding your own games is not implemented yet")
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ) {
                        Icon(Icons.Filled.Add, StringRes.get().add)
                    }
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) {
                    GameList()
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = StringRes.get().name
    ) {
        App()
    }
}