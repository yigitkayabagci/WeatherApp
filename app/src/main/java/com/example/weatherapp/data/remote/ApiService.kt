// ApiService.kt
package com.example.weatherapp.data.remote

import com.example.weatherapp.data.remote.model.LocationResponse
import com.example.weatherapp.data.remote.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale

interface ApiService {
    @GET("data/3.0/onecall")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = Locale.getDefault().language,
        @Query("exclude") exclude: String = "minutely,alerts"
    ): Response<WeatherResponse>

    @GET("geo/1.0/direct")
    suspend fun getCoordinates(
        @Query("q") cityName: String,
        @Query("limit") limit: Int,
        @Query("appid") apiKey: String
    ): Response<List<LocationResponse>>

    @GET("geo/1.0/reverse")
    suspend fun getReverseGeocoding(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("limit") limit: Int = 1,
        @Query("appid") apiKey: String
    ): Response<List<LocationResponse>>
}