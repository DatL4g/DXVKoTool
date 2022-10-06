package dev.datlag.dxvkotool.network

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Url
import dev.datlag.dxvkotool.model.github.RepoStructure
import dev.datlag.dxvkotool.model.github.StructureItemContent

interface GitHub {
    @GET("repos/{user}/{repo}/git/trees/{branch}?recursive=1")
    suspend fun getRepoStructure(
        @Path("user") user: String,
        @Path("repo") repo: String,
        @Path("branch") branch: String = "master"
    ): RepoStructure

    @GET
    suspend fun getStructureItemContent(
        @Url url: String
    ): StructureItemContent
}
