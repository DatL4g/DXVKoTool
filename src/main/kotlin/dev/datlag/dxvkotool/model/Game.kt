package dev.datlag.dxvkotool.model

import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.MergeException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

sealed class Game(
    open val name: String,
    open val path: File,
    open val caches: MutableStateFlow<List<DxvkStateCache>>
) {
    fun loadCacheInfo(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        loadCacheInfo()
    }

    suspend fun loadCacheInfo(): Nothing = coroutineScope {
        OnlineDXVK.dxvkRepoStructureFlow.collect { repoStructures ->
            val matchingCacheWithItem = repoStructures.findMatchingGameItem(this@Game)
            matchingCacheWithItem.map { (t, u) ->
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

    fun mergeCache(scope: CoroutineScope, cache: DxvkStateCache) = runCatching {
        val mergeFile = (cache.info.value as? CacheInfo.Download?)?.file ?: throw MergeException.NoFileFound

        scope.launch(Dispatchers.IO) {
            val mergeCache = DxvkStateCache.fromFile(mergeFile).getOrThrow()
            val combinedCache = cache.combine(mergeCache).getOrThrow()
            val combineResult = combinedCache.writeTo(combinedCache.file, true).isSuccess
            val currentCaches = caches.value.toMutableList()
            val cacheIndex = currentCaches.indexOf(cache)
            combinedCache.info.emit(CacheInfo.Merged(combineResult))

            if (cacheIndex >= 0) {
                currentCaches[cacheIndex] = combinedCache
            }

            caches.emit(currentCaches)
        }
    }

    data class Steam(
        val manifest: AppManifest,
        override val path: File,
        override val caches: MutableStateFlow<List<DxvkStateCache>>
    ) : Game(manifest.name, path, caches)

    data class Other(
        override val name: String,
        override val path: File,
        override val caches: MutableStateFlow<List<DxvkStateCache>>
    ) : Game(name, path, caches)
}
