package dev.datlag.dxvkotool.other

interface AppStringRes {
    val name: String
    val info: String
    val reload: String
    val versionPlaceholder: String
    val entriesPlaceholder: String
    val export: String
    val merge: String
    val restoreBackup: String
    val steamGames: String
    val otherGames: String
    val loading: String
    val noneFound: String
    val unavailable: String
    val download: String
    val unknown: String
    val add: String
    val merged: String
    val error: String
    val downloading: String
    val more: String
    val mergeLocalFile: String
    val load: String
    val save: String
    val connectRepoItem: String
    val upToDate: String

    val fileInvalidMagic: String
    val fileReadBytesError: String
    val fileInvalidEntry: String
    val fileUnexpectedEnd: String
    val cacheVersionMismatchPlaceholder: String
    val noDownloadUrlProvided: String
    val downloadFileInvalid: String

    val exportSuccessful: String
    val openFolder: String
    val close: String
    val donate: String
    val fullAppDescription: String
    val unsupportedSystem: String

    val githubPlaceholder: String
    val cacheInformation: String
}

object StringRes {
    private val en = object : AppStringRes {
        override val name: String = "DXVKoTool"
        override val info: String = "Info"
        override val reload: String = "Reload"
        override val versionPlaceholder: String = "Version: %s"
        override val entriesPlaceholder: String = "Entries: %d"
        override val export: String = "Export"
        override val merge: String = "Merge"
        override val restoreBackup: String = "Restore Backup"
        override val steamGames: String = "Steam Games"
        override val otherGames: String = "Other Games"

        override val loading: String = "Loading"
        override val noneFound: String = "None Found"
        override val unavailable: String = "Unavailable"
        override val download: String = "Download"
        override val unknown: String = "Unknown"
        override val add: String = "Add"
        override val merged: String = "Merged"
        override val error: String = "Error"
        override val downloading: String = "Downloading"

        override val more: String = "More"
        override val mergeLocalFile: String = "Merge local file"
        override val load: String = "Load"
        override val save: String = "Save"
        override val connectRepoItem: String = "Connect repository item"
        override val upToDate: String = "Up-to-date"

        override val fileInvalidMagic: String = "Magic is invalid, needs to be ${DXVK.MAGIC}"
        override val fileReadBytesError: String = "Could not read bytes properly"
        override val fileInvalidEntry: String = "Got invalid entry"
        override val fileUnexpectedEnd: String = "Unexpected end of file"
        override val cacheVersionMismatchPlaceholder: String = "Cache version mismatch, original: %d new: %d"
        override val noDownloadUrlProvided: String = "No download url provided"
        override val downloadFileInvalid: String = "Downloaded file is invalid, try downloading it yourself"

        override val exportSuccessful: String = "Exported file successfully"
        override val openFolder: String = "Open folder"
        override val close: String = "Close"
        override val donate: String = "Donate"
        override val fullAppDescription: String = "The DXVKoTool extracts DXVK caches of games automatically and can update them with newer caches."
        override val unsupportedSystem: String = "Unsupported system"

        override val githubPlaceholder: String = "GitHub: %s"
        override val cacheInformation: String = "Cache Information"
    }

    fun get() = en
}