package dev.datlag.dxvkotool.model.game

data class GamePartition(
    private val _steamGames: List<Game>,
    private val _epicGames: List<Game>,
    private val _otherGames: List<Game>
) {

    val steamGames: List<Game> = _steamGames.sortedBy { it.name }
    val epicGames: List<Game> = _epicGames.sortedBy { it.name }
    val otherGames: List<Game> = _otherGames.sortedBy { it.name }

    companion object {
        fun empty() = GamePartition(
            emptyList(),
            emptyList(),
            emptyList()
        )
    }
}
