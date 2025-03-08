package com.example.weatherapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("current") val current: CurrentWeather,
    @SerializedName("hourly") val hourly: List<HourlyWeather>,
    @SerializedName("daily") val daily: List<DailyWeatherResponse>
)

data class CurrentWeather(
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("temp") val temperature: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("wind_speed") val windSpeed: Double,
    @SerializedName("weather") val weatherCondition: List<WeatherCondition>,
    @SerializedName("rain") val rain: Rain? = null
)

data class Rain(
    @SerializedName("1h") val oneHour: Double? = null
)

data class HourlyWeather(
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("temp") val temperature: Double,
    @SerializedName("weather") val weatherCondition: List<WeatherCondition>
)

data class DailyWeatherResponse(
    @SerializedName("dt") val timestamp: Long,
    @SerializedName("temp") val temperature: Temperature,
    @SerializedName("weather") val weatherCondition: List<WeatherCondition>,
    @SerializedName("humidity") val humidity: Double,
    @SerializedName("wind_speed") val windSpeed: Double
)

data class Temperature(
    @SerializedName("day") val dayTemp: Double,
    @SerializedName("min") val minTemp: Double,
    @SerializedName("max") val maxTemp: Double,
    @SerializedName("night") val nightTemp: Double,
    @SerializedName("eve") val eveningTemp: Double,
    @SerializedName("morn") val morningTemp: Double
)

data class WeatherCondition(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)