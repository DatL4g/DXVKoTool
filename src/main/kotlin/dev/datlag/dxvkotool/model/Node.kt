package dev.datlag.dxvkotool.model

data class Node(val path: String) {
    val childs: MutableList<Node> = mutableListOf()

    fun hasChilds(): Boolean = childs.isNotEmpty()
}
