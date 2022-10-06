package dev.datlag.dxvkotool.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import dev.datlag.dxvkotool.other.Constants
import evalBash
import mdlaf.MaterialLookAndFeel
import mdlaf.themes.MaterialLiteTheme
import mdlaf.themes.MaterialOceanicTheme
import javax.swing.UIManager

val LocalDarkMode = compositionLocalOf<Boolean> { error("No dark mode state provided") }


@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isDark = useDarkTheme || getSystemDarkModeInfo()

    CompositionLocalProvider(LocalDarkMode provides isDark) {
        MaterialTheme(
            colorScheme = if (isDark) Colors.getDarkScheme() else Colors.getLightScheme()
        ) {
            androidx.compose.material.MaterialTheme(
                colors = MaterialTheme.colorScheme.toLegacyColors(isDark),
                shapes = MaterialTheme.shapes.toLegacyShapes()
            ) {
                try {
                    val theme = MaterialLookAndFeel(if (isDark) MaterialOceanicTheme() else MaterialLiteTheme())
                    UIManager.setLookAndFeel(theme)
                } catch (ignored: Throwable) {
                }
                content()
            }
        }
    }
}

fun getSystemDarkModeInfo(): Boolean {
    val linuxDarkMode = (Constants.LINUX_DARK_MODE_CMD.evalBash().getOrDefault(String())).ifEmpty {
        Constants.LINUX_DARK_MODE_LEGACY_CMD.evalBash().getOrDefault(String())
    }.contains("dark", true)

    return linuxDarkMode
}
