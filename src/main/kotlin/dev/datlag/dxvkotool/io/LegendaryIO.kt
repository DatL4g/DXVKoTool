package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.common.existsSafely
import dev.datlag.dxvkotool.common.listFrom
import dev.datlag.dxvkotool.model.LegendaryGame
import dev.datlag.dxvkotool.other.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.jsonObject
import java.io.File

object LegendaryIO {

    val systemLegendaryGamesFlow: MutableStateFlow<List<LegendaryGame>> = MutableStateFlow(emptyList())
    val heroicFlatpakLegendaryGamesFlow: MutableStateFlow<List<LegendaryGame>> = MutableStateFlow(emptyList())

    val legendaryGamesFlow = combine(systemLegendaryGamesFlow, heroicFlatpakLegendaryGamesFlow) { t1, t2 ->
        listFrom(t1, t2)
    }.flowOn(Dispatchers.IO)

    fun reload(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        systemLegendaryGamesFlow.emit(getSystemLegendaryInstalled())
        heroicFlatpakLegendaryGamesFlow.emit(getHeroicFlatpakLegendaryInstalled())
    }

    fun addGamesToDB(scope: CoroutineScope) {
        legendaryGamesFlow.onEach { list ->
            list.forEach {
                GameIO.addLegendaryGame(it)
            }
        }.launchIn(scope)
    }

    private val systemLegendaryDir = File(Constants.userDir, Constants.SYSTEM_DEFAULT_LEGENDARY)
    private val heroicFlatpakLegendaryDir = File(Constants.userDir, Constants.HEROIC_FLATPAK_LEGENDARY)

    private suspend fun getSystemLegendaryInstalled(): List<LegendaryGame> {
        val installedJson = File(systemLegendaryDir, "installed.json")
        return getGamesFromJsonFile(installedJson)
    }

    private suspend fun getHeroicFlatpakLegendaryInstalled(): List<LegendaryGame> {
        val installedJson = File(heroicFlatpakLegendaryDir, "installed.json")
        return getGamesFromJsonFile(installedJson)
    }

    private suspend fun getGamesFromJsonFile(file: File): List<LegendaryGame> = withContext(Dispatchers.IO) {
        val gameList: MutableList<LegendaryGame> = mutableListOf()
        if (file.existsSafely()) {
            file.inputStream().use {
                val jsonObject: JsonObject? = runCatching {
                    Constants.json.decodeFromStream<JsonElement?>(it)
                }.getOrNull()?.jsonObject

                val allEntries = jsonObject?.values?.mapNotNull {
                    runCatching {
                        it.jsonObject
                    }.getOrNull()
                }

                gameList.addAll(
                    allEntries?.mapNotNull {
                        runCatching {
                            Constants.json.decodeFromJsonElement<LegendaryGame>(it)
                        }.getOrNull()
                    } ?: emptyList()
                )
            }
        }
        return@withContext gameList
    }
}
