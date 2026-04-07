package com.taufik.notesapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object NoteList  : Screen("note_list")
    object Favorites : Screen("favorites")
    object Profile   : Screen("profile")

    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }
    object AddNote : Screen("add_note")
    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: Int) = "edit_note/$noteId"
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

val bottomNavItems = listOf(
    BottomNavItem("Notes",     Icons.Default.Note,     Screen.NoteList),
    BottomNavItem("Favorites", Icons.Default.Favorite, Screen.Favorites),
    BottomNavItem("Profile",   Icons.Default.Person,   Screen.Profile),
)