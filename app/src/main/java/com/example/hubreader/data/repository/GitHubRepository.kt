package com.example.hubreader.data.repository

import com.example.hubreader.data.local.BookmarkDao
import com.example.hubreader.data.local.BookmarkEntity
import com.example.hubreader.data.model.GitHubRepo
import com.example.hubreader.data.model.GitHubSearchResponse
import com.example.hubreader.data.model.TimeRange
import com.example.hubreader.data.remote.GitHubApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GitHubRepository(
    private val apiService: GitHubApiService,
    private val bookmarkDao: BookmarkDao
) {
    // Search repositories by keyword
    suspend fun searchRepositories(
        query: String,
        sort: String = "best_match",
        page: Int = 1,
        perPage: Int = 30
    ): Result<GitHubSearchResponse> = try {
        Result.success(apiService.searchRepositories(query, sort, "desc", page, perPage))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Get trending repos (sorted by stars, most starred recently)
    suspend fun getTrendingRepositories(
        timeRange: TimeRange = TimeRange.THIS_WEEK,
        language: String = "All",
        page: Int = 1,
        perPage: Int = 30
    ): Result<GitHubSearchResponse> = try {
        val parts = mutableListOf("stars:>1", timeRange.toQuerySuffix())
        if (language != "All") {
            parts.add("language:$language")
        }
        val query = parts.joinToString(" ")
        Result.success(apiService.getTrendingRepositories(query, page = page, perPage = perPage))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Get explore repos (popular open source projects)
    suspend fun getExploreRepositories(
        page: Int = 1,
        perPage: Int = 30
    ): Result<GitHubSearchResponse> = try {
        Result.success(apiService.getExploreRepositories(page = page, perPage = perPage))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Explore by language
    suspend fun exploreByLanguage(
        language: String,
        perPage: Int = 20
    ): Result<GitHubSearchResponse> = try {
        Result.success(apiService.getExploreByLanguage("language:$language", page = 1, perPage = perPage))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Explore by topic
    suspend fun exploreByTopic(
        topic: String,
        perPage: Int = 20
    ): Result<GitHubSearchResponse> = try {
        Result.success(apiService.getExploreByTopic("topic:$topic", page = 1, perPage = perPage))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Get repository detail
    suspend fun getRepositoryDetail(owner: String, repo: String): Result<GitHubRepo> = try {
        Result.success(apiService.getRepositoryDetail(owner, repo))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Search by language
    suspend fun searchByLanguage(
        language: String,
        perPage: Int = 20
    ): Result<GitHubSearchResponse> = try {
        Result.success(apiService.searchByLanguage("language:$language", perPage = perPage))
    } catch (e: Exception) {
        Result.failure(e)
    }

    // Bookmark operations
    fun getBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    suspend fun isBookmarked(repoId: Long): Boolean = bookmarkDao.isBookmarked(repoId)

    suspend fun addBookmark(repo: GitHubRepo) {
        bookmarkDao.insertBookmark(
            BookmarkEntity(
                repoId = repo.id,
                name = repo.name,
                fullName = repo.fullName,
                description = repo.description,
                stargazersCount = repo.stargazersCount,
                forks = repo.forks,
                language = repo.language,
                avatarUrl = repo.owner.avatarUrl,
                htmlUrl = repo.htmlUrl,
                topics = repo.topics.joinToString(","),
                pushedAt = repo.pushedAt,
                updatedAt = repo.updatedAt
            )
        )
    }

    suspend fun removeBookmark(repoId: Long) {
        bookmarkDao.deleteBookmarkById(repoId)
    }

    // Get README HTML
    suspend fun getReadmeHtml(owner: String, repo: String): Result<String> = try {
        val body = apiService.getReadmeHtml(owner, repo)
        Result.success(body.string())
    } catch (e: Exception) {
        Result.failure(e)
    }
}
