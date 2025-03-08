package com.example.weatherapp.utils

import java.time.Instant
import java.time.ZoneId

fun adjustTimeForTimezone(timestamp: Long, timezone: String): Long {
    val instant = Instant.ofEpochSecond(timestamp)
    val zoneId = ZoneId.of(timezone)
    val offset = zoneId.rules.getOffset(instant)
    return timestamp + offset.totalSeconds
}