package dev.datlag.dxvkotool.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

fun <T> listFrom(vararg list: Collection<T>): List<T> {
    return mutableListOf<T>().apply {
        list.forEach {
            addAll(it)
        }
    }
}

fun <T> setFrom(vararg list: Collection<T>): Set<T> {
    return mutableSetOf<T>().apply {
        list.forEach {
            addAll(it)
        }
    }
}

suspend fun <T, R> Collection<T>.mapAsync(dispatcher: CoroutineDispatcher = Dispatchers.IO, transform: suspend (T) -> R): List<R> =
    withContext(dispatcher) {
        return@withContext this@mapAsync.map {
            async(dispatcher) {
                transform(it)
            }
        }.awaitAll()
    }
