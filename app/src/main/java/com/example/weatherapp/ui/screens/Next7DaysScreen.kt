package com.example.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.ui.components.Next7DaysTopBar
import com.example.weatherapp.ui.components.DailyList
import com.example.weatherapp.ui.components.ErrorView
import com.example.weatherapp.ui.components.GradientBackground
import com.example.weatherapp.ui.components.LoadingView
import com.example.weatherapp.viewmodel.WeatherState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun Next7DaysScreen(
    viewModel: WeatherViewModel,
    OnBackPressed: () -> Unit,
) {
    val weatherState = viewModel.weatherState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground()

        when(val state = weatherState.value) {
            is WeatherState.Loading -> LoadingView()
            is WeatherState.Error -> ErrorView(state.message)
            is WeatherState.Success -> {
                val weather = state.weather
                val dailyList = weather.daily

                Column {
                    Next7DaysTopBar(
                        OnBackPressed = OnBackPressed
                    )

                    DailyList(dailyList = dailyList)
                }
            }
        }
    }
}

fun convertTimestampToDayName(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        val date = java.util.Date(timestamp * 1000)
        sdf.format(date)
    } catch (e: Exception) {
        "N/A"
    }
}
