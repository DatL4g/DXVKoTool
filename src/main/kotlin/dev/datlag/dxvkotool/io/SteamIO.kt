package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.common.canReadSafely
import dev.datlag.dxvkotool.common.existsSafely
import dev.datlag.dxvkotool.common.isDirectorySafely
import dev.datlag.dxvkotool.common.listFilesSafely
import dev.datlag.dxvkotool.common.listFrom
import dev.datlag.dxvkotool.common.normalize
import dev.datlag.dxvkotool.model.AppManifest
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.other.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object SteamIO {
    fun reload(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        systemSteamAppsFoldersFlow.emit(getSystemAcf())
        flatpakSteamAppsFoldersFlow.emit(getFlatpakAcf())
    }

    private fun getSystemAcf(): List<File> {
        val systemSteamFolders = listOf(
            File(Constants.userDir, Constants.STEAM_DEFAULT_ROOT),
            File(Constants.userDir, Constants.STEAM_SYMLINK_ROOT)
        )

        return getSteamAppsFolders(systemSteamFolders)
    }

    private fun getFlatpakAcf(): List<File> {
        val flatpakSteamFolders = listOf(
            File(Constants.userDir, Constants.STEAM_FLATPAK_ROOT),
            File(Constants.userDir, Constants.STEAM_FLATPAK_SYMLINK_ROOT),
        )

        return getSteamAppsFolders(flatpakSteamFolders)
    }

    private fun getSteamAppsFolders(list: Collection<File>): List<File> {
        val steamAppsFolder: MutableList<File> = mutableListOf()

        list.forEach {
            steamAppsFolder.add(File(it, "steamapps/"))
            steamAppsFolder.addAll(listOf(
                File(it, "libraryfolders.vdf"),
                File(it, "steamapps/libraryfolders.vdf"),
                File(it, "config/libraryfolders.vdf")
            ).filter { file -> file.existsSafely() && file.canReadSafely() }.flatMap { file ->
                Acf.toLibraryConfigs(file.readText())
            }.map { config -> config.path }.toSet().map { path ->
                File(path, "steamapps/")
            })
        }

        return steamAppsFolder.normalize()
    }

    private val systemSteamAppsFoldersFlow: MutableStateFlow<List<File>> = MutableStateFlow(emptyList())
    private val flatpakSteamAppsFoldersFlow: MutableStateFlow<List<File>> = MutableStateFlow(emptyList())
    private val steamAppsFoldersFlow = combine(systemSteamAppsFoldersFlow, flatpakSteamAppsFoldersFlow) { t1, t2 ->
        listFrom(t1, t2).normalize()
    }.flowOn(Dispatchers.IO)

    private val acfFlow = steamAppsFoldersFlow.transform { list ->
        val acfList = list.flatMap { file ->
            file.listFilesSafely().filter {
                it.extension.equals("acf", true) && it.canReadSafely()
            }
        }
        return@transform emit(acfList)
    }.flowOn(Dispatchers.IO)

    private val appManifestFlow: Flow<List<AppManifest>> = acfFlow.transform { acfFiles ->
        val configList = withContext(Dispatchers.IO) {
            acfFiles.map {
                async {
                    Acf.toAppManifest(it.readText(), it.parentFile?.absolutePath ?: it.absolutePath).getOrNull()
                }
            }
        }.awaitAll().filterNotNull()
        return@transform emit(configList)
    }.flowOn(Dispatchers.IO)

    private val shaderCacheFoldersFlow: Flow<List<File>> = steamAppsFoldersFlow.transform { list ->
        return@transform emit(list.flatMap { file ->
            File(file, "shadercache/").listFilesSafely().filter {
                it.isDirectorySafely()
            }
        })
    }.flowOn(Dispatchers.IO)

    val steamGamesFlow = combine(appManifestFlow, shaderCacheFoldersFlow) { t1, t2 ->
        val foldersWithManifest = t2.associateWith { file ->
            t1.firstOrNull {
                it.appId.equals(file.name, true)
            }
        }
        val manifestsWithFolder = t1.associateWith { manifest ->
            t2.firstOrNull {
                it.name.equals(manifest.appId, true)
            }
        }
        val allAssociations: MutableMap<AppManifest, File> = mutableMapOf()

        foldersWithManifest.forEach { (t, u) ->
            if (u != null) {
                allAssociations.putIfAbsent(u, t)
            }
        }

        manifestsWithFolder.forEach { (t, u) ->
            if (u != null) {
                allAssociations.putIfAbsent(t, u)
            }
        }

        coroutineScope {
            allAssociations.map {
                async(Dispatchers.IO) {
                    val caches = GameIO.getDxvkStateCaches(it.value)
                    if (caches.isEmpty()) {
                        null
                    } else {
                        Game.Steam(
                            it.key,
                            it.value,
                            MutableStateFlow(caches)
                        )
                    }
                }
            }.awaitAll().filterNotNull()
        }
    }.flowOn(Dispatchers.IO)
}
