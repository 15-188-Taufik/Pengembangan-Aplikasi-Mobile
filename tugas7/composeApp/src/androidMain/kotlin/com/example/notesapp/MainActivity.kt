package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.notesapp.data.local.DatabaseDriverFactory
import com.russhwolf.settings.SharedPreferencesSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Settings dibuat di Android menggunakan SharedPreferences
        val prefs = getSharedPreferences("notes_app_prefs", MODE_PRIVATE)
        val settings = SharedPreferencesSettings(prefs)

        setContent {
            MaterialTheme {
                App(
                    driverFactory = DatabaseDriverFactory(applicationContext),
                    settings = settings
                )
            }
        }
    }
}
