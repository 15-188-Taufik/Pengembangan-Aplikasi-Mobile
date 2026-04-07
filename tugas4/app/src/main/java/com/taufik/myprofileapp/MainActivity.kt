package com.taufik.myprofileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taufik.myprofileapp.ui.ProfileScreen
import com.taufik.myprofileapp.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Buat satu ViewModel yang dibagi ke seluruh app
            val profileViewModel: ProfileViewModel = viewModel()
            val uiState by profileViewModel.uiState.collectAsState()

            // Theme berubah dinamis sesuai isDarkMode di ViewModel
            MaterialTheme(
                colorScheme = if (uiState.isDarkMode) {
                    darkColorScheme(
                        primary = Color(0xFF1A73E8),
                        background = Color(0xFF0D1B2A),
                        surface = Color(0xFF1A2A3A)
                    )
                } else {
                    lightColorScheme(
                        primary = Color(0xFF1A73E8),
                        background = Color(0xFFF5F5F5),
                        surface = Color(0xFFFFFFFF)
                    )
                }
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProfileScreen(viewModel = profileViewModel)
                }
            }
        }
    }
}
