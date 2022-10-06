package dev.datlag.dxvkotool.io

import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.res.useResource
import dev.datlag.dxvkotool.common.mapAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Image
import javax.swing.ImageIcon

object AppIO {

    private val appIcons = listOf(
        "AppIcon20",
        "AppIcon32",
        "AppIcon36",
        "AppIcon48",
        "AppIcon64",
        "AppIcon96",
        "AppIcon128",
    ).flatMap {
        listOf("$it.png", "$it.ico")
    }.toMutableList().apply {
        add("AppIcon.icns")
    }.toList()

    fun loadAppIcon(window: ComposeWindow, scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        val appIcons = getResImages(this@AppIO.appIcons)

        withContext(Dispatchers.Main) {
            window.iconImages = appIcons
        }
    }

    private suspend fun getResImages(list: Collection<String>): List<Image> {
        return list.mapAsync { getResImage(it) }.filterNotNull()
    }

    private suspend fun getResImage(res: String): Image? = runCatching {
        useResource(res) { ImageIcon(it.readAllBytes()).image }
    }.getOrNull()
}
