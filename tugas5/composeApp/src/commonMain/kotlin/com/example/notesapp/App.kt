package com.example.notesapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesapp.data.local.DatabaseDriverFactory
import com.example.notesapp.data.local.DatabaseProvider
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.data.settings.SettingsManager
import com.example.notesapp.ui.notes.AddEditNoteScreen
import com.example.notesapp.ui.notes.AddEditNoteViewModel
import com.example.notesapp.ui.notes.NotesScreen
import com.example.notesapp.ui.notes.NotesViewModel
import com.example.notesapp.ui.settings.SettingsScreen
import com.example.notesapp.ui.settings.SettingsViewModel
import com.russhwolf.settings.Settings

@Composable
fun App(
    driverFactory: DatabaseDriverFactory,
    settings: Settings
) {
    val database = remember { DatabaseProvider.getDatabase(driverFactory) }
    val repository = remember { NoteRepository(database) }
    val settingsManager = remember { SettingsManager(settings) }

    val settingsViewModel: SettingsViewModel = viewModel { SettingsViewModel(settingsManager) }
    val isDarkModeSetting by settingsViewModel.isDarkMode.collectAsState()
    
    val useDarkTheme = isDarkModeSetting || (isSystemInDarkTheme() && !isDarkModeSetting)

    MaterialTheme(
        colorScheme = if (useDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        val navController = rememberNavController()
        
        NavHost(navController = navController, startDestination = "notes") {
            composable("notes") {
                val viewModel: NotesViewModel = viewModel { NotesViewModel(repository) }
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
                val viewModel: AddEditNoteViewModel = viewModel { AddEditNoteViewModel(repository) }
                AddEditNoteScreen(
                    viewModel = viewModel,
                    noteId = noteId,
                    onBack = { navController.popBackStack() }
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
