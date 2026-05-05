package com.example.hubreader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hubreader.data.local.AppLanguage
import com.example.hubreader.data.local.SettingsManager
import com.example.hubreader.data.local.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsManager: SettingsManager) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsManager.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val dynamicColor: StateFlow<Boolean> = settingsManager.dynamicColorFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val language: StateFlow<AppLanguage> = settingsManager.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppLanguage.SYSTEM)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsManager.setThemeMode(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { settingsManager.setDynamicColor(enabled) }
    }

    fun setLanguage(language: AppLanguage) {
        // Write to SharedPreferences synchronously so recreate() picks up the new locale
        settingsManager.saveLanguageSync(language)
        viewModelScope.launch { settingsManager.setLanguage(language) }
    }
}
