package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.common.download
import dev.datlag.dxvkotool.common.runSuspendCatching
import dev.datlag.dxvkotool.other.Constants
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.CompressionType
import java.io.File
import java.nio.file.Files

object FileExtractor {

    private fun createTempFile(name: String) = runCatching {
        val tmpFile = File.createTempFile("tmp_${name}", null)
        tmpFile.deleteOnExit()
        tmpFile
    }

    private fun createTempFolder(name: String) = runCatching {
        val tmpFolderPath = Files.createTempDirectory("tmp_${name}")
        val tmpFolder = tmpFolderPath.toFile()
        tmpFolder.deleteOnExit()
        tmpFolder
    }

    suspend fun downloadToTempFile(name: String, url: String): Result<File> = runSuspendCatching {
        val downloadFile = createTempFile(name).getOrThrow()
        Constants.httpClient.download(url, downloadFile, 1024 * 1000)

        if (Constants.tikaCore.detect(downloadFile).equals("application/octet-stream", true)) {
            return@runSuspendCatching downloadFile
        }

        val destination = createTempFolder(name).getOrThrow()
        val archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.XZ)
        archiver.extract(downloadFile, destination)

        return@runSuspendCatching if (destination.extension.equals("dxvk-cache", true)) {
            destination
        } else {
            destination.walkTopDown().firstOrNull {
                it.extension.equals("dxvk-cache", true) && it.nameWithoutExtension.equals(name, true)
            } ?: throw IllegalStateException()
        }
    }

}