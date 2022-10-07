package dev.datlag.dxvkotool.dxvk

import dev.datlag.dxvkotool.common.createBackup
import dev.datlag.dxvkotool.common.existsSafely
import dev.datlag.dxvkotool.common.findBackupFiles
import dev.datlag.dxvkotool.common.openWriteChannel
import dev.datlag.dxvkotool.common.readU32
import dev.datlag.dxvkotool.common.runSuspendCatching
import dev.datlag.dxvkotool.common.writeU32
import dev.datlag.dxvkotool.io.FileExtractor
import dev.datlag.dxvkotool.model.game.cache.CacheInfo
import dev.datlag.dxvkotool.other.DXVK
import dev.datlag.dxvkotool.other.DXVKException
import dev.datlag.dxvkotool.other.DownloadException
import dev.datlag.dxvkotool.other.ReadErrorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

data class DxvkStateCache(
    val header: Header,
    val entries: List<DxvkStateCacheEntry>,
    val file: File
) {

    val info: MutableStateFlow<CacheInfo> = MutableStateFlow(CacheInfo.Loading.Url)

    val associatedRepoItem: MutableStateFlow<String?> = MutableStateFlow(null)

    val backupFiles: MutableStateFlow<List<File>> = MutableStateFlow(file.findBackupFiles())

    suspend fun writeTo(file: File, backup: Boolean) = runSuspendCatching {
        var backupFile = file
        var createdBackup = false
        if (!file.existsSafely()) {
            file.createNewFile()
        } else {
            backupFile = backupFile.createBackup()

            createdBackup = file.renameTo(backupFile)
            file.createNewFile()
        }
        val writer = file.openWriteChannel()
        val newWriteResult = writeTo(writer)

        if (newWriteResult.isFailure && createdBackup) {
            backupFile.renameTo(file)
        }

        if (!backup && createdBackup) {
            backupFile.delete()
        }

        writer.force(true)
        writer.close()
        backupFiles.emit(file.findBackupFiles())
        return@runSuspendCatching true
    }

    suspend fun writeTo(writer: FileChannel) = runSuspendCatching {
        check(entries.isNotEmpty()) {
            "Could not write cache because it does not contain any entries"
        }
        header.writeTo(writer).getOrThrow()
        entries.forEach {
            it.writeTo(writer, header.edition).getOrThrow()
        }
    }

    suspend fun combine(other: DxvkStateCache) = runSuspendCatching {
        if (header.version != other.header.version) {
            throw DXVKException.VersionMismatch(
                header.version,
                other.header.version
            )
        }
        val newList: MutableList<DxvkStateCacheEntry> = mutableListOf()
        newList.addAll(entries)
        newList.addAll(other.entries)
        DxvkStateCache(
            header,
            newList.distinctBy { it.hash.array().contentHashCode() },
            file
        )
    }

    suspend fun downloadFromUrl(url: String) = runSuspendCatching {
        FileExtractor.downloadToTempFile(this, url).getOrThrow()
    }

    suspend fun downloadCache() = runSuspendCatching {
        val downloadUrl = (info.value as? CacheInfo.Url?)?.downloadUrl
        if (downloadUrl.isNullOrEmpty()) {
            throw DownloadException.NoDownloadUrl
        }
        info.emit(CacheInfo.Loading.Download)

        val fileResult = downloadFromUrl(downloadUrl).getOrNull() ?: run {
            info.emit(CacheInfo.Error.Download)
            throw DownloadException.InvalidFile
        }

        info.emit(CacheInfo.Processing.CreatingCache)
        val cache = fromFile(fileResult).getOrNull()
        info.emit(
            if (cache != null) {
                val combinedCache = combine(cache).getOrThrow()
                CacheInfo.Download.Cache(fileResult, cache, combinedCache)
            } else {
                CacheInfo.Download.NoCache(fileResult)
            }
        )
    }

    fun loadLocalFile(scope: CoroutineScope, loadFile: File) = scope.launch(Dispatchers.IO) {
        info.emit(CacheInfo.Loading.Local)

        val cache = fromFile(loadFile).getOrNull()
        info.emit(
            if (cache != null) {
                val combinedCache = combine(cache).getOrThrow()
                CacheInfo.Download.Cache(loadFile, cache, combinedCache)
            } else {
                CacheInfo.Download.NoCache(loadFile)
            }
        )
    }

    fun reloadBackupFiles(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        reloadBackupFiles()
    }

    suspend fun reloadBackupFiles() = withContext(Dispatchers.IO) {
        backupFiles.emit(file.findBackupFiles())
    }

    data class Header(
        val magic: ByteBuffer,
        val version: UInt,
        val entrySize: UInt
    ) {
        val edition: DxvkStateCacheEdition
            get() = when {
                version > DXVK.LEGACY_VERSION.toUInt() -> DxvkStateCacheEdition.Standard
                else -> DxvkStateCacheEdition.Legacy
            }

        fun writeTo(writer: FileChannel) = runCatching {
            writer.write(ByteBuffer.wrap(magic.array()))
            writer.writeU32(version, DXVK.ENDIAN)
            writer.writeU32(entrySize, DXVK.ENDIAN)
        }

        companion object {
            fun fromReader(reader: FileChannel): Result<Header> = runCatching {
                val magic = ByteBuffer.allocate(DXVK.MAGIC_BYTE_BUFFER_CAPACITY)
                reader.read(magic)

                if (String(magic.array()) != DXVK.MAGIC) {
                    throw DXVKException.ReadError(ReadErrorType.MAGIC)
                }

                val version = reader.readU32(DXVK.ENDIAN).getOrThrow()
                val entrySize = reader.readU32(DXVK.ENDIAN).getOrThrow()

                Header(
                    magic,
                    version,
                    entrySize
                )
            }
        }
    }

    companion object {
        fun fromFile(file: File): Result<DxvkStateCache> = runCatching {
            val reader = FileChannel.open(file.toPath())
            val cache = fromReader(reader, file).getOrThrow()
            reader.close()
            cache
        }

        fun fromReader(reader: FileChannel, file: File): Result<DxvkStateCache> = runCatching {
            val entries: MutableList<DxvkStateCacheEntry> = mutableListOf()
            val header = Header.fromReader(reader).getOrThrow()

            while (true) {
                val entry = runCatching {
                    DxvkStateCacheEntry.fromReader(reader, header).getOrThrow()
                }.getOrNull() ?: break
                entries.add(entry)
            }

            DxvkStateCache(header, entries.distinctBy { it.hash.array().contentHashCode() }, file)
        }
    }
}
