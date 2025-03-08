package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherapp.viewmodel.LanguageViewModel
import com.example.weatherapp.ui.components.LanguageManager
import com.example.weatherapp.ui.components.WeatherOption
import com.example.weatherapp.ui.screens.HomeScreen
import com.example.weatherapp.ui.screens.Next7DaysScreen
import com.example.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Extension function to navigate with a controlled delay and navigation state management
 *
 * @param route Destination route to navigate to
 * @param viewModel WeatherViewModel to manage navigation state
 * @param delay Duration of navigation delay (default 300ms)
 * @param scope CoroutineScope for launching navigation coroutine
 */
private fun NavHostController.navigateWithDelay(
    route: String,
    viewModel: WeatherViewModel,
    delay: Long = 300L,
    scope: CoroutineScope
) {
    scope.launch(Dispatchers.Main) {
        // Set navigation state to prevent interactions
        viewModel.setIsNavigating(true)

        // Small initial delay to ensure UI is ready
        delay(10)

        // Main navigation delay
        delay(delay)

        // Navigate to the new route
        navigate(route) {
            // Ensure we don't duplicate the home route in the back stack
            popUpTo("home") { inclusive = false }
        }

        // Additional delay to complete transition
        delay(500)

        // Reset navigation state
        viewModel.setIsNavigating(false)
    }
}

/**
 * Main navigation host for the Weather App
 *
 * @param navController Navigation controller for managing app navigation
 * @param viewModel WeatherViewModel for managing weather-related state
 * @param languageViewModel ViewModel for managing language change state
 * @param languageManager Manager for handling language changes
 * @param startDestination Initial route for the navigation host
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: WeatherViewModel,
    languageViewModel: LanguageViewModel, // Added language ViewModel
    languageManager: LanguageManager,
    startDestination: String = "home"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Home Screen Route
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                languageViewModel = languageViewModel, // Pass language ViewModel
                onNext7DaysClicked = {
                    // Navigate to next 7 days screen with controlled delay
                    navController.navigateWithDelay(
                        route = "next7",
                        viewModel = viewModel,
                        scope = viewModel.viewModelScope
                    )
                },
                languageManager
            )
        }

        // Next 7 Days Screen Route
        composable("next7") {
            Next7DaysScreen(
                viewModel = viewModel,
                OnBackPressed = {
                    // Reset to today's option and navigate back to home
                    viewModel.setOption(WeatherOption.TODAY)
                    navController.navigate("home") {
                        // Clear the back stack up to home
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}