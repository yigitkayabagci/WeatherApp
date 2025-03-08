package com.example.weatherapp.utils

fun time_zone_parse(timezone: String): String {
    val timeZone = timezone.split("/")[1]
    return timeZone.replace("_", " ")
}

fun time_zone_parse2(timezone: String): String {
    val timeZone = timezone.split("/")[0]
    return timeZone.replace("_", " ")
}