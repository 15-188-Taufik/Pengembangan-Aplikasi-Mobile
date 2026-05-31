package com.example.notesapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesapp.ui.notes.AddEditNoteScreen
import com.example.notesapp.ui.notes.AddEditNoteViewModel
import com.example.notesapp.ui.notes.NotesScreen
import com.example.notesapp.ui.notes.NotesViewModel
import com.example.notesapp.ui.settings.SettingsScreen
import com.example.notesapp.ui.settings.SettingsViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        val settingsViewModel: SettingsViewModel = koinViewModel()
        val isDarkModeSetting by settingsViewModel.isDarkMode.collectAsState()
        
        MaterialTheme(
            colorScheme = if (isDarkModeSetting) darkColorScheme() else lightColorScheme()
        ) {
            val navController = rememberNavController()
            
            NavHost(navController = navController, startDestination = "notes") {
                composable("notes") {
                    val viewModel: NotesViewModel = koinViewModel()
                    NotesScreen(
                        viewModel = viewModel,
                        onAddNote = { navController.navigate("add_edit_note/-1") },
                        onNoteClick = { note -> navController.navigate("add_edit_note/${note.id}") },
                        onSettingsClick = { navController.navigate("settings") }
                    )
                }
                
                composable(
                    route = "add_edit_note/{noteId}",
                    arguments = listOf(navArgument("noteId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getLong("noteId")
                    val viewModel: AddEditNoteViewModel = koinViewModel()
                    AddEditNoteScreen(
                        viewModel = viewModel,
                        noteId = noteId,
                        onBack = { navController.popBackStack() },
                        onSettingsClick = { navController.navigate("settings") }
                    )
                }
                
                composable("settings") {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
