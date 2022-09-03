package dev.datlag.dxvkotool.model

import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import java.io.File

sealed class CacheInfo {

    object None : CacheInfo()

    data class Url(
        val downloadUrl: String?
    ) : CacheInfo()

    sealed class Loading : CacheInfo() {
        object Url : Loading()

        object Download : Loading()

        object Local : Loading()
    }

    sealed class Download private constructor(open val file: File) : CacheInfo() {
        data class Cache(
            override val file: File,
            val cache: DxvkStateCache
        ) : Download(file)

        data class NoCache(override val file: File) : Download(file)
    }

    data class Merged(val success: Boolean) : CacheInfo()
}
