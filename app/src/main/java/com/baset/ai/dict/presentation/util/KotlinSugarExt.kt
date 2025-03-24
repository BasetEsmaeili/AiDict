package com.baset.ai.dict.presentation.util

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime
import java.time.ZoneId
import java.util.UUID

fun <T> lazyFast(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun isDaytime(timeZone: ZoneId = ZoneId.systemDefault()): Boolean {
    val now = LocalTime.now(timeZone)
    val startOfDay = LocalTime.of(6, 0)  // 6:00 AM
    val endOfDay = LocalTime.of(18, 0)   // 6:00 PM
    return now.isAfter(startOfDay) && now.isBefore(endOfDay)
}

fun randomStringUUID() = UUID.randomUUID().toString()

suspend fun ByteArray.toBase64(): String = withContext(Dispatchers.IO) {
    return@withContext Base64.encodeToString(this@toBase64, Base64.NO_WRAP)
}