package dev.datlag.dxvkotool.network

import dev.datlag.dxvkotool.model.RepoInfo
import dev.datlag.dxvkotool.model.RepoStructure
import dev.datlag.dxvkotool.other.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

object OnlineDXVK {

    val dxvkRepoStructureFlow: MutableStateFlow<List<RepoStructure>> = MutableStateFlow(emptyList())

    private val repoMap: Set<RepoInfo> = setOf(
        RepoInfo(
            "begin-theadventure",
            "dxvk-caches",
            "main"
        )
    )

    fun getDXVKCaches(scope: CoroutineScope) = scope.launch(Dispatchers.IO) {
        val structures = repoMap.map {
            async {
                runCatching {
                    Constants.githubService.getRepoStructure(it.owner, it.repo, it.branch)
                }.getOrNull()
            }
        }.awaitAll().filterNotNull()

        dxvkRepoStructureFlow.emit(structures)
    }
}