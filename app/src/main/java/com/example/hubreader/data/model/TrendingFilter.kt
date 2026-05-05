package com.example.hubreader.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.hubreader.R

enum class TimeRange(val labelRes: Int) {
    TODAY(R.string.time_range_today),
    THIS_WEEK(R.string.time_range_week),
    THIS_MONTH(R.string.time_range_month);

    fun toQuerySuffix(): String {
        val calendar = Calendar.getInstance()
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return when (this) {
            TODAY -> {
                "created:${fmt.format(calendar.time)}"
            }
            THIS_WEEK -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                "created:>=${fmt.format(calendar.time)}"
            }
            THIS_MONTH -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                "created:>=${fmt.format(calendar.time)}"
            }
        }
    }
}

object TrendingLanguages {
    val items = listOf(
        "All", "Python", "JavaScript", "Kotlin", "Go",
        "Rust", "TypeScript", "Java", "Swift", "C++"
    )
}
