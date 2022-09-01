package dev.datlag.dxvkotool.dxvk

import dev.datlag.dxvkotool.common.readU24
import dev.datlag.dxvkotool.common.readU8
import dev.datlag.dxvkotool.common.writeU24
import dev.datlag.dxvkotool.common.writeU8
import dev.datlag.dxvkotool.other.DXVK
import dev.datlag.dxvkotool.other.DXVKException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.security.MessageDigest

data class DxvkStateCacheEntry(
    val header: Header?,
    val hash: ByteBuffer,
    val data: ByteBuffer
) {

    private fun dataSha1(): ByteArray {
        val hasher = MessageDigest.getInstance("SHA-1")
        hasher.update(data.array())

        if (header == null) {
            hasher.update(DXVK.SHA1_EMPTY)
        }

        return hasher.digest()
    }

    fun isValid() = dataSha1().contentEquals(hash.array())

    fun writeTo(writer: FileChannel, edition: DxvkStateCacheEdition) = runCatching {
        when (edition) {
            is DxvkStateCacheEdition.Standard -> writeToStandard(writer).getOrThrow()
            is DxvkStateCacheEdition.Legacy -> writeToLegacy(writer).getOrThrow()
        }
    }

    private fun writeToStandard(writer: FileChannel) = runCatching {
        header?.writeTo(writer)
        writer.write(ByteBuffer.wrap(hash.array()))
        writer.write(ByteBuffer.wrap(data.array()))
    }

    private fun writeToLegacy(writer: FileChannel) = runCatching {
        writer.write(ByteBuffer.wrap(data.array()))
        writer.write(ByteBuffer.wrap(hash.array()))
    }

    data class Header(
        val stageMask: UInt,
        val entrySize: UInt
    ) {

        fun writeTo(writer: FileChannel) = runCatching {
            writer.writeU8(stageMask, null)
            writer.writeU24(entrySize, DXVK.ENDIAN)
        }

        companion object {
            fun fromReader(reader: FileChannel): Result<Header> = runCatching {
                Header(
                    reader.readU8(null).getOrThrow(),
                    reader.readU24(DXVK.ENDIAN).getOrThrow()
                )
            }
        }
    }

    companion object {
        fun fromReader(reader: FileChannel, header: DxvkStateCache.Header): Result<DxvkStateCacheEntry> = runCatching {
            val entry = if (header.edition.isLegacy()) {
                fromReaderLegacy(reader, header.entrySize)
            } else {
                fromReaderStandard(reader)
            }.getOrThrow()
            if (!entry.isValid()) {
                throw DXVKException.InvalidEntry
            }
            entry
        }

        private fun fromReaderStandard(reader: FileChannel): Result<DxvkStateCacheEntry> = runCatching {
            val header = Header.fromReader(reader)
            val entry = withHeader(header.getOrThrow())
            val hashState = reader.read(entry.hash)
            val dataState = reader.read(entry.data)

            if (hashState == -1) {
                throw DXVKException.ExpectedEndOfFile
            } else if (dataState == -1) {
                throw DXVKException.UnexpectedEndOfFile
            }

            entry
        }

        private fun fromReaderLegacy(reader: FileChannel, size: UInt): Result<DxvkStateCacheEntry> = runCatching {
            val entry = withLength(size)
            val dataState = reader.read(entry.data)
            val hashState = reader.read(entry.hash)

            if (dataState == -1) {
                throw DXVKException.ExpectedEndOfFile
            } else if (hashState == -1) {
                throw DXVKException.UnexpectedEndOfFile
            }

            entry
        }

        private fun withHeader(header: Header) = DxvkStateCacheEntry(
            header,
            ByteBuffer.allocate(DXVK.HASH_SIZE),
            ByteBuffer.allocate(header.entrySize.toInt())
        )

        private fun withLength(size: UInt) = DxvkStateCacheEntry(
            null,
            ByteBuffer.allocate(DXVK.HASH_SIZE),
            ByteBuffer.allocate(size.toInt() - DXVK.HASH_SIZE)
        )
    }
}
