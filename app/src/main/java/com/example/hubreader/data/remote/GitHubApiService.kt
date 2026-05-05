package com.example.hubreader.data.remote

import com.example.hubreader.data.model.GitHubEvent
import com.example.hubreader.data.model.GitHubRepo
import com.example.hubreader.data.model.GitHubSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "best_match",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): GitHubSearchResponse

    @GET("repositories")
    suspend fun getRepositories(
        @Query("since") since: Long,
        @Query("per_page") perPage: Int = 30
    ): List<GitHubRepo>

    @GET("search/repositories")
    suspend fun getTrendingRepositories(
        @Query("q") query: String = "stars:>1",
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): GitHubSearchResponse

    @GET("search/repositories")
    suspend fun getExploreRepositories(
        @Query("q") query: String = "stars:>100",
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): GitHubSearchResponse

    @GET("search/repositories")
    suspend fun getExploreByLanguage(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): GitHubSearchResponse

    @GET("search/repositories")
    suspend fun getExploreByTopic(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): GitHubSearchResponse

    @GET("repos/{owner}/{repo}")
    suspend fun getRepositoryDetail(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GitHubRepo

    @GET("networks/{owner}/{repo}/events")
    suspend fun getRepositoryEvents(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): List<GitHubEvent>

    @GET("search/repositories")
    suspend fun searchByLanguage(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 20
    ): GitHubSearchResponse

    @GET("repos/{owner}/{repo}/readme")
    @retrofit2.http.Headers("Accept: application/vnd.github.html")
    suspend fun getReadmeHtml(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): okhttp3.ResponseBody
}
