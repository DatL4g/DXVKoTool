package dev.datlag.dxvkotool.model

data class Node(
    val path: String,
    val item: StructureItem
) {
    val childs: MutableList<Node> = mutableListOf()

    fun hasChilds(): Boolean = childs.isNotEmpty()
}
