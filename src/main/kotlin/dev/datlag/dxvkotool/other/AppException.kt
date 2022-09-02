package dev.datlag.dxvkotool.other

sealed class DXVKException : Exception() {
    object ExpectedEndOfFile : DXVKException()
    object UnexpectedEndOfFile : DXVKException()
    object InvalidEntry : DXVKException()
    data class ReadError(val type: ReadErrorType) : DXVKException()

    data class VersionMismatch(val current: UInt, val new: UInt) : DXVKException()
}

sealed class ReadErrorType {
    object MAGIC : ReadErrorType()
    object U32 : ReadErrorType()
    object U24 : ReadErrorType()
    object U8 : ReadErrorType()
}

sealed class DownloadException : Exception() {

    object NoDownloadUrl : DownloadException()

    object InvalidFile : DownloadException()

}

sealed class MergeException : Exception() {

    object NoFileFound : MergeException()

}
