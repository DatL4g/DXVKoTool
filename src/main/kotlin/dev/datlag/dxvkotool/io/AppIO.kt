package dev.datlag.dxvkotool.io

import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.res.useResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Image
import javax.swing.ImageIcon

object AppIO {

    fun loadAppIcon(window: ComposeWindow, scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        val appIcons = listOfNotNull(
            getResImage("AppIcon20.png"),
            getResImage("AppIcon32.png"),
            getResImage("AppIcon36.png"),
            getResImage("AppIcon48.png"),
            getResImage("AppIcon64.png"),
            getResImage("AppIcon128.png")
        )

        withContext(Dispatchers.Main) {
            window.iconImages = appIcons
        }
    }

    private suspend fun getResImage(res: String): Image? = runCatching {
        useResource(res) { ImageIcon(it.readAllBytes()).image }
    }.getOrNull()
}
