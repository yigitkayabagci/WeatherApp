    package com.example.weatherapp.utils

    import android.content.Context
    import androidx.datastore.preferences.core.edit
    import androidx.datastore.preferences.core.stringPreferencesKey
    import androidx.datastore.preferences.preferencesDataStore
    import com.example.weatherapp.domain.model.Weather
    import com.google.gson.Gson
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.first
    import kotlinx.coroutines.flow.map

    private val Context.dataStore by preferencesDataStore(name = "weather_data")

    object
    DataStoreManager {
        private val WEATHER_KEY = stringPreferencesKey("weather_key")
        private val LAST_SEARCHED_CITY = stringPreferencesKey("last_searched_city")
        private val gson = Gson()

        suspend fun saveWeather(context: Context, weather: Weather) {
            context.dataStore.edit { preferences ->
                preferences[WEATHER_KEY] = gson.toJson(weather)
            }
        }

        suspend fun saveLastSearchedCity(context: Context, city: String) {
            context.dataStore.edit { preferences ->
                preferences[LAST_SEARCHED_CITY] = city
            }
        }

        suspend fun clearLastSearchedCity(context: Context) {
            context.dataStore.edit { preferences ->
                preferences.remove(LAST_SEARCHED_CITY)
            }
        }

        suspend fun getLastSearchedCity(context: Context): String? {
            return context.dataStore.data.first()[LAST_SEARCHED_CITY]
        }

        fun getWeather(context: Context): Flow<Weather?> {
            return context.dataStore.data.map { preferences ->
                val json = preferences[WEATHER_KEY]
                json?.let {
                    try {
                        gson.fromJson(it, Weather::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
    }