package com.example.notesapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.settings.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.notesapp.data.platform.DeviceInfo

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    val deviceInfo: DeviceInfo
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = settingsManager.isDarkMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setDarkMode(enabled)
        }
    }
}
