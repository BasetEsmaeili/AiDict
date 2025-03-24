package com.baset.ai.dict.presentation.util

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

inline fun <T> Iterable<T>.firstOrDefault(default: T, predicate: (T) -> Boolean): T {
    for (element in this) if (predicate(element)) return element
    return default
}