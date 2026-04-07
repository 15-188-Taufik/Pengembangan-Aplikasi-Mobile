package com.taufik.notesapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.taufik.notesapp.screens.AddNoteScreen
import com.taufik.notesapp.screens.EditNoteScreen
import com.taufik.notesapp.screens.FavoritesScreen
import com.taufik.notesapp.screens.NoteDetailScreen
import com.taufik.notesapp.screens.NoteListScreen
import com.taufik.notesapp.screens.ProfileTabScreen
import com.taufik.notesapp.viewmodel.NotesViewModel

@Composable
fun NotesNavGraph(
    navController: NavHostController,
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.NoteList.route,
        modifier = modifier
    ) {
        composable(Screen.NoteList.route) {
            NoteListScreen(
                viewModel = viewModel,
                onNoteClick = { navController.navigate(Screen.NoteDetail.createRoute(it)) },
                onAddClick  = { navController.navigate(Screen.AddNote.route) }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel,
                onNoteClick = { navController.navigate(Screen.NoteDetail.createRoute(it)) }
            )
        }

        composable(Screen.Profile.route) {
            ProfileTabScreen()
        }

        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { back ->
            val noteId = back.arguments?.getInt("noteId") ?: return@composable
            NoteDetailScreen(
                noteId    = noteId,
                viewModel = viewModel,
                onEditClick = { navController.navigate(Screen.EditNote.createRoute(it)) },
                onBack      = { navController.popBackStack() }
            )
        }

        composable(Screen.AddNote.route) {
            AddNoteScreen(
                onSave = { title, content ->
                    viewModel.addNote(title, content)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditNote.route,
            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
        ) { back ->
            val noteId = back.arguments?.getInt("noteId") ?: return@composable
            EditNoteScreen(
                noteId    = noteId,
                viewModel = viewModel,
                onSave = { id, title, content ->
                    viewModel.editNote(id, title, content)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}