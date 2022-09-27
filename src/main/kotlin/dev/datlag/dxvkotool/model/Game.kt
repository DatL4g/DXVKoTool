package dev.datlag.dxvkotool.model

import dev.datlag.dxvkotool.common.runSuspendCatching
import dev.datlag.dxvkotool.db.DB
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.MergeException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

sealed class Game(
    open val name: String,
    open val path: File,
    open val caches: MutableStateFlow<List<DxvkStateCache>>
) {

    abstract val connectDBItems: Flow<Any>

    fun loadCacheInfo(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        loadCacheInfo()
    }

    suspend fun loadCacheInfo() = coroutineScope {
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
        }.distinctUntilChanged().collect { (repoStructures, _) ->
            val matchingCacheWithItem = repoStructures.findMatchingGameItem(this@Game)
            matchingCacheWithItem.forEach { (t, u) ->
                val cacheInfo = if (u == null) {
                    CacheInfo.None
                } else {
                    val downloadUrl = runCatching {
                        Constants.githubService.getStructureItemContent(u.url.replace(Constants.githubApiBaseUrl, String()))
                    }
                    CacheInfo.Url(downloadUrl.getOrNull()?.getUrlInContent())
                }
                t.info.emit(cacheInfo)
            }
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
        override val caches: MutableStateFlow<List<DxvkStateCache>>
    ) : Game(name, path, caches) {
        override val connectDBItems: Flow<Unit> = flow {  }
    }
}
