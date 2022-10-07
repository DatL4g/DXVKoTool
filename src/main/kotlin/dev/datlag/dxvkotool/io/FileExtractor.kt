package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.common.download
import dev.datlag.dxvkotool.common.runSuspendCatching
import dev.datlag.dxvkotool.dxvk.DxvkStateCache
import dev.datlag.dxvkotool.model.game.cache.CacheInfo
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

    suspend fun downloadToTempFile(cache: DxvkStateCache, url: String): Result<File> = runSuspendCatching {
        val name = cache.file.nameWithoutExtension
        val downloadFile = createTempFile(name).getOrThrow()
        Constants.httpClient.download(url, downloadFile, Constants.KIBIBYTE_SIZE * Constants.BYTE_MULTIPLY_FACTOR).getOrThrow()

        cache.info.emit(CacheInfo.Processing.DetectingFileType)
        if (Constants.tikaCore.detect(downloadFile).equals("application/octet-stream", true)) {
            return@runSuspendCatching downloadFile
        }

        val destination = createTempFolder(name).getOrThrow()
        cache.info.emit(CacheInfo.Processing.ExtractingArchive)
        val archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.XZ)
        archiver.extract(downloadFile, destination)

        cache.info.emit(CacheInfo.Processing.FindMatchingFile)
        val destinationTree = destination.walkTopDown()

        return@runSuspendCatching checkNotNull(destinationTree.firstOrNull {
            it.extension.equals("dxvk-cache", true) && it.nameWithoutExtension.equals(name, true)
        } ?: destinationTree.singleOrNull()) {
            "No matching file found in downloaded cache"
        }
    }

}
