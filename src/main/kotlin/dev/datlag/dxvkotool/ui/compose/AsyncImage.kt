package dev.datlag.dxvkotool.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.network.OnlineSteam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AsyncImage(appId: String, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val bitmap: MutableState<ImageBitmap?> = remember { mutableStateOf(null) }

    coroutineScope.launch(Dispatchers.IO) {
        bitmap.value = OnlineSteam.getBitmapFromAppId(appId)
    }

    bitmap.value?.let {
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