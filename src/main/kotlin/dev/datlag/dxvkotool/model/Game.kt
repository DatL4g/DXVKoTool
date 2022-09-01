package dev.datlag.dxvkotool.model

import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.network.OnlineDXVK
import dev.datlag.dxvkotool.other.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

sealed class Game(
    open val name: String,
    open val path: File,
    open val caches: MutableStateFlow<Map<DxvkStateCache, CacheInfo>>
) {
    fun loadCacheInfo(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        loadCacheInfo()
    }

    suspend fun loadCacheInfo(): Nothing = coroutineScope {
        OnlineDXVK.dxvkRepoStructureFlow.collect { repoStructures ->
            val matchingCacheWithItem = repoStructures.findMatchingGameItem(this@Game)
            val mapped = matchingCacheWithItem.map { (t, u) ->
                async(Dispatchers.IO) {
                    t to if (u == null) {
                        CacheInfo.None
                    } else {
                        val downloadUrl = runCatching {
                            Constants.githubService.getStructureItemContent(u.url.replace(Constants.githubApiBaseUrl, String()))
                        }
                        CacheInfo.Url(downloadUrl.getOrNull()?.getUrlInContent())
                    }
                }
            }.awaitAll().toMap()
            caches.emit(mapped)
        }
    }

    data class Steam(
        val manifest: AppManifest,
        override val path: File,
        override val caches: MutableStateFlow<Map<DxvkStateCache, CacheInfo>>
    ) : Game(manifest.name, path, caches)

    data class Other(
        override val name: String,
        override val path: File,
        override val caches: MutableStateFlow<Map<DxvkStateCache, CacheInfo>>
    ) : Game(name, path, caches)
}
