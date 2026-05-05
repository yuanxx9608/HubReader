package com.example.hubreader.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepo(
    val id: Long,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("html_url")
    val htmlUrl: String,
    val description: String?,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
    @SerialName("forks_count")
    val forks: Int,
    @SerialName("open_issues")
    val openIssues: Int,
    val language: String?,
    @SerialName("pushed_at")
    val pushedAt: String?,
    @SerialName("created_at")
    val createdAt: String?,
    @SerialName("updated_at")
    val updatedAt: String?,
    val owner: GitHubOwner,
    val topics: List<String> = emptyList(),
    @SerialName("watchers_count")
    val watchersCount: Int = 0,
    @SerialName("default_branch")
    val defaultBranch: String = "main",
    val visibility: String = "public"
)
