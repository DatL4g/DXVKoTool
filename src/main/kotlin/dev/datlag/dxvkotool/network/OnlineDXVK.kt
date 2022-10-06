package dev.datlag.dxvkotool.network

import dev.datlag.dxvkotool.model.github.Node
import dev.datlag.dxvkotool.model.github.RepoInfo
import dev.datlag.dxvkotool.model.github.RepoStructure
import dev.datlag.dxvkotool.other.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

object OnlineDXVK {

    val dxvkRepoStructureFlow: MutableStateFlow<List<RepoStructure>> = MutableStateFlow(emptyList())

    val selectedNodeFlow: MutableStateFlow<Node?> = MutableStateFlow(null)
    val dxvkRepoNodeFlow = combine(dxvkRepoStructureFlow, selectedNodeFlow) { t1, t2 ->
        t2?.childs ?: t1.map { repo -> repo.toNodeStructure() }.flatten()
    }.flowOn(Dispatchers.IO)

    private val repoMap: Set<RepoInfo> = setOf(
        RepoInfo(
            Constants.dxvkRepoOwner,
            Constants.dxvkRepo,
            Constants.dxvkRepoBranch
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
