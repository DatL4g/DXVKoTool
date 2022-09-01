package dev.datlag.dxvkotool.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AppManifest(
    @SerialName("appid") val appId: String,
    @SerialName("name") val name: String,
    @SerialName("installdir") val installDir: String
) {
    @Transient
    lateinit var location: String
}
