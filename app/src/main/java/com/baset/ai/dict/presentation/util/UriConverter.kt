package com.baset.ai.dict.presentation.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class UriConverter(private val context: Context) {
    suspend fun toByteArray(uri: Uri) = withContext(Dispatchers.IO) {
        return@withContext kotlin.runCatching {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (stream.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                byteArrayOutputStream.toByteArray()
            }
        }.getOrNull()
    }
}