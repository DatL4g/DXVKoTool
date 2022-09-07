package dev.datlag.dxvkotool

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.dxvkotool.io.SteamIO
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.CacheInfoDialog
import dev.datlag.dxvkotool.ui.compose.CombinedLoadFileDialog
import dev.datlag.dxvkotool.ui.compose.GameList
import dev.datlag.dxvkotool.ui.compose.ToolBar
import dev.datlag.dxvkotool.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

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
                    val infoFile: MutableState<File?> = remember { mutableStateOf(null) }
                    val isInfoDialogOpen = remember { mutableStateOf(false) }
                    var isLoadDialogOpen by remember { mutableStateOf(false) }

                    if (isLoadDialogOpen) {
                        CombinedLoadFileDialog { loadFile ->
                            infoFile.value = loadFile
                            if (loadFile != null) {
                                isInfoDialogOpen.value = true
                            }
                            isLoadDialogOpen = false
                        }
                    }

                    CacheInfoDialog(infoFile, isInfoDialogOpen)
                    Column {
                        FloatingActionButton(
                            onClick = {
                                isLoadDialogOpen = true
                            },
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ) {
                            Icon(Icons.Outlined.Info, StringRes.get().info)
                        }
                        Spacer(modifier = Modifier.padding(4.dp))
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