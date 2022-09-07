package dev.datlag.dxvkotool.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.AwtWindow
import dev.datlag.dxvkotool.common.canReadSafely
import dev.datlag.dxvkotool.common.canWriteSafely
import dev.datlag.dxvkotool.common.existsSafely
import dev.datlag.dxvkotool.other.Constants
import dev.datlag.dxvkotool.other.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

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
                        if (destFile.existsSafely()) {
                            if (destFile.canReadSafely() && destFile.canWriteSafely()) {
                                onCloseRequest(destFile)
                            } else {
                                onCloseRequest(null)
                            }
                        } else {
                            if (destFile.parentFile.canWriteSafely()) {
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
                        if (loadFile.existsSafely() && loadFile.canReadSafely()) {
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

@Composable
fun SaveJFileDialog(
    fileName: String,
    onCloseRequest: (file: File?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val fileChooser = JFileChooser(System.getProperty("user.home"))
    fileChooser.selectedFile = File(fileName)

    coroutineScope.launch(Dispatchers.IO) {
        val option = fileChooser.showSaveDialog(null)

        withContext(Dispatchers.Main) {
            if (option == JFileChooser.APPROVE_OPTION) {
                val destFile: File? = fileChooser.selectedFile
                if (destFile == null) {
                    onCloseRequest(null)
                } else {
                    if (destFile.existsSafely()) {
                        if (destFile.canReadSafely() && destFile.canWriteSafely()) {
                            onCloseRequest(destFile)
                        } else {
                            onCloseRequest(null)
                        }
                    } else {
                        if (destFile.parentFile.canWriteSafely()) {
                            onCloseRequest(destFile)
                        } else {
                            onCloseRequest(null)
                        }
                    }
                }
            } else {
                onCloseRequest(null)
            }
        }
    }
}

@Composable
fun LoadJFileDialog(
    onCloseRequest: (file: File?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val fileChooser = JFileChooser(System.getProperty("user.home"))
    fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY
    fileChooser.fileFilter = FileNameExtensionFilter("Game cache", "dxvk-cache")

    coroutineScope.launch(Dispatchers.IO) {
        val option = fileChooser.showOpenDialog(null)

        withContext(Dispatchers.Main) {
            if (option == JFileChooser.APPROVE_OPTION) {
                val loadFile: File? = fileChooser.selectedFile
                if (loadFile == null) {
                    onCloseRequest(null)
                } else {
                    if (loadFile.existsSafely() && loadFile.canReadSafely()) {
                        onCloseRequest(loadFile)
                    } else {
                        onCloseRequest(null)
                    }
                }
            } else {
                onCloseRequest(null)
            }
        }
    }
}

@Composable
fun CombinedSaveFileDialog(
    fileName: String,
    onCloseRequest: (file: File?) -> Unit
) {
    val desktop = runCatching {
        System.getenv("XDG_CURRENT_DESKTOP")
    }.getOrNull() ?: String()
    if (desktop.equals(Constants.GNOME, true)) {
        SaveFileDialog(fileName, null, onCloseRequest)
    } else {
        SaveJFileDialog(fileName, onCloseRequest)
    }
}

@Composable
fun CombinedLoadFileDialog(
    onCloseRequest: (file: File?) -> Unit
) {
    val desktop = runCatching {
        System.getenv("XDG_CURRENT_DESKTOP")
    }.getOrNull() ?: String()
    if (desktop.equals(Constants.GNOME, true)) {
        LoadFileDialog(null, onCloseRequest)
    } else {
        LoadJFileDialog(onCloseRequest)
    }
}