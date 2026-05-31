package com.example.notesapp.di

import android.content.Context
import com.example.notesapp.data.local.DatabaseDriverFactory
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory(get()) }
    single<Settings> {
        val prefs = get<Context>().getSharedPreferences("notes_app_prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }
}
