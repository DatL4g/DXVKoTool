package dev.datlag.dxvkotool.ui.compose

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import dev.datlag.dxvkotool.io.GameIO
import dev.datlag.dxvkotool.model.GamePartition
import kotlinx.coroutines.flow.combine

@Composable
@Preview
fun GameList(
    selectedGameTypeIndex: MutableState<Int>
) {
    val gamesWithOnlineItem by GameIO.allGamesFlow.collectAsState(emptyList())
    combine(gamesWithOnlineItem.map { it.cacheInfoCollector }) {
        it
    }.collectAsState(emptyArray())
    val gamePartition by GameIO.allGamesPartitioned.collectAsState(GamePartition(
        emptyList(),
        emptyList(),
        emptyList()
    ))

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 500.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (selectedGameTypeIndex.value) {
            0 -> items(gamePartition.steamGames) {
                GameCard(it)
            }
            1 -> items(gamePartition.epicGames) {
                GameCard(it)
            }
            2 -> items(gamePartition.otherGames) {
                GameCard(it)
            }
        }
    }
}
