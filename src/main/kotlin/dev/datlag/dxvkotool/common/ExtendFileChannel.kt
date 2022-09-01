package dev.datlag.dxvkotool.common

import dev.datlag.dxvkotool.other.DXVKException
import dev.datlag.dxvkotool.other.ReadErrorType
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

fun FileChannel.readU32(order: ByteOrder?): Result<UInt> = runCatching {
    val bytes = ByteBuffer.allocate(4)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U32)
    }

    bytes[0].unsignedInt(order) + bytes[1].unsignedShl(8, order) + bytes[2].unsignedShl(16, order) + bytes[3].unsignedShl(24, order)
}

fun FileChannel.readU24(order: ByteOrder?): Result<UInt> = runCatching {
    val bytes = ByteBuffer.allocate(3)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U24)
    }

    bytes[0].unsignedInt(order) + bytes[1].unsignedShl(8, order) + bytes[2].unsignedShl(16, order)
}

fun FileChannel.readU8(order: ByteOrder?): Result<UInt> = runCatching {
    val bytes = ByteBuffer.allocate(1)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U8)
    }

    bytes[0].unsignedInt(order)
}

fun FileChannel.writeU32(value: UInt, order: ByteOrder?) = runCatching {
    val byteArray = byteArrayOf(
        value.toByte(),
        value.shr(8).toByte(),
        value.shr(16).toByte(),
        value.shr(24).toByte()
    )
    val bytes = ByteBuffer.wrap(byteArray)
    order?.let { bytes.order(it) }
    this.write(bytes)
}

fun FileChannel.writeU24(value: UInt, order: ByteOrder?) = runCatching {
    val byteArray = byteArrayOf(
        value.toByte(),
        value.shr(8).toByte(),
        value.shr(16).toByte()
    )
    val bytes = ByteBuffer.wrap(byteArray)
    order?.let { bytes.order(it) }
    this.write(bytes)
}

fun FileChannel.writeU8(value: UInt, order: ByteOrder?) = runCatching {
    val bytes = ByteBuffer.wrap(
        byteArrayOf(
            value.toByte()
        )
    )
    order?.let { bytes.order(it) }
    this.write(bytes)
}
