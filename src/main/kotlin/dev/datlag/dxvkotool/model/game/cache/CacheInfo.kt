package dev.datlag.dxvkotool.model.game.cache

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Search
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.other.StringRes
import java.io.File

sealed class CacheInfo {

    object None : CacheInfo() {
        override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
            text = StringRes.get().noneFound,
            icon = Icons.Filled.Clear,
            isDownload = false,
            isMerge = false,
            isRepair = false
        )
    }

    data class Url(
        val downloadUrl: String?
    ) : CacheInfo() {
        override fun toButtonInfo(gameCache: DxvkStateCache) = if (this.downloadUrl.isNullOrEmpty()) {
            UpdateButtonInfo(
                text = StringRes.get().unavailable,
                icon = Icons.Filled.Clear,
                isDownload = false,
                isMerge = false,
                isRepair = false
            )
        } else {
            UpdateButtonInfo(
                text = StringRes.get().download,
                icon = Icons.Filled.FileDownload,
                isDownload = true,
                isMerge = false,
                isRepair = false
            )
        }
    }

    sealed class Loading : CacheInfo() {
        object Url : Loading() {
            override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
                text = StringRes.get().loading,
                icon = Icons.Filled.HourglassBottom,
                isDownload = false,
                isMerge = false,
                isRepair = false
            )
        }

        object Download : Loading() {
            override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
                text = StringRes.get().downloading,
                icon = Icons.Filled.HourglassBottom,
                isDownload = false,
                isMerge = false,
                isRepair = false
            )
        }

        object Local : Loading() {
            override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
                text = StringRes.get().loading,
                icon = Icons.Filled.HourglassBottom,
                isDownload = false,
                isMerge = false,
                isRepair = false
            )
        }
    }

    sealed class Download private constructor(open val file: File) : CacheInfo() {
        data class Cache(
            override val file: File,
            val cache: DxvkStateCache,
            val combinedCache: DxvkStateCache
        ) : Download(file) {
            override fun toButtonInfo(gameCache: DxvkStateCache): UpdateButtonInfo {
                val newEntrySize = this.combinedCache.entries.size - gameCache.entries.size
                return if (newEntrySize > 0) {
                    UpdateButtonInfo(
                        text = StringRes.get().merge,
                        icon = Icons.Filled.MergeType,
                        isDownload = false,
                        isMerge = true,
                        isRepair = false
                    )
                } else {
                    UpdateButtonInfo(
                        text = StringRes.get().upToDate,
                        icon = Icons.Filled.Check,
                        isDownload = false,
                        isMerge = false,
                        isRepair = false
                    )
                }
            }
        }

        data class NoCache(override val file: File) : Download(file)
    }

    data class Merged(val success: Boolean) : CacheInfo() {
        override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
            text = if (this.success) StringRes.get().merged else StringRes.get().error,
            icon = if (this.success) Icons.Filled.Check else Icons.Filled.Clear,
            isDownload = false,
            isMerge = false,
            isRepair = false
        )
    }

    sealed class Processing : CacheInfo() {

        object DetectingFileType : Processing() {
            override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
                text = StringRes.get().detecting,
                icon = Icons.Filled.InsertDriveFile,
                isDownload = false,
                isMerge = false,
                isRepair = false
            )
        }

        object ExtractingArchive : Processing() {
            override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
                text = StringRes.get().extracting,
                icon = Icons.Filled.Archive,
                isDownload = false,
                isMerge = false,
                isRepair = false
            )
        }

        object FindMatchingFile : Processing() {
            override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
                text = StringRes.get().matching,
                icon = Icons.Filled.Search,
                isDownload = false,
                isMerge = false,
                isRepair = false
            )
        }

        object CreatingCache : Processing() {
            override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
                text = StringRes.get().creating,
                icon = Icons.Filled.Build,
                isDownload = false,
                isMerge = false,
                isRepair = false
            )
        }
    }

    sealed class Error : CacheInfo() {

        data class InvalidEntries(
            val amount: Int
        ) : Error() {
            override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
                text = StringRes.get().repair,
                icon = Icons.Filled.Build,
                isDownload = false,
                isMerge = false,
                isRepair = true
            )
        }
        object Download : Error()

        override fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
            text = StringRes.get().error,
            icon = Icons.Filled.Clear,
            isDownload = false,
            isMerge = false,
            isRepair = false
        )
    }

    open fun toButtonInfo(gameCache: DxvkStateCache) = UpdateButtonInfo(
        text = StringRes.get().unknown,
        icon = Icons.Filled.QuestionAnswer,
        isDownload = false,
        isMerge = false,
        isRepair = false
    )
}
