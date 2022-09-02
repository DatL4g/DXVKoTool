package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.AppManifest
import dev.datlag.dxvkotool.model.CacheInfo
import dev.datlag.dxvkotool.model.Game
import dev.datlag.dxvkotool.other.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

object SteamIO {
    fun reload(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        defaultAcfFlow.emit(getDefaultAcf())
        flatpakAcfFlow.emit(getFlatpakAcf())
    }

    private fun getDefaultAcf() = (File(System.getProperty("user.home"), Constants.STEAM_DEFAULT_ROOT).listFiles() ?: emptyArray()).filter {
        it.extension.equals("acf", true)
    }

    private fun getFlatpakAcf() = (File(System.getProperty("user.home"), Constants.STEAM_FLATPAK_ROOT).listFiles() ?: emptyArray()).filter {
        it.extension.equals("acf", true)
    }

    val defaultAcfFlow: MutableStateFlow<List<File>> = MutableStateFlow(emptyList())

    val flatpakAcfFlow: MutableStateFlow<List<File>> = MutableStateFlow(emptyList())

    val acfFlow = combine(defaultAcfFlow, flatpakAcfFlow) { t1, t2 ->
        setOf(*t1.toTypedArray(), *t2.toTypedArray()).toList()
    }.flowOn(Dispatchers.IO)

    val appManifestFlow: Flow<List<AppManifest>> = acfFlow.transform { acfFiles ->
        return@transform emit(coroutineScope { acfFiles.map { async {
            Acf.toAppManifest(it.readText(), it.parentFile?.absolutePath ?: it.absolutePath).getOrNull()
        } } }.awaitAll().filterNotNull())
    }.flowOn(Dispatchers.IO)

    val defaultShaderCacheFoldersFlow = flow<List<File>> {
        emit((File(System.getProperty("user.home"), Constants.STEAM_SHADER_DEFAULT_ROOT).listFiles() ?: emptyArray()).filter {
            it.isDirectory
        })
    }.flowOn(Dispatchers.IO)

    val flatpakShaderCacheFoldersFlow = flow<List<File>> {
        emit((File(System.getProperty("user.home"), Constants.STEAM_SHADER_FLATPAK_ROOT).listFiles() ?: emptyArray()).filter {
            it.isDirectory
        })
    }.flowOn(Dispatchers.IO)

    val shaderCacheFoldersFlow = combine(defaultShaderCacheFoldersFlow, flatpakShaderCacheFoldersFlow) { t1, t2 ->
        setOf(*t1.toTypedArray(), *t2.toTypedArray()).toList()
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
                    val caches = getDxvkStateCaches(it.value)
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

    suspend fun getDxvkStateCaches(file: File): List<DxvkStateCache> = coroutineScope {
        file.walkTopDown().map {
            async(Dispatchers.IO) {
                if (it.isFile && it.extension.equals("dxvk-cache", true)) {
                    DxvkStateCache.fromFile(it).getOrNull()
                } else {
                    null
                }
            }
        }.toList().awaitAll().filterNotNull()
    }
}