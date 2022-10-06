package dev.datlag.dxvkotool.model.game

import dev.datlag.dxvkotool.common.createBackup
import dev.datlag.dxvkotool.common.runSuspendCatching
import dev.datlag.dxvkotool.db.DB
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.game.cache.CacheInfo
import dev.datlag.dxvkotool.model.game.steam.AppManifest
import dev.datlag.dxvkotool.model.github.findMatchingGameItem
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.MergeException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import java.io.File

sealed class Game(
    open val name: String,
    open val path: File,
    open val caches: MutableStateFlow<List<DxvkStateCache>>
) {

    abstract val connectDBItems: Flow<Any>

    val cacheInfoCollector by lazy {
        combine(
            OnlineDXVK.dxvkRepoStructureFlow,
            caches.flatMapLatest {
                combine(it.map { cache -> cache.associatedRepoItem }) { list ->
                    list
                }
            },
            connectDBItems
        ) { t1, t2, _ ->
            t1 to t2
        }.transform { (repoStructures, _) ->
            val matchingCacheWithItem = repoStructures.findMatchingGameItem(this@Game)
            matchingCacheWithItem.forEach { (t, u) ->
                val cacheInfo = if (u == null) {
                    CacheInfo.None
                } else {
                    val downloadUrl = runCatching {
                        Constants.githubService.getStructureItemContent(u.url.replace(Constants.githubApiBaseUrl, String()))
                    }
                    val contentUrl = downloadUrl.getOrNull()?.getUrlInContent()
                    CacheInfo.Url(contentUrl)
                }
                t.info.emit(cacheInfo)
            }
            return@transform emit(matchingCacheWithItem.keys)
        }
    }

    suspend fun mergeCache(cache: DxvkStateCache) = runSuspendCatching {
        val downloadCache = (cache.info.value as? CacheInfo.Download.Cache?) ?: throw MergeException.NoFileFound

        val combinedCache = downloadCache.combinedCache
        val combineResult = combinedCache.writeTo(combinedCache.file, true).isSuccess
        val currentCaches = caches.value.toMutableList()
        val cacheIndex = currentCaches.indexOf(cache)
        combinedCache.info.emit(CacheInfo.Merged(combineResult))

        if (cacheIndex >= 0) {
            currentCaches[cacheIndex] = combinedCache
        }

        caches.emit(currentCaches)
    }

    private suspend fun restoreFile(cache: DxvkStateCache, restoreFile: File) = runSuspendCatching {
        val backupFile = cache.file.createBackup()
        val backupSuccess = cache.file.renameTo(backupFile)
        val renamed = restoreFile.renameTo(cache.file)
        if (!renamed) {
            cache.file.delete()
            return@runSuspendCatching restoreFile.renameTo(cache.file) && backupSuccess
        }
        return@runSuspendCatching backupSuccess
    }

    suspend fun restoreBackup(cache: DxvkStateCache, restoreFile: File) = runSuspendCatching {
        val success = restoreFile(cache, restoreFile)
        val restoreCache = DxvkStateCache.fromFile(cache.file).getOrThrow()

        val currentCaches = caches.value.toMutableList()
        val cacheIndex = currentCaches.indexOf(cache)
        if (cacheIndex >= 0) {
            currentCaches[cacheIndex] = restoreCache
        }
        caches.emit(currentCaches)

        success
    }

    data class Steam(
        val manifest: AppManifest,
        override val path: File,
        override val caches: MutableStateFlow<List<DxvkStateCache>>
    ) : Game(manifest.name, path, caches) {
        override val connectDBItems = combine(caches, DB.steamGames) { t1, t2 ->
            val matchingDbGameItems = t2.filter { it.appId == manifest.appId.toLongOrNull() }
            matchingDbGameItems.map { dbGame ->
                val matchingCache = t1.firstOrNull { dbGame.cacheName == it.file.name }
                matchingCache?.associatedRepoItem?.emit(dbGame.repoItem)
                matchingCache to dbGame.repoItem
            }
        }.distinctUntilChanged()
    }

    data class Other(
        override val name: String,
        override val path: File,
        val isEpicGame: Boolean,
        override val caches: MutableStateFlow<List<DxvkStateCache>>
    ) : Game(name, path, caches) {
        override val connectDBItems = flow {
            emit(this.toString())
        }
    }
}
