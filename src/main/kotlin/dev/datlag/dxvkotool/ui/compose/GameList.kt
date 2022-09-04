package dev.datlag.dxvkotool.ui.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.datlag.dxvkotool.common.header
import dev.datlag.dxvkotool.io.GameIO
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.other.StringRes

@Composable
@Preview
fun GameList() {
    val coroutineScope = rememberCoroutineScope()
    val gamesWithOnlineItem by GameIO.allGamesFlow.collectAsState(emptyList())
    gamesWithOnlineItem.onEach {
        it.loadCacheInfo(coroutineScope)
    }
    val (steamGames, otherGames) = gamesWithOnlineItem.partition { it is Game.Steam }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 500.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        header {
            Text(
                text = StringRes.get().steamGames,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(steamGames) {
            GameCard(it)
        }

        header {
            Text(
                text = StringRes.get().otherGames,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        items(otherGames) {
            GameCard(it)
        }
    }
}