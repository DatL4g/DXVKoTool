package dev.datlag.dxvkotool.model

import androidx.compose.ui.graphics.vector.ImageVector

data class UpdateButtonInfo(
    val text: String,
    val icon: ImageVector,
    val isDownload: Boolean,
    val isMerge: Boolean
)
