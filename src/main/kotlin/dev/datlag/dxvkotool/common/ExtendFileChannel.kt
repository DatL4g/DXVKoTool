package dev.datlag.dxvkotool.common

import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.DXVKException
import dev.datlag.dxvkotool.other.ReadErrorType
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

fun FileChannel.readU32(order: ByteOrder?): Result<UInt> = runCatching {
    val bytes = ByteBuffer.allocate(Constants.U32_BYTE_BUFFER_CAPACITY)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U32)
    }

    bytes[Constants.BYTE_POSITION_1].unsignedInt(order) +
        bytes[Constants.BYTE_POSITION_2].unsignedShl(Constants.BYTE_POSITION_2_SHIFT, order) +
        bytes[Constants.BYTE_POSITION_3].unsignedShl(Constants.BYTE_POSITION_3_SHIFT, order) +
        bytes[Constants.BYTE_POSITION_4].unsignedShl(Constants.BYTE_POSITION_4_SHIFT, order)
}

fun FileChannel.readU24(order: ByteOrder?): Result<UInt> = runCatching {
    val bytes = ByteBuffer.allocate(Constants.U24_BYTE_BUFFER_CAPACITY)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U24)
    }

    bytes[Constants.BYTE_POSITION_1].unsignedInt(order) +
        bytes[Constants.BYTE_POSITION_2].unsignedShl(Constants.BYTE_POSITION_2_SHIFT, order) +
        bytes[Constants.BYTE_POSITION_3].unsignedShl(Constants.BYTE_POSITION_3_SHIFT, order)
}

fun FileChannel.readU8(order: ByteOrder?): Result<UInt> = runCatching {
    val bytes = ByteBuffer.allocate(Constants.U8_BYTE_BUFFER_CAPACITY)
    order?.let { bytes.order(it) }
    val state = this.read(bytes)

    if (state == -1) {
        throw DXVKException.ReadError(ReadErrorType.U8)
    }

    bytes[Constants.BYTE_POSITION_1].unsignedInt(order)
}

fun FileChannel.writeU32(value: UInt, order: ByteOrder?) = runCatching {
    val byteArray = byteArrayOf(
        value.toByte(),
        value.shr(Constants.BYTE_POSITION_2_SHIFT).toByte(),
        value.shr(Constants.BYTE_POSITION_3_SHIFT).toByte(),
        value.shr(Constants.BYTE_POSITION_4_SHIFT).toByte()
    )
    val bytes = ByteBuffer.wrap(byteArray)
    order?.let { bytes.order(it) }
    this.write(bytes)
}

fun FileChannel.writeU24(value: UInt, order: ByteOrder?) = runCatching {
    val byteArray = byteArrayOf(
        value.toByte(),
        value.shr(Constants.BYTE_POSITION_2_SHIFT).toByte(),
        value.shr(Constants.BYTE_POSITION_3_SHIFT).toByte()
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
