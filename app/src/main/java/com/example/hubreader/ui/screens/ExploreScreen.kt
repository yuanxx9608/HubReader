package com.example.hubreader.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hubreader.R
import com.example.hubreader.data.local.BookmarkEntity
import com.example.hubreader.data.model.GitHubRepo
import com.example.hubreader.ui.components.RepoCard
import com.example.hubreader.viewmodel.BookmarksViewModel
import com.example.hubreader.viewmodel.ExploreViewModel
import com.example.hubreader.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    bookmarksViewModel: BookmarksViewModel
) {
    val context = LocalContext.current
    val exploreState by viewModel.exploreState.collectAsState()
    val categoryState by viewModel.categoryState.collectAsState()
    val bookmarkState by bookmarksViewModel.state.collectAsState()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

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
                Text(
                    text = selectedCategory ?: stringResource(R.string.explore_title),
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            navigationIcon = {
                if (selectedCategory != null) {
                    IconButton(onClick = {
                        selectedCategory = null
                        viewModel.resetCategory()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        if (selectedCategory == null) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Languages section
                item {
                    Text(
                        text = stringResource(R.string.popular_languages),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.popularLanguages) { language ->
                            CategoryChip(
                                label = language,
                                icon = Icons.Default.Language,
                                onClick = {
                                    selectedCategory = language
                                    viewModel.loadByLanguage(language)
                                }
                            )
                        }
                    }
                }

                // Topics section
                item {
                    Text(
                        text = stringResource(R.string.popular_topics),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.popularTopics) { topic ->
                            CategoryChip(
                                label = topic.replace("-", " "),
                                icon = Icons.Default.Code,
                                onClick = {
                                    selectedCategory = topic
                                    viewModel.loadByTopic(topic)
                                }
                            )
                        }
                    }
                }

                // Top repos section
                item {
                    Text(
                        text = stringResource(R.string.top_repositories),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }

                when (val state = exploreState) {
                    is UiState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    is UiState.Success -> {
                        val repos = state.data
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
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }

                    is UiState.Error -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    else -> Unit
                }
            }
        } else {
            // Category view
            when (val state = categoryState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is UiState.Success -> {
                    val repos = state.data
                    if (repos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.no_category_repos))
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
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))
                                        context.startActivity(intent)
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
fun CategoryChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
