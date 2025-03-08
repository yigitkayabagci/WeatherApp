package com.example.weatherapp.data.remote.model

fun CurrentWeather.toDomainModel() = com.example.weatherapp.domain.model.CurrentWeather(
    timestamp = timestamp,
    temperature = temperature,
    humidity = humidity,
    windSpeed = windSpeed,
    weatherCondition = weatherCondition.map { it.toDomainModel() },
    rain = rain?.toDomainModel()
)

fun Rain.toDomainModel() = com.example.weatherapp.domain.model.Rain(oneHour)

fun HourlyWeather.toDomainModel() = com.example.weatherapp.domain.model.HourlyWeather(
    timestamp = timestamp,
    temperature = temperature,
    weatherCondition = weatherCondition.map { it.toDomainModel() }
)

fun DailyWeatherResponse.toDomainModel() = com.example.weatherapp.domain.model.DailyWeather(
    timestamp = timestamp,
    temperature = temperature.toDomainModel(),
    weatherCondition = weatherCondition.map { it.toDomainModel() },
    humidity = humidity,
    windSpeed = windSpeed
)

fun Temperature.toDomainModel() = com.example.weatherapp.domain.model.Temperature(
    dayTemp = dayTemp,
    minTemp = minTemp,
    maxTemp = maxTemp,
    nightTemp = nightTemp,
    eveningTemp = eveningTemp,
    morningTemp = morningTemp
)

fun WeatherCondition.toDomainModel() = com.example.weatherapp.domain.model.WeatherCondition(
    id = id,
    description = description
)