package com.example.weatherapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.viewmodel.LanguageViewModel
import com.example.weatherapp.data.remote.RetrofitClient
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.navigation.AppNavHost
import com.example.weatherapp.ui.components.LanguageManager
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.utils.NotificationScheduler
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val languageViewModel by lazy { LanguageViewModel() }

    private val languageManager by lazy {
        LanguageManager(
            context = this,
            languageViewModel = languageViewModel
        )
    }

    override fun attachBaseContext(newBase: Context) {
        val tempLanguageManager = LanguageManager(
            context = newBase,
            languageViewModel = LanguageViewModel()
        )
        val languageCode = tempLanguageManager.getStoredLanguage()

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        //notifications
        NotificationScheduler.createNotificationChannel(this)
        NotificationScheduler.scheduleTestNotification(this)

        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()

                val viewModel = WeatherViewModel(
                    WeatherRepository(RetrofitClient.weatherService, languageManager),
                    applicationContext
                )

                AppNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    languageViewModel = languageViewModel,
                    languageManager = languageManager
                )
            }
        }
    }
}
