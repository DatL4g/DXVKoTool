package dev.datlag.dxvkotool.common

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        this.then(modifier.invoke(Modifier))
    } else {
        this
    }
}

fun Modifier.conditionalBackground(condition: Boolean, trueColor: Color, falseColor: Color): Modifier {
    return this.conditional(condition) {
        background(trueColor)
    }.conditional(!condition) {
        background(falseColor)
    }
}
