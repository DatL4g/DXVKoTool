package dev.datlag.dxvkotool.model

import java.io.File

sealed class CacheInfo {

    object None : CacheInfo()

    data class Url(
        val downloadUrl: String?
    ) : CacheInfo()

    sealed class Loading : CacheInfo() {
        object Url : Loading()

        object Download : Loading()
    }

    data class Download(
        val file: File
    ) : CacheInfo()

    data class Merged(val success: Boolean) : CacheInfo()
}
