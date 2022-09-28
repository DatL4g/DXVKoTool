package dev.datlag.dxvkotool.common

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

suspend fun HttpClient.download(url: String, outFile: File, chunkSize: Int = 1024) = runSuspendCatching {
    withContext(Dispatchers.IO) {
        val length = head(url).headers[HttpHeaders.ContentLength]?.toLong() as Long
        val lastByte = length - 1

        var start = outFile.length()
        val output = FileOutputStream(outFile, true)

        while (true) {
            val end = min(start + chunkSize - 1, lastByte)
            val data = get(url) {
                header("Range", "bytes=${start}-${end}")
            }.body<ByteArray>()
            output.write(data)
            if (end >= lastByte) break
            start += chunkSize
        }

        output.flush()
        output.close()
    }
}
