package com.example.notesapp.di

import com.example.notesapp.data.local.DatabaseProvider
import com.example.notesapp.data.platform.DeviceInfo
import com.example.notesapp.data.platform.NetworkMonitor
import com.example.notesapp.data.remote.GeminiService
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.data.settings.SettingsManager
import com.example.notesapp.ui.notes.NotesViewModel
import com.example.notesapp.ui.notes.AddEditNoteViewModel
import com.example.notesapp.ui.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single { DatabaseProvider.getDatabase(get()) }
    singleOf(::NoteRepository)
    singleOf(::SettingsManager)
    singleOf(::DeviceInfo)
    singleOf(::NetworkMonitor)
    singleOf(::GeminiService)
    
    viewModelOf(::NotesViewModel)
    viewModelOf(::AddEditNoteViewModel)
    viewModelOf(::SettingsViewModel)
}

expect fun platformModule(): Module
