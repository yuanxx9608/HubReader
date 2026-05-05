package com.example.hubreader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hubreader.data.model.GitHubRepo
import com.example.hubreader.data.repository.GitHubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(private val repository: GitHubRepository) : ViewModel() {

    private val _exploreState = MutableStateFlow<UiState<List<GitHubRepo>>>(UiState.Loading)
    val exploreState: StateFlow<UiState<List<GitHubRepo>>> = _exploreState.asStateFlow()

    private val _categoryState = MutableStateFlow<UiState<List<GitHubRepo>>>(UiState.Idle)
    val categoryState: StateFlow<UiState<List<GitHubRepo>>> = _categoryState.asStateFlow()

    val popularLanguages = listOf(
        "Kotlin", "TypeScript", "Python", "Rust", "Go",
        "JavaScript", "Java", "Swift", "C++", "C#"
    )

    val popularTopics = listOf(
        "machine-learning", "react", "docker", "flutter",
        "android", "ai", "blockchain", "vue", "spring-boot", "gaming"
    )

    init {
        loadExplore()
    }

    fun loadExplore() {
        viewModelScope.launch {
            _exploreState.value = UiState.Loading
            repository.getExploreRepositories().fold(
                onSuccess = { response ->
                    _exploreState.value = UiState.Success(response.items)
                },
                onFailure = { e ->
                    _exploreState.value = UiState.Error(e.message ?: "Failed to load explore repos")
                }
            )
        }
    }

    fun loadByLanguage(language: String) {
        viewModelScope.launch {
            _categoryState.value = UiState.Loading
            repository.exploreByLanguage(language).fold(
                onSuccess = { response ->
                    _categoryState.value = UiState.Success(response.items)
                },
                onFailure = { e ->
                    _categoryState.value = UiState.Error(e.message ?: "Failed to load")
                }
            )
        }
    }

    fun loadByTopic(topic: String) {
        viewModelScope.launch {
            _categoryState.value = UiState.Loading
            repository.exploreByTopic(topic).fold(
                onSuccess = { response ->
                    _categoryState.value = UiState.Success(response.items)
                },
                onFailure = { e ->
                    _categoryState.value = UiState.Error(e.message ?: "Failed to load")
                }
            )
        }
    }

    fun resetCategory() {
        _categoryState.value = UiState.Idle
    }
}
