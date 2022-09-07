package dev.datlag.dxvkotool.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import mdlaf.MaterialLookAndFeel
import mdlaf.themes.MaterialLiteTheme
import mdlaf.themes.MaterialOceanicTheme
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.UIManager


private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,

    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,

    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    // unable to detect Adwaita dark/light theme
    val isDark = true

    MaterialTheme(
        colorScheme = DarkColors
    ) {
        androidx.compose.material.MaterialTheme(
            colors = MaterialTheme.colorScheme.toLegacyColors(isDark),
            shapes = MaterialTheme.shapes.toLegacyShapes()
        ) {
            try {
                val theme = MaterialLookAndFeel(MaterialOceanicTheme())
                UIManager.setLookAndFeel(theme)
            } catch (ignored: Throwable) { }
            content()
        }
    }
}