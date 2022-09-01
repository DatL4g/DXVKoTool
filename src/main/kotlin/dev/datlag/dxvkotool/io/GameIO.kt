package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.model.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

object GameIO {

    // ToDo("combine with 'normal' games")
    val allGamesFlow: Flow<List<Game>> = combine(SteamIO.steamGamesFlow) { games: Array<List<Game>> ->
        games.toList().flatten()
    }.flowOn(Dispatchers.IO)
}