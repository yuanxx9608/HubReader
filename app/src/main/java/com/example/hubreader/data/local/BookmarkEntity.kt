package com.example.hubreader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val repoId: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val stargazersCount: Int,
    val forks: Int,
    val language: String?,
    val avatarUrl: String,
    val htmlUrl: String,
    val topics: String,
    val pushedAt: String?,
    val updatedAt: String?,
    val bookmarkedAt: Long = System.currentTimeMillis()
)
