package dev.datlag.dxvkotool.common

fun <T> listFrom(vararg list: Collection<T>): List<T> {
    return mutableListOf<T>().apply {
        list.forEach {
            addAll(it)
        }
    }
}

fun <T> setFrom(vararg list: Collection<T>): Set<T> {
    return mutableSetOf<T>().apply {
        list.forEach {
            addAll(it)
        }
    }
}
