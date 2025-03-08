package com.example.weatherapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R

enum class WeatherOption { TODAY, TOMORROW, NEXT7 }

@Composable
fun WeatherOptionItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val textStyle = if (isSelected) {
        MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold)
    } else {
        MaterialTheme.typography.bodyMedium
    }

    Text(
        text = text,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        style = textStyle,
        color = if (isSelected) Color.Black else Color.Black.copy(alpha = 0.5f)
    )
}

@Composable
fun WeatherOptions(
    selected: WeatherOption,
    onOptionSelected: (WeatherOption) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            WeatherOptionItem(
                text = stringResource(R.string.today),
                isSelected = selected == WeatherOption.TODAY,
                onClick = { onOptionSelected(WeatherOption.TODAY) }
            )
            WeatherOptionItem(
                text = stringResource(R.string.tomorrow),
                isSelected = selected == WeatherOption.TOMORROW,
                onClick = { onOptionSelected(WeatherOption.TOMORROW) }
            )
        }

        WeatherOptionItem(
            text = stringResource(R.string.next_seven_days),
            isSelected = selected == WeatherOption.NEXT7,
            onClick = { onOptionSelected(WeatherOption.NEXT7) }
        )
    }
}
