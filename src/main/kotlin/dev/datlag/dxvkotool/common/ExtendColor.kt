package dev.datlag.dxvkotool.common

import androidx.compose.ui.graphics.Color

fun Color.withAlpha(value: Float): Color {
    return Color(this.red, this.green, this.blue, value)
}
