package com.example.hubreader.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.hubreader.R

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Search : Screen("search", R.string.search, Icons.Filled.Search, Icons.Outlined.Search)
    object Trending : Screen("trending", R.string.trending, Icons.Filled.Star, Icons.Outlined.Star)
    object Settings : Screen("settings", R.string.settings, Icons.Filled.Settings, Icons.Outlined.Settings)
    object Bookmarks : Screen("bookmarks", R.string.bookmarks, Icons.Filled.Bookmark, Icons.Outlined.Bookmark)

    companion object {
        val items = listOf(Search, Trending, Bookmarks, Settings)
    }
}
