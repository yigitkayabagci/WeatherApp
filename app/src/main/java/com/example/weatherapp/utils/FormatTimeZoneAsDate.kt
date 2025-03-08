package com.example.weatherapp.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

fun formatTimeZoneAsDate(timeZoneId: String): String {
    return try {
        val dateTime = LocalDateTime.now(ZoneId.of(timeZoneId))

        val currentLocale = Locale.getDefault()

        val dayOfWeek = dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, currentLocale)
        val month = dateTime.month.getDisplayName(TextStyle.FULL, currentLocale)
        val dayOfMonth = dateTime.dayOfMonth

        "$dayOfWeek, $month $dayOfMonth"
    } catch (e: Exception) {
        ""
    }
}