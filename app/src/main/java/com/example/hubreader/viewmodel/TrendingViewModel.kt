package com.example.hubreader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hubreader.data.model.GitHubRepo
import com.example.hubreader.data.model.TimeRange
import com.example.hubreader.data.model.TrendingLanguages
import com.example.hubreader.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrendingViewModel(private val repository: GitHubRepository) : ViewModel() {

    private val _trendingState = MutableStateFlow<UiState<List<GitHubRepo>>>(UiState.Loading)
    val trendingState: StateFlow<UiState<List<GitHubRepo>>> = _trendingState.asStateFlow()

    private val _selectedTimeRange = MutableStateFlow(TimeRange.THIS_WEEK)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("All")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    val languages = TrendingLanguages.items

    init {
        loadTrending()
    }

    fun setTimeRange(range: TimeRange) {
        _selectedTimeRange.value = range
        loadTrending()
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        loadTrending()
    }

    fun loadTrending() {
        viewModelScope.launch {
            _trendingState.value = UiState.Loading
            repository.getTrendingRepositories(
                timeRange = _selectedTimeRange.value,
                language = _selectedLanguage.value
            ).fold(
                onSuccess = { response ->
                    _trendingState.value = UiState.Success(response.items)
                },
                onFailure = { e ->
                    _trendingState.value = UiState.Error(e.message ?: "Failed to load trending repos")
                }
            )
        }
    }
}
