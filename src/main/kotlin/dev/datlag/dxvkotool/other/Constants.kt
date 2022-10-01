package dev.datlag.dxvkotool.other

import de.jensklingenberg.ktorfit.create
import de.jensklingenberg.ktorfit.ktorfit
import dev.datlag.dxvkotool.common.systemProperty
import dev.datlag.dxvkotool.network.GitHub
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.apache.commons.io.FileUtils
import org.apache.tika.Tika
import java.time.format.DateTimeFormatter

object Constants {
    const val STEAM_DEFAULT_ROOT = ".steam/steam/steamapps/"
    const val STEAM_FLATPAK_ROOT = ".var/app/com.valvesoftware.Steam/.steam/steam/steamapps/"

    const val STEAM_SHADER_DEFAULT_ROOT = "$STEAM_DEFAULT_ROOT/shadercache/"
    const val STEAM_SHADER_FLATPAK_ROOT = "$STEAM_FLATPAK_ROOT/shadercache/"

    const val ACF_ALL_ENDING_WITH_COMMA = "\"\\S+\"\\s+\"(\\S|[ ])+\"(?!(\\s+)?})"
    const val ACF_ALL_ENDING_WITH_COLON = "\"(\\S|[ ])+\"(?!([,]|\\s+(}|])))"
    const val ACF_ALL_ENDING_WITH_PARENTHESIS = "(}|])(?!([,]|\\s+(}|])))"

    const val SYSTEM_DEFAULT_LEGENDARY = ".config/legendary/"
    const val HEROIC_FLATPAK_LEGENDARY = ".var/app/com.heroicgameslauncher.hgl/config/legendary/"

    const val githubApiBaseUrl = "https://api.github.com/"

    const val githubProjectLink = "https://github.com/DATL4G/DXVKoTool"
    const val githubSponsorLink = "https://github.com/sponsors/DATL4G"

    const val GNOME = "GNOME"
    val tikaCore = Tika()

    val json = Json {
        isLenient = true
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    val githubKtorfit = ktorfit {
        baseUrl(githubApiBaseUrl)
        httpClient(httpClient)
    }

    val githubService = githubKtorfit.create<GitHub>()

    val userDir: String = systemProperty("user.home") ?: FileUtils.getUserDirectoryPath()
    val defaultDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
}
