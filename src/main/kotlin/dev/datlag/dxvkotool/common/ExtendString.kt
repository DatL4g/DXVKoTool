package dev.datlag.dxvkotool.common

import dev.datlag.dxvkotool.other.StringRes
import java.awt.Desktop
import java.net.URI


fun String.openInBrowser() = runCatching {
    val openBrowserCommands = arrayOf(
        arrayOf("xdg-open", "$1"),
        arrayOf("gio", "open", "$1"),
        arrayOf("gvfs-open", "$1"),
        arrayOf("gnome-open", "$1"),
        arrayOf("mate-open", "$1"),
        arrayOf("exo-open", "$1"),
        arrayOf("enlightenment_open", "$1"),
        arrayOf(
            "gdbus", "call", "--session", "--dest", "org.freedesktop.portal.Desktop",
            "--object-path", "/org/freedesktop/portal/desktop",
            "--method", "org.freedesktop.portal.OpenURI.OpenURI",
            "", "$1", "{}"
        ),
        arrayOf("open", "$1"),
        arrayOf("rundll32", "url.dll,FileProtocolHandler", "$1")
    )

    openBrowserCommands.forEach { browser ->
        try {
            val command = arrayOfNulls<String>(browser.size)
            for (i in browser.indices) {
                if (browser[i] == "$1") {
                    command[i] = this
                } else {
                    command[i] = browser[i]
                }
            }
            if (Runtime.getRuntime().exec(command).waitFor() == 0) {
                return@runCatching
            }
        } catch (ignored: Throwable) {
        }
    }

    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        Desktop.getDesktop().browse(URI.create(this))
    } else {
        throw IllegalStateException(StringRes.get().unsupportedSystem)
    }
}

fun getBuildVersion(): String {
    return systemProperty("jpackage.app-version") ?: StringRes.get().unknown
}

fun systemProperty(key: String): String? = try {
    System.getProperty(key).ifEmpty {
        null
    }
} catch (ignored: Throwable) {
    null
}

fun systemEnv(key: String): String? = try {
    System.getenv(key).ifEmpty {
        null
    }
} catch (ignored: Throwable) {
    null
}
