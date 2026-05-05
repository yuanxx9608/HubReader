package com.example.hubreader.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.hubreader.R
import com.example.hubreader.data.local.BookmarkEntity
import com.example.hubreader.ui.components.RepoCard
import com.example.hubreader.viewmodel.SearchViewModel
import com.example.hubreader.viewmodel.UiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onRepoClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchState by viewModel.searchState.collectAsState()
    val bookmarkState by viewModel.bookmarkState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()

    val bookmarkedIds = remember(bookmarkState) {
        if (bookmarkState is UiState.Success) {
            (bookmarkState as UiState.Success<List<BookmarkEntity>>).data.map { it.repoId }.toSet()
        } else emptySet()
    }

    fun toggleBookmark(repo: com.example.hubreader.data.model.GitHubRepo) {
        if (repo.id in bookmarkedIds) {
            viewModel.removeBookmark(repo.id)
        } else {
            viewModel.addBookmark(repo)
        }
    }

    fun doSearch(query: String) {
        searchQuery = query
        viewModel.saveSearchQuery(query)
        viewModel.search(query)
    }

    BackHandler(enabled = searchState !is UiState.Idle) {
        searchQuery = ""
        viewModel.resetToIdle()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = {
                if (searchState !is UiState.Idle) {
                    IconButton(onClick = {
                        searchQuery = ""
                        viewModel.resetToIdle()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                } else {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            },
            trailingIcon = {
                if (searchState !is UiState.Idle && searchQuery.isNotBlank()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        viewModel.resetToIdle()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear_history))
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { doSearch(searchQuery) }
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )

        LaunchedEffect(searchQuery) {
            delay(500)
            if (searchQuery.isNotBlank()) {
                viewModel.saveSearchQuery(searchQuery)
                viewModel.search(searchQuery)
            }
        }

        when (val state = searchState) {
            is UiState.Idle -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Search history section
                        if (searchHistory.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 4.dp),
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = stringResource(R.string.recent_searches),
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                TextButton(onClick = { viewModel.clearSearchHistory() }) {
                                    Text(
                                        text = stringResource(R.string.delete_history),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                            ) {
                                searchHistory.forEach { term ->
                                    HistoryChip(
                                        text = term,
                                        onClick = { doSearch(term) }
                                    )
                                }
                            }
                        }

                        // Hot search section
                        Text(
                            text = stringResource(R.string.popular_searches),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.hotSearchTerms.forEach { term ->
                                HotSearchChip(
                                    text = term,
                                    icon = iconForHotSearch(term),
                                    onClick = { doSearch(term) }
                                )
                            }
                        }
                    }
                }
            }

            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                val repos = state.data
                if (repos.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_results))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(repos, key = { it.id }) { repo ->
                            RepoCard(
                                fullName = repo.fullName,
                                description = repo.description,
                                stars = repo.stargazersCount,
                                forks = repo.forks,
                                language = repo.language,
                                avatarUrl = repo.owner.avatarUrl,
                                topics = repo.topics,
                                isBookmarked = repo.id in bookmarkedIds,
                                onBookmarkClick = { toggleBookmark(repo) },
                                onClick = {
                                    val parts = repo.fullName.split("/")
                                    if (parts.size == 2) {
                                        onRepoClick(parts[0], parts[1], repo.fullName)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryChip(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

private fun iconForHotSearch(term: String): ImageVector = when (term) {
    "machine-learning" -> Icons.Default.Psychology
    "react" -> Icons.Default.Refresh
    "kotlin" -> Icons.Default.Android
    "gpt" -> Icons.Default.Chat
    "llm" -> Icons.Default.ChatBubble
    "rust" -> Icons.Default.Build
    "docker" -> Icons.Default.Cloud
    "flutter" -> Icons.Default.Palette
    "spring-boot" -> Icons.Default.FlashOn
    "tensorflow" -> Icons.Default.AccountTree
    else -> Icons.Default.Search
}

@Composable
fun HotSearchChip(text: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
