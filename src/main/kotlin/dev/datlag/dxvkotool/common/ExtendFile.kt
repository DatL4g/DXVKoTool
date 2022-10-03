package dev.datlag.dxvkotool.common

import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.LinkOption
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
    }.getOrNull() ?: runCatching {
        Files.isRegularFile(this.toPath())
    }.getOrNull() ?: false
}

fun File.findBackupFiles(): List<File> {
    return runCatching {
        val parent = this.parentFile
        if (parent.existsSafely() && parent.canReadSafely()) {
            parent.listFilesSafely().filter {
                it.existsSafely() && it.canReadSafely() && it.isFileSafely() && it.name.startsWith(this@findBackupFiles.name) && it.extension.equals(
                    "bak",
                    true
                )
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

fun File.createBackup(): File {
    var backupFile = this
    while (true) {
        backupFile = File(backupFile.parentFile, "${backupFile.name}.bak")
        if (backupFile.existsSafely()) {
            continue
        } else {
            break
        }
    }
    return backupFile
}

fun File.isDirectorySafely(): Boolean {
    return runCatching {
        this.isDirectory
    }.getOrNull() ?: runCatching {
        Files.isDirectory(this.toPath())
    }.getOrNull() ?: false
}

fun File.isSymlinkSafely(): Boolean {
    return runCatching {
        Files.isSymbolicLink(this.toPath())
    }.getOrNull() ?: runCatching {
        !Files.isRegularFile(this.toPath(), LinkOption.NOFOLLOW_LINKS)
    }.getOrNull() ?: false
}

fun File.getRealFile(): File {
    return if (isSymlinkSafely()) runCatching {
        Files.readSymbolicLink(this.toPath()).toFile()
    }.getOrNull() ?: this else this
}

fun File.isSame(file: File?): Boolean {
    return if (file == null) {
        false
    } else {
        this == file || runCatching {
            this.absoluteFile == file.absoluteFile || Files.isSameFile(this.toPath(), file.toPath())
        }.getOrNull() ?: false
    }
}

fun Collection<File>.normalize(mustExist: Boolean = true): List<File> {
    val list: MutableList<File> = mutableListOf()
    this.forEach { file ->
        var realFile = file.getRealFile()
        if (mustExist) {
            if (!realFile.existsSafely()) {
                if (file.existsSafely()) {
                    realFile = file
                } else {
                    return@forEach
                }
            }
        }
        if (list.firstOrNull { it.isSame(realFile) } == null) {
            list.add(realFile)
        }
    }
    return list
}
