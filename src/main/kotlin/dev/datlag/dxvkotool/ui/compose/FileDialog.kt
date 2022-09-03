package dev.datlag.dxvkotool.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import dev.datlag.dxvkotool.other.StringRes
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun SaveFileDialog(
    fileName: String,
    parent: Frame? = null,
    onCloseRequest: (file: File?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, StringRes.get().save, SAVE) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    if (directory != null && file != null) {
                        val destFile = File(directory, file)
                        if (destFile.exists()) {
                            if (destFile.canRead() && destFile.canWrite()) {
                                onCloseRequest(destFile)
                            } else {
                                onCloseRequest(null)
                            }
                        } else {
                            if (destFile.parentFile.canWrite()) {
                                onCloseRequest(destFile)
                            } else {
                                onCloseRequest(null)
                            }
                        }
                    } else {
                        onCloseRequest(null)
                    }
                }
            }
        }.apply {
            directory = System.getProperty("user.home")
            file = fileName
        }
    },
    dispose = FileDialog::dispose
)

@Composable
fun LoadFileDialog(
    parent: Frame? = null,
    onCloseRequest: (file: File?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, StringRes.get().load, LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    if (file != null && directory != null) {
                        val loadFile = File(directory, file)
                        if (loadFile.exists() && loadFile.canRead()) {
                            onCloseRequest(loadFile)
                        } else {
                            onCloseRequest(null)
                        }
                    } else {
                        onCloseRequest(null)
                    }
                }
            }
        }.apply {
            directory = System.getProperty("user.home")
            setFilenameFilter { _, name ->
                name.endsWith(".dxvk-cache", true)
            }
        }
    },
    dispose = FileDialog::dispose
)