package dev.datlag.dxvkotool.network

import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.net.URI
import javax.imageio.ImageIO

object OnlineSteam {

    private val steamHeaderCDN = listOf(
        "https://cdn4.steampowered.com/v/gfx/apps/{appid}/header.jpg",
        "https://cdn.akamai.steamstatic.com/steam/apps/{appid}/header.jpg"
    )

    suspend fun getBitmapFromAppId(appId: String) = withContext(Dispatchers.IO) {
        var image: BufferedImage? = null
        val iterator = steamHeaderCDN.iterator()
        while (image == null && iterator.hasNext()) {
            val url = iterator.next().replace("{appid}", appId)
            val loaded = runCatching {
                ImageIO.read(URI.create(url).toURL())
            }
            image = loaded.getOrNull()
        }
        return@withContext image?.toComposeImageBitmap()
    }
}