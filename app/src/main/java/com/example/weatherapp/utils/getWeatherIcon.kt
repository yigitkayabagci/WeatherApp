package com.example.weatherapp.utils

import com.example.weatherapp.R

fun getWeatherIcon(weatherId: Int): Int {
    return when (weatherId) {
        in 200..232 -> R.drawable.ic_thunderstorm
        in 300..321 -> R.drawable.ic_drizzle
        in 500..531 -> R.drawable.ic_rain
        in 600..622 -> R.drawable.ic_snow
        in 701..781 -> R.drawable.ic_atmos
        800 -> R.drawable.ic_clear
        in 801..804 -> R.drawable.ic_clouds
        else -> R.drawable.ic_unknown
    }
}