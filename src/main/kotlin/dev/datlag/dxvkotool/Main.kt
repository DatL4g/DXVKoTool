package dev.datlag.dxvkotool

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.singleWindowApplication
import app.softwork.routingcompose.DesktopRouter
import dev.datlag.dxvkotool.io.AppIO
import dev.datlag.dxvkotool.io.LegendaryIO
import dev.datlag.dxvkotool.io.SteamIO
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.Routing
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.fab.FABContainer
import dev.datlag.dxvkotool.ui.compose.game.GameList
import dev.datlag.dxvkotool.ui.compose.settings.Settings
import dev.datlag.dxvkotool.ui.compose.toolbar.ToolBar
import dev.datlag.dxvkotool.ui.theme.AppTheme

val LocalSnackbarHost = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }

@Composable
@Preview
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val selectedGameTypeIndex = remember { mutableStateOf(0) }

    SteamIO.reload(coroutineScope)
    OnlineDXVK.getDXVKCaches(coroutineScope)
    LegendaryIO.reload(coroutineScope)
    LegendaryIO.addGamesToDB(coroutineScope)

    CompositionLocalProvider(LocalSnackbarHost provides scaffoldState.snackbarHostState) {
        Scaffold(
            topBar = {
                ToolBar(selectedGameTypeIndex)
            },
            scaffoldState = scaffoldState,
            floatingActionButton = {
                FABContainer()
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                GameList(selectedGameTypeIndex)
            }
        }
    }
}

fun main() = singleWindowApplication(
    title = StringRes.get().name
) {
    AppIO.loadAppIcon(this.window, rememberCoroutineScope())
    AppTheme {
        DesktopRouter("/") {
            route(Routing.SETTINGS) {
                Settings()
            }
            noMatch {
                App()
            }
        }
    }
}
