package dev.datlag.dxvkotool.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.network.OnlineSteam

@Composable
fun AsyncImage(appId: String, modifier: Modifier = Modifier) {
    val image: ImageBitmap? by produceState<ImageBitmap?>(null) {
        value = OnlineSteam.getBitmapFromAppId(appId)
    }

    image?.let {
        Image(
            bitmap = it, null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}

@Composable
fun AsyncImage(game: Game.Steam, modifier: Modifier = Modifier) = AsyncImage(game.manifest.appId, modifier)

@Composable
fun AsyncImage(game: Game, modifier: Modifier = Modifier) {
    if (game is Game.Steam) {
        AsyncImage(game, modifier)
    }
}
