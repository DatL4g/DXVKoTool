package dev.datlag.dxvkotool.common

import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileTime
import kotlin.math.max

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

fun File.listFilesSafely(): List<File> {
    return runCatching {
        this.listFiles()
    }.getOrNull()?.filterNotNull() ?: emptyList()
}

fun File.isFileSafely(): Boolean {
    return runCatching {
        this.isFile
    }.getOrNull() ?: false
}

fun File.findBackupFiles(): List<File> {
    return runCatching {
        val parent = this.parentFile
        if (parent.existsSafely() && parent.canReadSafely()) {
            parent.listFilesSafely().filter {
                it.existsSafely() && it.canReadSafely() && it.isFileSafely() && it.name.startsWith(this@findBackupFiles.name) && it.extension.equals("bak", true)
            }
        } else {
            emptyList()
        }
    }.getOrNull() ?: emptyList()
}

fun File.lastModifiedSafely(): Long {
    return runCatching {
        this.lastModified()
    }.getOrNull() ?: runCatching {
        Files.getAttribute(this.toPath(), "lastModifiedTime") as FileTime
    }.getOrNull()?.toMillis() ?: runCatching {
        Files.readAttributes(this.toPath(), BasicFileAttributes::class.java).lastModifiedTime()
    }.getOrNull()?.toMillis() ?: 0L
}

fun File.creationTimeSafely(): Long {
    return runCatching {
        Files.getAttribute(this.toPath(), "creationTime") as FileTime
    }.getOrNull()?.toMillis() ?: runCatching {
        Files.readAttributes(this.toPath(), BasicFileAttributes::class.java).creationTime()
    }.getOrNull()?.toMillis() ?: 0L
}

fun File.getLastModifiedOrCreated(): Long {
    return max(this.lastModifiedSafely(), this.creationTimeSafely())
}

fun File.sizeSafely(): Long {
    return runCatching {
        this.length()
    }.getOrNull() ?: runCatching {
        Files.getAttribute(this.toPath(), "size") as Long
    }.getOrNull() ?: runCatching {
        Files.readAttributes(this.toPath(), BasicFileAttributes::class.java).size()
    }.getOrNull() ?: 0L
}

fun File.deleteSafely(): Boolean {
    return runCatching {
        this.delete()
    }.getOrNull() ?: false
}
