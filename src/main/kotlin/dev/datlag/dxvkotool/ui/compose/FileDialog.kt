package dev.datlag.dxvkotool.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.AwtWindow
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@Composable
fun FileDialog(
    fileName: String,
    parent: Frame? = null,
    onCloseRequest: (file: File?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Save to", SAVE) {
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