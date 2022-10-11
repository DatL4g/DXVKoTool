package dev.datlag.dxvkotool.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.datlag.DXVKoToolDB
import dev.datlag.dxvkotool.common.existsSafely
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.game.Game
import dev.datlag.sqldelight.db.SelectGamesWithCaches
import dev.datlag.sqldelight.db.SteamGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transformLatest
import java.io.File

object DB {

    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        DXVKoToolDB.Schema.create(it)
    }

    val database = DXVKoToolDB(driver)

    val steamGames: Flow<List<SteamGame>> = database.steamGameQueries.selectAll().asFlow().mapToList(Dispatchers.IO)

    val otherGamesFlat: Flow<List<SelectGamesWithCaches>> =
        database.otherGameQueries.selectGamesWithCaches().asFlow().mapToList(Dispatchers.IO)

    val otherGames = otherGamesFlat.transformLatest { games ->
        val gameList = mutableListOf<Game.Other>()

        games.forEach { game ->
            val gamePath = File(game.installPath)
            if (!gamePath.existsSafely()) {
                return@forEach
            }
            val existingGame = gameList.firstOrNull {
                it.path == gamePath || it.path.absolutePath.equals(gamePath.absolutePath, true)
            }

            if (existingGame != null) {
                val cacheList = existingGame.caches.value.toMutableList()
                DxvkStateCache.fromFile(File(game.cachePath)).getOrNull()?.let {
                    cacheList.add(it)
                }
                existingGame.caches.emit(cacheList)
            } else {
                val cache = DxvkStateCache.fromFile(File(game.cachePath)).getOrNull()
                cache?.associatedRepoItem?.emit(game.repoItem)
                val newGame = Game.Other(
                    game.name,
                    gamePath,
                    game.isEpic,
                    MutableStateFlow(listOfNotNull(cache))
                )
                gameList.add(newGame)
            }
        }

        return@transformLatest emit(gameList.toList())
    }.flowOn(Dispatchers.IO)

}
