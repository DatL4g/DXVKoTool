package dev.datlag.dxvkotool.common

import kotlin.math.log2
import kotlin.math.pow

fun Long.toHumanReadableBytes(): String = log2(if (this != 0L) toDouble() else 1.0).toInt().div(10).let {
    val precision = when (it) {
        0 -> 0; 1 -> 1; else -> 2
    }
    val prefix = arrayOf("", "K", "M", "G", "T", "P", "E", "Z", "Y")
    String.format("%.${precision}f ${prefix[it]}B", toDouble() / 2.0.pow(it * 10.0))
}
