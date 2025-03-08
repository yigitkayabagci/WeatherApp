package com.example.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageViewModel : ViewModel() {
    private val _isLanguageChanging = MutableStateFlow(false)
    val isLanguageChanging: StateFlow<Boolean> = _isLanguageChanging.asStateFlow()

    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguageChanging(isChanging: Boolean, newLanguage: String? = null) {
        viewModelScope.launch {
            _isLanguageChanging.value = isChanging
            newLanguage?.let {
                _currentLanguage.value = it
            }
        }
    }
}