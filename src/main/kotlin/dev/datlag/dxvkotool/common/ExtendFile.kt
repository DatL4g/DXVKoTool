package dev.datlag.dxvkotool.common

import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

fun File.openReadChannel(): FileChannel {
    val reader = RandomAccessFile(this, "r")
    return reader.channel
}

fun File.openWriteChannel(): FileChannel {
    val writer = RandomAccessFile(this, "rw")
    return writer.channel
}

fun File.existsSafely(): Boolean {
    return runCatching {
        this.exists()
    }.getOrNull() ?: false
}

fun File.canWriteSafely(): Boolean {
    return runCatching {
        this.canWrite()
    }.getOrNull() ?: false
}

fun File.canReadSafely(): Boolean {
    return runCatching {
        this.canRead()
    }.getOrNull() ?: false
}