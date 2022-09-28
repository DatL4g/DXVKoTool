package dev.datlag.dxvkotool.common

import androidx.compose.material.SnackbarHostState
import dev.datlag.dxvkotool.other.DXVKException
import dev.datlag.dxvkotool.other.DownloadException
import dev.datlag.dxvkotool.other.ReadErrorType
import dev.datlag.dxvkotool.other.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun <T> SnackbarHostState.showFromResult(scope: CoroutineScope, result: Result<T>, success: String) = scope.launch(Dispatchers.IO) {
    val message = snackbarMessage(result, success)

    if (message.isNotEmpty()) {
        this@showFromResult.showSnackbar(message)
    }
}

suspend fun <T> SnackbarHostState.showFromResult(result: Result<T>, success: String) {
    val message = snackbarMessage(result, success)

    if (message.isNotEmpty()) {
        this@showFromResult.showSnackbar(message)
    }
}

private fun <T> snackbarMessage(result: Result<T>, success: String) = if (result.isSuccess) {
    success
} else when (val exception = result.exceptionOrNull()) {
    is DXVKException.ReadError -> {
        if (exception.type is ReadErrorType.MAGIC) {
            StringRes.get().fileInvalidMagic
        } else {
            StringRes.get().fileReadBytesError
        }
    }

    is DXVKException.InvalidEntry -> StringRes.get().fileInvalidEntry
    is DXVKException.UnexpectedEndOfFile -> StringRes.get().fileUnexpectedEnd
    is DXVKException.VersionMismatch -> StringRes.get().cacheVersionMismatchPlaceholder.format(
        exception.current.toInt(),
        exception.new.toInt()
    )

    is DownloadException.NoDownloadUrl -> StringRes.get().noDownloadUrlProvided
    is DownloadException.InvalidFile -> StringRes.get().downloadFileInvalid
    else -> {
        val innerMessage = exception?.message ?: StringRes.get().unknown
        innerMessage.ifEmpty {
            StringRes.get().unknown
        }
    }
}
