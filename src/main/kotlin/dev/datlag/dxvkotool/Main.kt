package dev.datlag.dxvkotool

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.singleWindowApplication
import app.softwork.routingcompose.DesktopRouter
import dev.datlag.dxvkotool.io.AppIO
import dev.datlag.dxvkotool.other.Routing
import dev.datlag.dxvkotool.other.StringRes
import dev.datlag.dxvkotool.ui.compose.App
import dev.datlag.dxvkotool.ui.compose.settings.Settings
import dev.datlag.dxvkotool.ui.theme.AppTheme

val LocalSnackbarHost = compositionLocalOf<SnackbarHostState> { error("No SnackbarHostState provided") }
val LocalWindow = compositionLocalOf<ComposeWindow> { error("No Window provided") }

fun main() = singleWindowApplication(
    title = StringRes.get().name
) {
    AppIO.loadAppIcon(this.window, rememberCoroutineScope())
    CompositionLocalProvider(LocalWindow provides this.window) {
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
}
