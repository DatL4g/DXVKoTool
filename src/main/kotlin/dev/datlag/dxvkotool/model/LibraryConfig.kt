package dev.datlag.dxvkotool.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LibraryConfig(
    @SerialName("path") val path: String
)
