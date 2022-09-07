package dev.datlag.dxvkotool.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.other.StringRes
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
            val cache: DxvkStateCache,
            val combinedCache: DxvkStateCache
        ) : Download(file)

        data class NoCache(override val file: File) : Download(file)
    }

    data class Merged(val success: Boolean) : CacheInfo()

    sealed class Processing : CacheInfo() {

        object DetectingFileType : Processing()

        object ExtractingArchive : Processing()

        object FindMatchingFile : Processing()

        object CreatingCache : Processing()
    }

    sealed class Error : CacheInfo() {
        object Download : Error()
    }

    fun toButtonInfo(): UpdateButtonInfo {
        return when (this) {
            is Loading.Url -> {
                UpdateButtonInfo(
                    text = StringRes.get().loading,
                    icon = Icons.Filled.HourglassBottom,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Loading.Download -> {
                UpdateButtonInfo(
                    text = StringRes.get().downloading,
                    icon = Icons.Filled.HourglassBottom,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Loading.Local -> {
                UpdateButtonInfo(
                    text = StringRes.get().loading,
                    icon = Icons.Filled.HourglassBottom,
                    isDownload = false,
                    isMerge = false
                )
            }
            is None -> {
                UpdateButtonInfo(
                    text = StringRes.get().noneFound,
                    icon = Icons.Filled.Clear,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Url -> {
                if (this.downloadUrl.isNullOrEmpty()) {
                    UpdateButtonInfo(
                        text = StringRes.get().unavailable,
                        icon = Icons.Filled.Clear,
                        isDownload = false,
                        isMerge = false
                    )
                } else {
                    UpdateButtonInfo(
                        text = StringRes.get().download,
                        icon = Icons.Filled.FileDownload,
                        isDownload = true,
                        isMerge = false
                    )
                }
            }
            is Download.Cache -> {
                val newEntrySize = this.combinedCache.entries.size - cache.entries.size
                if (newEntrySize > 0) {
                    UpdateButtonInfo(
                        text = StringRes.get().merge,
                        icon = Icons.Filled.MergeType,
                        isDownload = false,
                        isMerge = true
                    )
                } else {
                    UpdateButtonInfo(
                        text = StringRes.get().upToDate,
                        icon = Icons.Filled.Check,
                        isDownload = false,
                        isMerge = false
                    )
                }
            }
            is Download.NoCache -> {
                UpdateButtonInfo(
                    text = StringRes.get().unknown,
                    icon = Icons.Filled.QuestionAnswer,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Merged -> {
                UpdateButtonInfo(
                    text = if (this.success) StringRes.get().merged else StringRes.get().error,
                    icon = if (this.success) Icons.Filled.Check else Icons.Filled.Clear,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Error -> {
                UpdateButtonInfo(
                    text = StringRes.get().error,
                    icon = Icons.Filled.Clear,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Processing.DetectingFileType -> {
                UpdateButtonInfo(
                    text = StringRes.get().detecting,
                    icon = Icons.Filled.InsertDriveFile,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Processing.ExtractingArchive -> {
                UpdateButtonInfo(
                    text = StringRes.get().extracting,
                    icon = Icons.Filled.Archive,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Processing.FindMatchingFile -> {
                UpdateButtonInfo(
                    text = StringRes.get().matching,
                    icon = Icons.Filled.Search,
                    isDownload = false,
                    isMerge = false
                )
            }
            is Processing.CreatingCache -> {
                UpdateButtonInfo(
                    text = StringRes.get().creating,
                    icon = Icons.Filled.Build,
                    isDownload = false,
                    isMerge = false
                )
            }
        }
    }
}
