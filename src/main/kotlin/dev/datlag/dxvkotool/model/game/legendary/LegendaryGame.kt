package dev.datlag.dxvkotool.model.game.legendary

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LegendaryGame(
    @SerialName("install_path") val installPath: String,
    @SerialName("title") val title: String
)
