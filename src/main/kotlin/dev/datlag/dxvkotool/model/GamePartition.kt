package dev.datlag.dxvkotool.model

data class GamePartition(
    val steamGames: List<Game>,
    val epicGames: List<Game>,
    val otherGames: List<Game>
)
