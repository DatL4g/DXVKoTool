package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.common.existsSafely
import dev.datlag.dxvkotool.common.isDirectorySafely
import dev.datlag.dxvkotool.common.isFileSafely
import dev.datlag.dxvkotool.db.DB
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File

object GameIO {

    val allGamesFlow: Flow<List<Game>> = combine(SteamIO.steamGamesFlow, DB.otherGames) { t1, t2 ->
        mutableListOf<Game>().apply {
            addAll(t1)
            addAll(t2)
        }
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

    suspend fun addGameFromPath(file: File) {
        if (file.existsSafely() && file.isDirectorySafely()) {
            DB.database.otherGameQueries.insertGame(file.absolutePath)
            getDxvkStateCaches(file).forEach { cache ->
                DB.database.otherGameQueries.insertGameCache(file.absolutePath, cache.file.absolutePath, null)
            }
        }
    }
}
