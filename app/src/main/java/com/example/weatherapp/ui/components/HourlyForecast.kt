package com.example.weatherapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.domain.model.HourlyWeather
import com.example.weatherapp.utils.getWeatherIcon
import java.text.SimpleDateFormat
import java.util.*

fun convertTimestampToHour(timestamp: Long): String {
    val sdf = SimpleDateFormat("ha", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

@Composable
fun HourlyForecast(forecasts: List<HourlyWeather>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(forecasts) { forecast ->
            val hour = convertTimestampToHour(forecast.timestamp)
            val temperature = forecast.temperature.toInt().toString()
            val iconRes = getWeatherIcon(forecast.weatherCondition[0].id)
            HourlyForecastItem(
                hour = hour,
                icon = painterResource(id = iconRes),
                temperature = temperature
            )
        }
    }
}

@Composable
fun HourlyForecastItem(
    hour: String,
    icon: Painter,
    temperature: String
) {
    Card(
        shape = RoundedCornerShape(35.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        modifier = Modifier
            .width(70.dp)
            .height(150.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = hour,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(3.dp))

            Image(
                painter = icon,
                contentDescription = hour,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$temperature°",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            )
        }
    }
}
