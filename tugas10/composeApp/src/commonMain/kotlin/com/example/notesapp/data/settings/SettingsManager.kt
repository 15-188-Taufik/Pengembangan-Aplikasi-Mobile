package com.example.notesapp.data.settings

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getStringFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsManager(private val settings: Settings) {

    private val observableSettings = settings as? ObservableSettings

    companion object {
        private const val KEY_THEME      = "app_theme"
        private const val KEY_SORT_ORDER = "sort_order"
        private const val KEY_FONT_SIZE  = "font_size"
        private const val KEY_NOTIF      = "notifications_enabled"
        private const val KEY_DARK_MODE  = "is_dark_mode"
        private const val KEY_GEMINI_API_KEY = "gemini_api_key"
    }

    fun isDarkMode(): Flow<Boolean> {
        return observableSettings?.getBooleanFlow(KEY_DARK_MODE, false) ?: flowOf(false)
    }

    fun setDarkMode(enabled: Boolean) {
        settings.putBoolean(KEY_DARK_MODE, enabled)
    }

    fun getGeminiApiKey(): Flow<String> {
        return observableSettings?.getStringFlow(KEY_GEMINI_API_KEY, "") ?: flowOf("")
    }

    fun setGeminiApiKey(key: String) {
        settings.putString(KEY_GEMINI_API_KEY, key)
    }

    var theme: String
        get() = settings.getString(KEY_THEME, "system")
        set(value) { settings.putString(KEY_THEME, value) }

    var sortOrder: String
        get() = settings.getString(KEY_SORT_ORDER, "updated")
        set(value) { settings.putString(KEY_SORT_ORDER, value) }

    var fontSize: Int
        get() = settings.getInt(KEY_FONT_SIZE, 16)
        set(value) { settings.putInt(KEY_FONT_SIZE, value) }

    var notificationsEnabled: Boolean
        get() = settings.getBoolean(KEY_NOTIF, true)
        set(value) { settings.putBoolean(KEY_NOTIF, value) }
}
