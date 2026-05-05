package com.example.hubreader.ui.screens

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import com.example.hubreader.data.local.AppLanguage
import com.example.hubreader.data.local.ThemeMode
import com.example.hubreader.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val dynamicColor by viewModel.dynamicColor.collectAsState()
    val language by viewModel.language.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(com.example.hubreader.R.string.settings_title.asString()) },
            navigationIcon = {
                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Theme section
            Text(
                text = com.example.hubreader.R.string.settings_theme.asString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            var themeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = themeExpanded,
                onExpandedChange = { themeExpanded = !themeExpanded }
            ) {
                TextField(
                    value = themeMode.labelRes.asString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = themeExpanded,
                    onDismissRequest = { themeExpanded = false }
                ) {
                    ThemeMode.entries.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.labelRes.asString()) },
                            onClick = {
                                viewModel.setThemeMode(mode)
                                themeExpanded = false
                            }
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Appearance section
            Text(
                text = com.example.hubreader.R.string.settings_appearance.asString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            val isDynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = com.example.hubreader.R.string.settings_dynamic_color.asString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDynamicColorAvailable)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.outline
                )
                Switch(
                    checked = dynamicColor && isDynamicColorAvailable,
                    enabled = isDynamicColorAvailable,
                    onCheckedChange = { viewModel.setDynamicColor(it) }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Language section
            Text(
                text = com.example.hubreader.R.string.settings_language.asString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            var langExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = langExpanded,
                onExpandedChange = { langExpanded = !langExpanded }
            ) {
                TextField(
                    value = language.labelRes.asString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = langExpanded,
                    onDismissRequest = { langExpanded = false }
                ) {
                    AppLanguage.entries.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang.labelRes.asString()) },
                            onClick = {
                                viewModel.setLanguage(lang)
                                langExpanded = false
                                (context as? ComponentActivity)?.recreate()
                            }
                        )
                    }
                }
            }
        }
    }
}

private val ThemeMode.labelRes: Int
    get() = when (this) {
        ThemeMode.LIGHT -> com.example.hubreader.R.string.theme_light
        ThemeMode.DARK -> com.example.hubreader.R.string.theme_dark
        ThemeMode.SYSTEM -> com.example.hubreader.R.string.theme_system
    }

private val AppLanguage.labelRes: Int
    get() = when (this) {
        AppLanguage.SYSTEM -> com.example.hubreader.R.string.lang_system
        AppLanguage.ENGLISH -> com.example.hubreader.R.string.lang_english
        AppLanguage.CHINESE -> com.example.hubreader.R.string.lang_chinese
    }

@Composable
private fun Int.asString(): String = LocalContext.current.getString(this)
