package com.example.notesapp

import android.app.Application
import com.example.notesapp.di.appModule
import com.example.notesapp.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NotesApplication : Application() {
    companion object {
        lateinit var instance: NotesApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidContext(this@NotesApplication)
            modules(appModule, platformModule())
        }
    }
}
