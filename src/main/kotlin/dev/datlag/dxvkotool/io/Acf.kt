package dev.datlag.dxvkotool.io

import dev.datlag.dxvkotool.model.AppManifest
import dev.datlag.dxvkotool.other.Constants
import kotlinx.serialization.decodeFromString

object Acf {
    fun toJson(value: String): String {
        return ("{${value.substringAfter('{')}").replace(Constants.ACF_ALL_ENDING_WITH_COMMA.toRegex()) {
            "${it.value},"
        }.replace(Constants.ACF_ALL_ENDING_WITH_COLON.toRegex()) {
            "${it.value}:"
        }.replace(Constants.ACF_ALL_ENDING_WITH_PARENTHESIS.toRegex()) {
            if (it.next() == null) {
                it.value
            } else {
                "${it.value},"
            }
        }
    }

    fun toAppManifest(value: String, location: String): Result<AppManifest> = runCatching {
        Constants.json.decodeFromString<AppManifest>(toJson(value)).apply {
            this.location = location
        }
    }
}