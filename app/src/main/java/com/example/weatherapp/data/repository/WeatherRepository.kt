package com.example.weatherapp.data.repository

import com.example.weatherapp.data.remote.ApiService
import com.example.weatherapp.data.remote.model.LocationResponse
import com.example.weatherapp.data.remote.model.WeatherResponse
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.ui.components.LanguageManager
import java.util.Locale

class WeatherRepository(private val apiService: ApiService, private val languageManager: LanguageManager) {

    suspend fun fetchWeatherByCoordinates(
        lat: Double,
        lon: Double,
        language: String? = null
    ): Result<WeatherResponse> {
        return try {
            val selectedLanguage = language
                ?: languageManager.getCurrentLanguage()
                ?: Locale.getDefault().language

            val response = apiService.getWeatherByCoordinates(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.WEATHER_API_KEY,
                lang = selectedLanguage
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun searchLocation(cityName: String): LocationResponse? {
        val response = apiService.getCoordinates(
            cityName,
            limit = 1,
            apiKey = BuildConfig.WEATHER_API_KEY
        )
        return response.body()?.firstOrNull()
    }

    suspend fun getReverseGeocoding(lat: Double, lon: Double): Result<LocationResponse> {
        return try {
            val response = apiService.getReverseGeocoding(
                lat = lat,
                lon = lon,
                apiKey = BuildConfig.WEATHER_API_KEY
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.first())
            } else {
                Result.failure(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}