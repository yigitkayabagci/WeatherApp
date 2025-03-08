package com.example.weatherapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R
import com.example.weatherapp.domain.model.DailyWeather
import com.example.weatherapp.ui.screens.convertTimestampToDayName
import com.example.weatherapp.utils.getWeatherIcon

@Composable
fun DailyList(dailyList: List<DailyWeather>) {
    val expandedItems = remember { mutableStateMapOf<Int, Boolean>() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        itemsIndexed(dailyList) { index, dailyWeather ->
            val expanded = expandedItems[index] ?: false
            DayItem(
                index = index,
                daily = dailyWeather,
                expanded = expanded,
                onExpandToggle = {
                    expandedItems[index] = !expanded
                }
            )
        }
    }
}

@Composable
fun DayItem(
    index: Int,
    daily: DailyWeather,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    val creamColor = Color(0xFFFFF8DC).copy(alpha = 0.9f)

    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = creamColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onExpandToggle() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dayName = convertTimestampToDayName(daily.timestamp)
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp
                    ),
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val tempDay = daily.temperature.dayTemp.toInt()
                    Text(
                        text = "$tempDay°",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 26.sp
                        ),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    val weatherId = daily.weatherCondition.firstOrNull()?.id ?: 800
                    val iconRes = getWeatherIcon(weatherId)
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Weather Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(32.dp))
                ExtraDailyInfo(daily)
            }
        }
    }
}


@Composable
fun ExtraDailyInfo(daily: DailyWeather) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painter = painterResource(id = R.drawable.ic_rainfall), contentDescription = "rainfall",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp))
            Text(
                text = stringResource(R.string.rain),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${daily.rain ?: 0} mm",
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painter = painterResource(id = R.drawable.ic_wind), contentDescription = "wind",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp))
            Text(
                text = stringResource(R.string.wind),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${daily.windSpeed} m/s",
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(painter = painterResource(id = R.drawable.ic_humidity), contentDescription = "humidity" ,
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp))
            Text(
                text = stringResource(R.string.humidity),
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${daily.humidity}%",
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
