package dev.datlag.dxvkotool.db

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dev.datlag.DXVKoToolDB
import dev.datlag.sqldelight.db.SteamGame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

object DB {

    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
        DXVKoToolDB.Schema.create(it)
    }

    val database = DXVKoToolDB(driver)

    val steamGames: Flow<List<SteamGame>> = database.steamGameQueries.selectAll().asFlow().mapToList(Dispatchers.IO)

}