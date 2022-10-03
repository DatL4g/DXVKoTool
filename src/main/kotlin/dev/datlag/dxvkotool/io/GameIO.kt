package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.common.existsSafely
import dev.datlag.dxvkotool.common.isDirectorySafely
import dev.datlag.dxvkotool.common.isFileSafely
import dev.datlag.dxvkotool.common.listFrom
import dev.datlag.dxvkotool.db.DB
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.model.GamePartition
import dev.datlag.dxvkotool.model.LegendaryGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import java.io.File

object GameIO {

    private val allGamesFlow: Flow<List<Game>> = combine(SteamIO.steamGamesFlow, DB.otherGames) { t1, t2 ->
        listFrom(t1, t2)
    }.flowOn(Dispatchers.IO)

    val allGamesPartitioned: Flow<GamePartition> = allGamesFlow.transform { list ->
        val (steamGames, otherGamesFlat) = list.partition { it is Game.Steam }
        val (epicGames, otherGames) = otherGamesFlat.partition { (it as? Game.Other?)?.isEpicGame == true }

        emit(
            GamePartition(
                steamGames,
                epicGames,
                otherGames
            )
        )
    }.flowOn(Dispatchers.IO)

    suspend fun getDxvkStateCaches(file: File): List<DxvkStateCache> = withContext(Dispatchers.IO) {
        file.walkTopDown().map {
            async(Dispatchers.IO) {
                if (it.isFileSafely() && it.extension.equals("dxvk-cache", true)) {
                    DxvkStateCache.fromFile(it).getOrNull()
                } else {
                    null
                }
            }
        }.toList().awaitAll().filterNotNull()
    }

    suspend fun addGameFromPath(file: File) = withContext(Dispatchers.IO) {
        addGame(
            file,
            file.name,
            false
        )
    }

    suspend fun addLegendaryGame(legendaryGame: LegendaryGame) = withContext(Dispatchers.IO) {
        val gamePath = File(legendaryGame.installPath)
        addGame(
            gamePath,
            legendaryGame.title.ifEmpty {
                gamePath.name
            },
            true
        )
    }

    private suspend fun addGame(
        file: File,
        name: String,
        isEpic: Boolean
    ) {
        if (file.existsSafely() && file.isDirectorySafely()) {
            DB.database.otherGameQueries.insertGame(
                file.absolutePath,
                name,
                isEpic
            )
            getDxvkStateCaches(file).forEach { cache ->
                DB.database.otherGameQueries.insertGameCache(file.absolutePath, cache.file.absolutePath, null)
            }
        }
    }
}
