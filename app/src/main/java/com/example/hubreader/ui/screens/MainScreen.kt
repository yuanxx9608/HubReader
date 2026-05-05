package com.example.hubreader.ui.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hubreader.data.local.BookmarkDatabase
import com.example.hubreader.data.local.SettingsManager
import com.example.hubreader.data.remote.RetrofitClient
import com.example.hubreader.data.repository.GitHubRepository
import com.example.hubreader.ui.navigation.Screen
import com.example.hubreader.viewmodel.BookmarksViewModel
import com.example.hubreader.viewmodel.BookmarksViewModelFactory
import com.example.hubreader.viewmodel.SearchHistoryManager
import com.example.hubreader.viewmodel.SearchViewModel
import com.example.hubreader.viewmodel.SearchViewModelFactory
import com.example.hubreader.viewmodel.SettingsViewModel
import com.example.hubreader.viewmodel.SettingsViewModelFactory
import com.example.hubreader.viewmodel.TrendingViewModel
import com.example.hubreader.viewmodel.TrendingViewModelFactory

@Composable
fun MainScreen(
    bookmarkDatabase: BookmarkDatabase,
    settingsManager: SettingsManager
) {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = GitHubRepository(RetrofitClient.apiService, bookmarkDatabase.bookmarkDao())
    val historyManager = remember { SearchHistoryManager(context) }

    val searchViewModel: SearchViewModel = viewModel(
        factory = SearchViewModelFactory(repository, historyManager)
    )
    val trendingViewModel: TrendingViewModel = viewModel(
        factory = TrendingViewModelFactory(repository)
    )
    val bookmarksViewModel: BookmarksViewModel = viewModel(
        factory = BookmarksViewModelFactory(repository)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(settingsManager)
    )

    fun onRepoClick(owner: String, repo: String, fullName: String) {
        navController.navigate("repo_detail/$owner/$repo?fullName=$fullName")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val isDetailRoute = currentDestination?.route?.startsWith("repo_detail") == true

            if (!isDetailRoute) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    tonalElevation = 3.dp
                ) {
                    Screen.items.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = stringResource(screen.titleRes)
                                )
                            },
                            label = { Text(stringResource(screen.titleRes)) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onRepoClick = ::onRepoClick
                )
            }
            composable(Screen.Trending.route) {
                TrendingScreen(
                    viewModel = trendingViewModel,
                    bookmarksViewModel = bookmarksViewModel,
                    onRepoClick = ::onRepoClick
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
            composable(Screen.Bookmarks.route) {
                BookmarksScreen(
                    viewModel = bookmarksViewModel,
                    onRepoClick = ::onRepoClick
                )
            }
            composable(
                route = "repo_detail/{owner}/{repo}?fullName={fullName}",
            ) { backStackEntry ->
                val owner = backStackEntry.arguments?.getString("owner") ?: ""
                val repo = backStackEntry.arguments?.getString("repo") ?: ""
                val fullName = backStackEntry.arguments?.getString("fullName") ?: ""
                RepoDetailScreen(
                    owner = owner,
                    repo = repo,
                    fullName = fullName,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
