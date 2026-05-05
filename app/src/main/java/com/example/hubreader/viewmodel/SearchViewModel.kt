package com.example.hubreader.viewmodel

import android.content.Context
import android.content.SharedPreferences
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

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

private const val SEARCH_HISTORY_PREFS = "search_history"
private const val SEARCH_HISTORY_KEY = "history"
private const val MAX_HISTORY = 20
private const val DELIMITER = "|||"

class SearchHistoryManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(SEARCH_HISTORY_PREFS, Context.MODE_PRIVATE)

    fun getHistory(): List<String> {
        val raw = prefs.getString(SEARCH_HISTORY_KEY, "") ?: ""
        return if (raw.isEmpty()) emptyList() else raw.split(DELIMITER)
    }

    fun saveQuery(query: String) {
        val current = getHistory().toMutableList()
        current.remove(query)
        current.add(0, query)
        while (current.size > MAX_HISTORY) {
            current.removeAt(current.lastIndex)
        }
        prefs.edit().putString(SEARCH_HISTORY_KEY, current.joinToString(DELIMITER)).apply()
    }

    fun clearHistory() {
        prefs.edit().remove(SEARCH_HISTORY_KEY).apply()
    }
}

class SearchViewModel(
    private val repository: GitHubRepository,
    private val historyManager: SearchHistoryManager
) : ViewModel() {

    val hotSearchTerms = listOf(
        "machine-learning", "react", "kotlin", "gpt", "llm",
        "rust", "docker", "flutter", "spring-boot", "tensorflow"
    )

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()

    private val _searchState = MutableStateFlow<UiState<List<GitHubRepo>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<GitHubRepo>>> = _searchState.asStateFlow()

    private val _bookmarkState = MutableStateFlow<UiState<List<BookmarkEntity>>>(UiState.Loading)
    val bookmarkState: StateFlow<UiState<List<BookmarkEntity>>> = _bookmarkState.asStateFlow()

    init {
        loadBookmarks()
        loadSearchHistory()
    }

    fun loadSearchHistory() {
        _searchHistory.value = historyManager.getHistory()
    }

    fun saveSearchQuery(query: String) {
        historyManager.saveQuery(query)
        _searchHistory.value = historyManager.getHistory()
    }

    fun clearSearchHistory() {
        historyManager.clearHistory()
        _searchHistory.value = emptyList()
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _searchState.value = UiState.Idle
            return
        }
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            repository.searchRepositories(query).fold(
                onSuccess = { response ->
                    _searchState.value = UiState.Success(response.items)
                },
                onFailure = { e ->
                    _searchState.value = UiState.Error(e.message ?: "Search failed")
                }
            )
        }
    }

    fun searchByLanguage(language: String) {
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            repository.searchByLanguage(language).fold(
                onSuccess = { response ->
                    _searchState.value = UiState.Success(response.items)
                },
                onFailure = { e ->
                    _searchState.value = UiState.Error(e.message ?: "Search failed")
                }
            )
        }
    }

    fun resetToIdle() {
        _searchState.value = UiState.Idle
        loadSearchHistory()
    }

    fun isBookmarked(repoId: Long) = viewModelScope.launch {
        repository.isBookmarked(repoId)
    }

    fun addBookmark(repo: GitHubRepo) = viewModelScope.launch {
        repository.addBookmark(repo)
    }

    fun removeBookmark(repoId: Long) = viewModelScope.launch {
        repository.removeBookmark(repoId)
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            repository.getBookmarks()
                .catch { e -> _bookmarkState.value = UiState.Error(e.message ?: "Failed to load bookmarks") }
                .collect { bookmarks ->
                    _bookmarkState.value = UiState.Success(bookmarks)
                }
        }
    }
}
