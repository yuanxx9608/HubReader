package com.example.hubreader.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.unit.dp
import com.example.hubreader.R
import com.example.hubreader.data.local.BookmarkEntity
import com.example.hubreader.data.model.GitHubRepo
import com.example.hubreader.data.model.TimeRange
import com.example.hubreader.ui.components.RepoCard
import com.example.hubreader.viewmodel.BookmarksViewModel
import com.example.hubreader.viewmodel.TrendingViewModel
import com.example.hubreader.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingScreen(
    viewModel: TrendingViewModel,
    bookmarksViewModel: BookmarksViewModel,
    onRepoClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val trendingState by viewModel.trendingState.collectAsState()
    val bookmarkState by bookmarksViewModel.state.collectAsState()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(trendingState) {
        if (trendingState !is UiState.Loading) {
            isRefreshing = false
        }
    }

    val bookmarkedIds = remember(bookmarkState) {
        if (bookmarkState is UiState.Success) {
            (bookmarkState as UiState.Success<List<BookmarkEntity>>).data.map { it.repoId }.toSet()
        } else emptySet()
    }

    fun toggleBookmark(repo: GitHubRepo) {
        if (repo.id in bookmarkedIds) {
            bookmarksViewModel.removeBookmark(repo.id)
        } else {
            bookmarksViewModel.addBookmark(repo)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.trending_title))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeRange.entries.forEach { range ->
                            CompactTimeChip(
                                label = stringResource(range.labelRes),
                                selected = range == selectedTimeRange,
                                onClick = { viewModel.setTimeRange(range) }
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Language filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            items(viewModel.languages) { language ->
                LanguageChip(
                    label = language,
                    selected = language == selectedLanguage,
                    onClick = { viewModel.setLanguage(language) }
                )
            }
        }

        // Repo list with pull-to-refresh
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadTrending()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when (val state = trendingState) {
                is UiState.Loading -> {
                    if (!isRefreshing) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is UiState.Success -> {
                    val repos = state.data
                    if (repos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.no_trending_found))
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

                else -> Unit
            }
        }
    }
}

@Composable
fun CompactTimeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun LanguageChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
