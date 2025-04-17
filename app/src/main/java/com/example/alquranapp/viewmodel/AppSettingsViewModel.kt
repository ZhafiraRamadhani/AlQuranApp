package com.example.alquranapp.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AppSettingsViewModel : ViewModel() {
    val isDarkTheme = mutableStateOf(false)
}