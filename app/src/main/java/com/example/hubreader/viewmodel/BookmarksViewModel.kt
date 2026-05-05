package com.example.hubreader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hubreader.data.local.BookmarkEntity
import com.example.hubreader.data.model.GitHubRepo
import com.example.hubreader.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class BookmarksViewModel(private val repository: GitHubRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<BookmarkEntity>>>(UiState.Loading)
    val state: StateFlow<UiState<List<BookmarkEntity>>> = _state.asStateFlow()

    private val _bookmarkStatus = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val bookmarkStatus: StateFlow<Map<Long, Boolean>> = _bookmarkStatus.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            repository.getBookmarks()
                .catch { e -> _state.value = UiState.Error(e.message ?: "Failed to load bookmarks") }
                .collect { bookmarks ->
                    _state.value = UiState.Success(bookmarks)
                    val statusMap = bookmarks.associate { it.repoId to true }
                    _bookmarkStatus.value = statusMap
                }
        }
    }

    fun removeBookmark(repoId: Long) = viewModelScope.launch {
        repository.removeBookmark(repoId)
    }

    fun addBookmark(repo: GitHubRepo) = viewModelScope.launch {
        repository.addBookmark(repo)
    }

    fun isBookmarked(repoId: Long): Boolean = _bookmarkStatus.value[repoId] == true
}
