package com.example.smartfashion.data.api

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File

class ProgressRequestBody(
    private val file: File,
    private val contentType: MediaType?,
    private val onProgress: (Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {
        val fileLength = contentLength()
        val buffer = ByteArray(2048)

        file.inputStream().use { inputStream ->
            var uploaded = 0L
            var read: Int

            // Liên tục đọc và gửi từng gói dữ liệu lên server
            while (inputStream.read(buffer).also { read = it } != -1) {
                sink.write(buffer, 0, read)
                uploaded += read

                // Tính toán phần trăm và gửi ra ngoài
                val progress = ((uploaded.toDouble() / fileLength.toDouble()) * 100).toInt()
                onProgress(progress)
            }
        }
    }
}