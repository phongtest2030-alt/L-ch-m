package com.example.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppState {
    var isDarkTheme by mutableStateOf(false)
    var startWeekOnMonday by mutableStateOf(true)
    var notificationsEnabled by mutableStateOf(true)
}
