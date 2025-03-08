package com.example.weatherapp.utils

import com.example.weatherapp.domain.model.HourlyWeather
import com.example.weatherapp.domain.model.Weather

fun filterHourlyForDay(weather: Weather, dayIndex: Int): List<HourlyWeather> {
    val dailyList = weather.daily
    if (dayIndex >= 0 && dayIndex < dailyList.size) {
        val startOfDay = dailyList[dayIndex].timestamp
        val endOfDay = startOfDay + 24 * 3600
        return weather.hourly.filter { it.timestamp in startOfDay..endOfDay }
    }
    return emptyList()
}