package com.example.hubreader.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.hubreader.ui.theme.GitHubBlue
import com.example.hubreader.ui.theme.GitHubDark
import com.example.hubreader.ui.theme.GitHubDarkBorder
import com.example.hubreader.ui.theme.GitHubDarkSurface
import com.example.hubreader.ui.theme.GitHubGreen
import com.example.hubreader.ui.theme.GitHubPurple

private val DarkColorScheme = darkColorScheme(
    primary = GitHubBlue,
    secondary = GitHubGreen,
    tertiary = GitHubPurple,
    background = GitHubDark,
    surface = GitHubDarkSurface,
    surfaceVariant = GitHubDarkBorder,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF8B949E),
    outline = Color(0xFF484F58)
)

private val LightColorScheme = lightColorScheme(
    primary = GitHubBlue,
    secondary = GitHubGreen,
    tertiary = GitHubPurple,
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color(0xFFF6F8FA),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF24292F),
    onSurface = Color(0xFF24292F),
    onSurfaceVariant = Color(0xFF57606A),
    outline = Color(0xFFD0D7DE)
)

@Composable
fun HubreaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
