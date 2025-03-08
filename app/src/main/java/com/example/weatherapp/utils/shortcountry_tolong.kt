package com.example.weatherapp.utils

import android.content.Context
import java.util.Locale

fun getCountryNameFromCode(
    countryCode: String,
    context: Context? = null
): String {
    return try {
        if (context == null) {
            val locale = Locale("", countryCode)
            locale.getDisplayCountry(Locale.getDefault())
        } else {
            val sharedPrefs = context.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
            val savedLanguage = sharedPrefs.getString("selected_language", Locale.getDefault().language) ?: "en"

            val locale = Locale("", countryCode)
            locale.getDisplayCountry(Locale(savedLanguage))
        }
    } catch (e: Exception) {
        countryCode
    }
}