package com.example.weatherapp.utils

fun formatCityName(cityName: String): String {
    return cityName
        .trim()
        .split(Regex("\\s+"))
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}