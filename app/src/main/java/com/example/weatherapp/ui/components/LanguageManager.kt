package com.example.weatherapp.ui.components

import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.viewmodel.LanguageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class LanguageManager(
    private val context: Context,
    private val languageViewModel: LanguageViewModel
) {
    companion object {
        private const val PREFS_NAME = "LanguagePrefs"
        private const val SELECTED_LANGUAGE = "selected_language"
        private const val LANGUAGE_CHANGE_DELAY = 2500L
    }

    fun setLocale(
        languageCode: String,
        onLanguageChangeComplete: (() -> Unit)? = null
    ) {
        languageViewModel.viewModelScope.launch(Dispatchers.Main) {
            try {
                languageViewModel.setLanguageChanging(true)
                delay(LANGUAGE_CHANGE_DELAY)
                saveLanguagePreference(languageCode)
                updateAppLocale(context, languageCode)

                onLanguageChangeComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                languageViewModel.setLanguageChanging(false)
            }
        }
    }

    private fun updateAppLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val configuration = resources.configuration

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            configuration.setLocales(LocaleList(locale))
        } else {
            configuration.locale = locale
        }

        context.createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun saveLanguagePreference(languageCode: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
    }

    fun getStoredLanguage(): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, Locale.getDefault().language) ?: "en"
    }

    fun getCurrentLanguage(): String? {
        return getStoredLanguage()
    }

}