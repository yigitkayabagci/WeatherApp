package com.example.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
import com.example.weatherapp.data.remote.model.toDomainModel
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.ui.components.WeatherOption
import com.example.weatherapp.utils.Coordinates
import com.example.weatherapp.utils.DataStoreManager
import com.example.weatherapp.utils.LocationUtils
import com.example.weatherapp.utils.adjustTimeForTimezone
import com.example.weatherapp.utils.formatCityName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class WeatherViewModel(
    private val repository: WeatherRepository,
    private val context: Context
) : ViewModel() {

    private val locationUtils = LocationUtils(context)
    private var lastSearchedCity: String? = null

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState.asStateFlow()

    private val _selectedOption = MutableStateFlow(WeatherOption.TODAY)
    val selectedOption: StateFlow<WeatherOption> = _selectedOption.asStateFlow()

    private var isInitialized = false

    private val _isNavigating = MutableStateFlow(false)
    val isNavigating: StateFlow<Boolean> = _isNavigating.asStateFlow()


    init {
        viewModelScope.launch {
            if (!isInitialized) {
                lastSearchedCity = DataStoreManager.getLastSearchedCity(context)
                if (lastSearchedCity != null) {
                    searchLocation(lastSearchedCity!!)
                }
                isInitialized = true
            }
        }
    }

    private fun restoreLastWeather() {
        viewModelScope.launch {
            if (_weatherState.value is WeatherState.Loading) {
                DataStoreManager.getWeather(context).collect { savedWeather ->
                    savedWeather?.let {
                        _weatherState.value = WeatherState.Success(it)
                    }
                }
            }
        }
    }


    private fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        cityName: String,
        country: String? = null
    ) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            repository.fetchWeatherByCoordinates(lat, lon)
                .onSuccess { weatherResponse ->
                    val weather = Weather(
                        name = cityName,
                        country = country ?: "",
                        lat = weatherResponse.lat,
                        lon = weatherResponse.lon,
                        timezone = weatherResponse.timezone,
                        current = weatherResponse.current.toDomainModel().copy(
                            timestamp = adjustTimeForTimezone(
                                weatherResponse.current.timestamp,
                                weatherResponse.timezone
                            )
                        ),
                        hourly = weatherResponse.hourly.map {
                            it.toDomainModel().copy(
                                timestamp = adjustTimeForTimezone(it.timestamp, weatherResponse.timezone)
                            )
                        },
                        daily = weatherResponse.daily.map {
                            it.toDomainModel().copy(
                                timestamp = adjustTimeForTimezone(it.timestamp, weatherResponse.timezone)
                            )
                        }
                    )

                    _weatherState.value = WeatherState.Success(weather)
                    DataStoreManager.saveWeather(context, weather)
                }
                .onFailure { error ->
                    _weatherState.value = WeatherState.Error(error.message ?: context.getString(R.string.error_generic))
                }
        }
    }

    fun setOption(option: WeatherOption) {
        _selectedOption.value = option
    }

    fun searchLocation(cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            //format city name
            val formattedCityName = formatCityName(cityName)
            repository.searchLocation(formattedCityName)?.let { location ->
                lastSearchedCity = formattedCityName

                getWeatherForLocation(
                    lat = location.lat,
                    lon = location.lon,
                    cityName = formattedCityName,
                    country = location.country
                )

                DataStoreManager.saveLastSearchedCity(context, formattedCityName)
            } ?: run {
                _weatherState.value = WeatherState.Error(
                    message = context.getString(R.string.error_city_not_found),
                    showRetryButton = true
                )
            }
        }
    }
    fun loadLocationBasedWeather() {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                val location = locationUtils.getCurrentLocation()

                val currentLocation = selectedLocation ?: location

                repository.getReverseGeocoding(currentLocation.lat, currentLocation.lon)
                    .onSuccess { locationResponse ->
                        searchLocation(locationResponse.name)
                    }
                    .onFailure { error ->
                        _weatherState.value = WeatherState.Error(error.message ?: context.getString(R.string.error_city_not_found))
                    }
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: context.getString(R.string.error_location_not_found))
            }
        }
    }

    private var selectedLocation: Coordinates? = null


    fun onPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            _weatherState.value = WeatherState.Loading
            try {
                if (!isInitialized) {
                    lastSearchedCity = DataStoreManager.getLastSearchedCity(context)
                    if (lastSearchedCity != null) {
                        searchLocation(lastSearchedCity!!)
                    } else if (granted) {
                        loadLocationBasedWeather()
                    } else {
                        searchLocation("London")
                    }
                    isInitialized = true
                } else {
                    lastSearchedCity?.let {
                        searchLocation(it)
                    } ?: searchLocation("London")
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: context.getString(R.string.error_generic))
                restoreLastWeather()
            }
        }
    }

    fun onFirstLaunch() {
        viewModelScope.launch {
            if (!isInitialized) {
                _weatherState.value = WeatherState.Loading
                try {
                    loadLocationBasedWeather()
                } catch (e: Exception) {
                    lastSearchedCity?.let {
                        searchLocation(it)
                    } ?: searchLocation("London")
                }
                isInitialized = true
            }
        }
    }

    // 7screen delay navigation
    fun setIsNavigating(value: Boolean) {
        _isNavigating.value = value
    }

    // bulunamadiginda retry için
    fun onRetryClicked() {
        lastSearchedCity?.let { city ->
            searchLocation(city)
        }
    }
}
sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val weather: Weather) : WeatherState()
    data class Error(
        val message: String,
        val showRetryButton: Boolean = false
    ) : WeatherState()
}