package com.example.weatherapp.domain.model

data class Weather(
    var name: String,
    var country: String,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>
)

data class CurrentWeather(
    val timestamp: Long,
    val temperature: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCondition: List<WeatherCondition>,
    val rain: Rain? = null
)

data class Rain(
    val oneHour: Double? = null
)

data class HourlyWeather(
    val timestamp: Long,
    val temperature: Double,
    val weatherCondition: List<WeatherCondition>
)

data class DailyWeather(
    val timestamp: Long,
    val temperature: Temperature,
    val weatherCondition: List<WeatherCondition>,
    val rain: Rain? = null,
    val humidity: Double,
    val windSpeed: Double
)

data class Temperature(
    val dayTemp: Double,
    val minTemp: Double,
    val maxTemp: Double,
    val nightTemp: Double,
    val eveningTemp: Double,
    val morningTemp: Double
)

data class WeatherCondition(
    val id: Int,
    val description: String
)