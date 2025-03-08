package com.example.weatherapp.ui.screens

import WeatherStats
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.ui.components.GradientBackground
import com.example.weatherapp.ui.components.HourlyForecast
import com.example.weatherapp.ui.components.TopBar
import com.example.weatherapp.ui.components.WeatherOptions
import com.example.weatherapp.utils.getCountryNameFromCode
import com.example.weatherapp.utils.getWeatherIcon
import com.example.weatherapp.utils.time_zone_parse
import com.example.weatherapp.viewmodel.WeatherState
import com.example.weatherapp.viewmodel.WeatherViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weatherapp.viewmodel.LanguageViewModel
import com.example.weatherapp.ui.components.ErrorView
import com.example.weatherapp.ui.components.LanguageManager
import com.example.weatherapp.ui.components.LoadingView
import com.example.weatherapp.ui.components.WeatherOption
import com.example.weatherapp.ui.permissions.LocationPermissionHandler
import com.example.weatherapp.utils.filterHourlyForDay
import com.example.weatherapp.utils.formatTimeZoneAsDate
import com.example.weatherapp.utils.time_zone_parse2
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    languageViewModel: LanguageViewModel,
    onNext7DaysClicked: () -> Unit,
    langaugeManager: LanguageManager
) {
    val weatherState = viewModel.weatherState.collectAsState()
    val selectedOption = viewModel.selectedOption.collectAsState()
    val isNavigating = viewModel.isNavigating.collectAsState()

    var isFirstLaunch by remember { mutableStateOf(true) }

    val isLanguageChanging by languageViewModel.isLanguageChanging.collectAsState()


    LocationPermissionHandler(
        isFirstLaunch = isFirstLaunch,
        onPermissionGranted = {
            if (isFirstLaunch) {
                viewModel.onFirstLaunch()
            }
            isFirstLaunch = false
        },
        onPermissionDenied = {
            viewModel.onPermissionResult(false)
            isFirstLaunch = false
        }
    )

    val nameCountryTextStyle = MaterialTheme.typography.headlineLarge.copy(
        fontWeight = FontWeight.W400,
        fontSize = 40.sp
    )

    if (isLanguageChanging) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.changing_language),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
    else {
        Box(modifier = Modifier.fillMaxSize()) {
            GradientBackground()

            when (val state = weatherState.value) {
                is WeatherState.Loading -> LoadingView()
                is WeatherState.Error -> ErrorView(
                    message = state.message,
                    showRetryButton = state.showRetryButton,
                    onRetry = {
                        viewModel.onRetryClicked()
                    }
                )

                is WeatherState.Success -> {
                    if (isNavigating.value) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        val weather = state.weather

                        if (weather.name.isEmpty()) {
                            weather.name = time_zone_parse(weather.timezone)
                            weather.country = time_zone_parse2(weather.timezone)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            TopBar(
                                onSearch = { cityName -> viewModel.searchLocation(cityName) },
                                onLocationClick = { viewModel.loadLocationBasedWeather() },
                                onRefreshClick = { viewModel.onPermissionResult(true) },
                                languageManager = langaugeManager,
                                onLanguageChange = {
                                    viewModel.onPermissionResult(true)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "${weather.name},",
                                style = nameCountryTextStyle,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                            Text(
                                text = getCountryNameFromCode(weather.country),
                                style = nameCountryTextStyle,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 20.dp)
                            )

                            val formattedDate = formatTimeZoneAsDate(weather.timezone)
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.padding(start = 20.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            when (selectedOption.value) {
                                WeatherOption.TODAY -> {
                                    TodayContent(weather)
                                }

                                WeatherOption.TOMORROW -> {
                                    TomorrowContent(weather)
                                }

                                WeatherOption.NEXT7 -> {
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            WeatherOptions(
                                selected = selectedOption.value,
                                onOptionSelected = { option ->
                                    if (option == WeatherOption.NEXT7) {
                                        onNext7DaysClicked()
                                    } else {
                                        viewModel.setOption(option)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val hourlyListToShow = remember(selectedOption.value) {
                                when (selectedOption.value) {
                                    WeatherOption.TODAY -> filterHourlyForDay(weather, 0)
                                    WeatherOption.TOMORROW -> filterHourlyForDay(weather, 1)
                                    WeatherOption.NEXT7 -> weather.hourly
                                }
                            }

                            if (!isNavigating.value) {
                                HourlyForecast(forecasts = hourlyListToShow)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun TodayContent(weather: Weather) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(
                id = getWeatherIcon(weather.current.weatherCondition[0].id)
            ),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "${weather.current.temperature.toInt()}°",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 60.sp
            )
            Text(
                text = weather.current.weatherCondition[0].description,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontSize = 30.sp
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    WeatherStats(
        rainfall = weather.current.rain?.oneHour?.toString() ?: "0",
        windSpeed = weather.current.windSpeed.toString(),
        humidity = weather.current.humidity.toString(),
        rainfallPainter = painterResource(id = R.drawable.ic_rainfall),
        windPainter = painterResource(id = R.drawable.ic_wind),
        humidityPainter = painterResource(id = R.drawable.ic_humidity)
    )
}

@Composable
fun TomorrowContent(weather: Weather) {
    val tomorrow = weather.daily.getOrNull(1)

    if (tomorrow != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val iconId = tomorrow.weatherCondition.firstOrNull()?.id ?: 800
            Image(
                painter = painterResource(id = getWeatherIcon(iconId)),
                contentDescription = "Tomorrow Icon",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${tomorrow.temperature.dayTemp.toInt()}°",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 60.sp
                )
                val description = tomorrow.weatherCondition.firstOrNull()?.description ?: ""
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontSize = 30.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        WeatherStats(
            rainfall = tomorrow.rain?.toString() ?: "0",
            windSpeed = tomorrow.windSpeed.toString(),
            humidity = tomorrow.humidity.toString(),
            rainfallPainter = painterResource(id = R.drawable.ic_rainfall),
            windPainter = painterResource(id = R.drawable.ic_wind),
            humidityPainter = painterResource(id = R.drawable.ic_humidity)
        )
    } else {
        Text(
            text = stringResource(R.string.no_data_tomorrow),
            color = Color.Red
        )
    }
}