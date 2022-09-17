package dev.datlag.dxvkotool.network

import dev.datlag.dxvkotool.model.Node
import dev.datlag.dxvkotool.model.RepoInfo
import dev.datlag.dxvkotool.model.RepoStructure
import dev.datlag.dxvkotool.other.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

object OnlineDXVK {

    val dxvkRepoStructureFlow: MutableStateFlow<List<RepoStructure>> = MutableStateFlow(emptyList())

    val selectedNodeFlow: MutableStateFlow<Node?> = MutableStateFlow(null)
    val dxvkRepoNodeFlow = combine(dxvkRepoStructureFlow, selectedNodeFlow) { t1, t2 ->
        t2?.childs ?: t1.map { repo -> repo.toNodeStructure() }.flatten()
    }.flowOn(Dispatchers.IO)

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