package com.example.hubreader.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubSearchResponse(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<GitHubRepo>
)

@Serializable
data class GitHubEvent(
    val id: String,
    val type: String,
    val repo: EventRepo,
    val actor: EventActor,
    @SerialName("created_at")
    val createdAt: String
) {
    @Serializable
    data class EventRepo(
        val id: Long,
        val name: String,
        val url: String
    )

    @Serializable
    data class EventActor(
        val id: Long,
        val login: String,
        @SerialName("avatar_url")
        val avatarUrl: String,
        @SerialName("gravatar_id")
        val gravatarId: String,
        val url: String,
        @SerialName("html_url")
        val htmlUrl: String
    )
}
