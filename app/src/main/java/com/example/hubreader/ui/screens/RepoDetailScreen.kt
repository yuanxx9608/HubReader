package com.example.hubreader.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.hubreader.R
import com.example.hubreader.data.model.GitHubRepo
import com.example.hubreader.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RepoDetailScreen(
    owner: String,
    repo: String,
    fullName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var repoDetail by remember { mutableStateOf<GitHubRepo?>(null) }
    var readmeHtml by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(owner, repo) {
        isLoading = true
        error = null
        readmeHtml = null
        repoDetail = null

        try {
            coroutineScope {
                val detailDeferred = async(Dispatchers.IO) {
                    RetrofitClient.apiService.getRepositoryDetail(owner, repo)
                }
                val readmeDeferred = async(Dispatchers.IO) {
                    try {
                        RetrofitClient.apiService.getReadmeHtml(owner, repo)
                    } catch (_: Exception) {
                        null
                    }
                }

                repoDetail = detailDeferred.await()
                val readmeBody = readmeDeferred.await()
                readmeHtml = readmeBody?.string()
            }
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = error ?: stringResource(R.string.error_loading),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Repository info card
                    repoDetail?.let { detail ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                detail.description?.let { desc ->
                                    Text(
                                        text = desc,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatItem(
                                        icon = Icons.Default.Star,
                                        value = formatCount(detail.stargazersCount),
                                        label = stringResource(R.string.repo_stars)
                                    )
                                    StatItem(
                                        value = formatCount(detail.forks),
                                        label = stringResource(R.string.repo_forks)
                                    )
                                    StatItem(
                                        value = formatCount(detail.watchersCount),
                                        label = stringResource(R.string.repo_watchers)
                                    )
                                    StatItem(
                                        value = formatCount(detail.openIssues),
                                        label = stringResource(R.string.repo_open_issues)
                                    )
                                }

                                if (detail.topics.isNotEmpty()) {
                                    FlowRow(
                                        modifier = Modifier.padding(top = 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        detail.topics.forEach { topic ->
                                            Text(
                                                text = topic,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                detail.language?.let { lang ->
                                    Text(
                                        text = lang,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // README header
                    Text(
                        text = stringResource(R.string.repo_readme),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    // README content
                    if (readmeHtml != null) {
                        val bgColor = MaterialTheme.colorScheme.background
                        val onBgColor = MaterialTheme.colorScheme.onBackground
                        val bgArgb = colorToArgb(bgColor)
                        val bgHex = String.format("#%06X", 0xFFFFFF and bgArgb)
                        val textHex = String.format("#%06X", 0xFFFFFF and colorToArgb(onBgColor))
                        val isDark = (bgColor.red * 0.299f + bgColor.green * 0.587f + bgColor.blue * 0.114f) < 0.5f
                        val linkColor = if (isDark) "#58a6ff" else "#0969da"
                        val pageHtml = remember(readmeHtml, bgHex, textHex, linkColor) {
                            buildReadmePage(readmeHtml!!, bgHex, textHex, linkColor)
                        }
                        var webViewHeight by remember { mutableStateOf(400.dp) }

                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            view?.evaluateJavascript(
                                                "(function() { return Math.max(document.body.scrollHeight, document.documentElement.scrollHeight); })();"
                                            ) { result ->
                                                val height = result?.trim('"')?.toFloatOrNull() ?: 0f
                                                if (height > 0) {
                                                    webViewHeight = height.dp
                                                }
                                            }
                                        }
                                    }
                                    settings.javaScriptEnabled = true
                                    settings.loadsImagesAutomatically = true
                                }
                            },
                            update = { webView ->
                                if (webView.tag != pageHtml) {
                                    webView.tag = pageHtml
                                    webView.setBackgroundColor(bgArgb)
                                    webView.loadDataWithBaseURL(
                                        "https://github.com/",
                                        pageHtml,
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(webViewHeight)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(context.getString(R.string.repo_no_readme))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 2.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

private fun formatCount(n: Int): String {
    return when {
        n >= 1_000_000 -> String.format("%.1fM", n / 1_000_000f)
        n >= 1000 -> String.format("%.1fk", n / 1000f)
        else -> n.toString()
    }
}

private fun colorToArgb(color: androidx.compose.ui.graphics.Color): Int {
    val a = (color.alpha * 255).toInt()
    val r = (color.red * 255).toInt()
    val g = (color.green * 255).toInt()
    val b = (color.blue * 255).toInt()
    return android.graphics.Color.argb(a, r, g, b)
}

private fun buildReadmePage(bodyHtml: String, backgroundColor: String, textColor: String, linkColor: String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
            <style>
                * { box-sizing: border-box; }
                body {
                    margin: 0;
                    padding: 16px;
                    background-color: $backgroundColor;
                    color: $textColor;
                    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
                    font-size: 16px;
                    line-height: 1.6;
                    word-wrap: break-word;
                }
                h1, h2, h3, h4, h5, h6 {
                    margin-top: 24px;
                    margin-bottom: 16px;
                    font-weight: 600;
                    line-height: 1.25;
                    color: $textColor;
                }
                h1 { font-size: 2em; border-bottom: 1px solid rgba(175,184,193,0.3); padding-bottom: 0.3em; }
                h2 { font-size: 1.5em; border-bottom: 1px solid rgba(175,184,193,0.3); padding-bottom: 0.3em; }
                h3 { font-size: 1.25em; }
                h4 { font-size: 1em; }
                h5 { font-size: 0.875em; }
                h6 { font-size: 0.85em; opacity: 0.7; }
                p { margin-top: 0; margin-bottom: 16px; }
                a { color: $linkColor; text-decoration: none; }
                a:hover { text-decoration: underline; }
                strong { font-weight: 600; }
                em { font-style: italic; }
                ul, ol { margin-top: 0; margin-bottom: 16px; padding-left: 2em; }
                li { margin-bottom: 4px; }
                li > ul, li > ol { margin-top: 4px; }
                code {
                    background-color: rgba(175,184,193,0.2);
                    border-radius: 6px;
                    font-size: 85%;
                    margin: 0;
                    padding: 0.2em 0.4em;
                    font-family: SFMono-Regular, Consolas, "Liberation Mono", Menlo, monospace;
                    color: $textColor;
                }
                pre {
                    background-color: rgba(175,184,193,0.15);
                    border-radius: 6px;
                    font-size: 85%;
                    line-height: 1.45;
                    overflow: auto;
                    padding: 16px;
                    margin-bottom: 16px;
                }
                pre code {
                    background-color: transparent;
                    border: 0;
                    display: inline;
                    line-height: inherit;
                    margin: 0;
                    overflow: visible;
                    padding: 0;
                    word-wrap: normal;
                }
                blockquote {
                    border-left: 0.25em solid rgba(175,184,193,0.4);
                    color: ${textColor}99;
                    padding: 0 1em;
                    margin: 0 0 16px 0;
                }
                blockquote > :first-child { margin-top: 0; }
                blockquote > :last-child { margin-bottom: 0; }
                table {
                    border-collapse: collapse;
                    border-spacing: 0;
                    display: block;
                    overflow: auto;
                    width: 100%;
                    margin-bottom: 16px;
                }
                td, th {
                    border: 1px solid rgba(175,184,193,0.3);
                    padding: 6px 13px;
                }
                th { font-weight: 600; }
                tr:nth-child(2n) { background-color: rgba(175,184,193,0.1); }
                img {
                    max-width: 100%;
                    box-sizing: border-box;
                    background-color: transparent;
                    height: auto;
                }
                hr {
                    border: 0;
                    border-bottom: 1px solid rgba(175,184,193,0.3);
                    margin: 24px 0;
                }
                input[type="checkbox"] { margin-right: 4px; vertical-align: middle; }
                .highlight { margin-bottom: 16px; }
                .highlight pre { margin-bottom: 0; word-break: normal; }
                details { margin-bottom: 16px; }
                summary { cursor: pointer; font-weight: 600; }
            </style>
        </head>
        <body>
            $bodyHtml
        </body>
        </html>
    """.trimIndent()
}
