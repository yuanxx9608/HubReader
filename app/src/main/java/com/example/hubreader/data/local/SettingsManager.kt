package com.example.hubreader.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode { LIGHT, DARK, SYSTEM }
enum class AppLanguage(val code: String?) { SYSTEM(null), ENGLISH("en"), CHINESE("zh") }

class SettingsManager(context: Context) {
    private val dataStore = context.dataStore
    private val prefs: SharedPreferences = context.getSharedPreferences("settings_sync", Context.MODE_PRIVATE)

    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val dynamicColorKey = booleanPreferencesKey("dynamic_color")
    private val languageKey = "app_language"

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[themeModeKey] = mode.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[dynamicColorKey] = enabled }
    }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[stringPreferencesKey("app_language")] = language.name }
    }

    /**
     * Synchronous write to SharedPreferences for immediate effect on Activity.recreate().
     */
    fun saveLanguageSync(language: AppLanguage) {
        prefs.edit().putString(languageKey, language.name).commit()
    }

    val themeModeFlow: Flow<ThemeMode> = dataStore.data
        .map { prefs ->
            val name = prefs[themeModeKey] ?: ThemeMode.SYSTEM.name
            runCatching { ThemeMode.valueOf(name) }.getOrDefault(ThemeMode.SYSTEM)
        }

    val dynamicColorFlow: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[dynamicColorKey] != false }

    val languageFlow: Flow<AppLanguage> = dataStore.data
        .map { datastorePrefs ->
            val name = datastorePrefs[stringPreferencesKey("app_language")]
                ?: prefs.getString(languageKey, null)
                ?: AppLanguage.SYSTEM.name
            runCatching { AppLanguage.valueOf(name) }.getOrDefault(AppLanguage.SYSTEM)
        }

    /**
     * Synchronously read saved locale from SharedPreferences.
     * Called in Activity.attachBaseContext before coroutines are available.
     */
    fun getSavedLocale(): Locale? {
        val name = prefs.getString(languageKey, null) ?: return null
        val lang = runCatching { AppLanguage.valueOf(name) }.getOrDefault(AppLanguage.SYSTEM)
        return lang.code?.let { Locale(it) }
    }
}
