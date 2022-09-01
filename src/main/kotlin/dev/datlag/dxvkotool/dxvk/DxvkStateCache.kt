package dev.datlag.dxvkotool.dxvk

import dev.datlag.dxvkotool.common.openWriteChannel
import dev.datlag.dxvkotool.common.readU32
import dev.datlag.dxvkotool.common.writeU32
import dev.datlag.dxvkotool.io.FileExtractor
import dev.datlag.dxvkotool.other.DXVK
import dev.datlag.dxvkotool.other.DXVKException
import dev.datlag.dxvkotool.other.ReadErrorType
import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

data class DxvkStateCache(
    val header: Header,
    val entries: List<DxvkStateCacheEntry>,
    val file: File?
) {

    fun writeTo(file: File, backup: Boolean) = runCatching {
        var backupFile = file
        var createdBackup = false
        if (!file.exists()) {
            file.createNewFile()
        } else {
            while(true) {
                backupFile = File(backupFile.parentFile, "${backupFile.name}.bak")
                if (backupFile.exists()) {
                    continue
                } else {
                    break
                }
            }

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
    }

    fun writeTo(writer: FileChannel) = runCatching {
        if (entries.isEmpty()) {
            throw IllegalStateException()
        }
        header.writeTo(writer).getOrThrow()
        entries.forEach {
            it.writeTo(writer, header.edition).getOrThrow()
        }
    }

    fun combine(other: DxvkStateCache) = runCatching {
        if (header.version != other.header.version) {
            throw IllegalArgumentException()
        }
        val newList: MutableList<DxvkStateCacheEntry> = mutableListOf()
        newList.addAll(entries)
        newList.addAll(other.entries)
        DxvkStateCache(
            header,
            newList.distinctBy { it.hash.array().contentHashCode() },
            file ?: other.file
        )
    }

    suspend fun downloadFromUrl(url: String) = runCatching {
        FileExtractor.downloadToTempFile(file!!.nameWithoutExtension, url).getOrThrow()
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
                val magic = ByteBuffer.allocate(4)
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

        fun fromReader(reader: FileChannel, file: File?): Result<DxvkStateCache> = runCatching {
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