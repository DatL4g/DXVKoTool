package dev.datlag.dxvkotool.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

suspend fun <R> runSuspendCatching(dispatcher: CoroutineDispatcher = Dispatchers.IO, block: suspend () -> R): Result<R> =
    withContext(dispatcher) {
        return@withContext try {
            Result.success(block())
        } catch (c: CancellationException) {
            throw c
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
