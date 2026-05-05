package com.example.hubreader

import android.content.Context
import android.os.Bundle
import android.os.Build
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.hubreader.data.local.BookmarkDatabase
import com.example.hubreader.data.local.SettingsManager
import com.example.hubreader.data.local.ThemeMode
import com.example.hubreader.ui.screens.MainScreen
import com.example.hubreader.ui.theme.HubreaderTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val bookmarkDatabase by lazy {
        BookmarkDatabase.getDatabase(this)
    }

    private val settingsManager by lazy {
        SettingsManager(this)
    }

    private var themeMode by mutableStateOf(ThemeMode.SYSTEM)
    private var dynamicColor by mutableStateOf(true)

    override fun attachBaseContext(newBase: Context) {
        val settingsManager = SettingsManager(newBase)
        val locale = settingsManager.getSavedLocale()
        if (locale != null) {
            val config = newBase.resources.configuration.apply {
                setLocales(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) LocaleList(locale) else null)
            }
            super.attachBaseContext(newBase.createConfigurationContext(config))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            settingsManager.themeModeFlow.collect { mode -> themeMode = mode }
        }
        lifecycleScope.launch {
            settingsManager.dynamicColorFlow.collect { color -> dynamicColor = color }
        }

        setContent {
            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            HubreaderTheme(
                darkTheme = isDark,
                dynamicColor = dynamicColor
            ) {
                MainScreen(
                    bookmarkDatabase = bookmarkDatabase,
                    settingsManager = settingsManager
                )
            }
        }
    }
}
