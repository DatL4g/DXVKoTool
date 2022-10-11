package dev.datlag.dxvkotool.model.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Branch(
    @SerialName("name") val name: String,
    @SerialName("protected") val protected: Boolean = false
)
