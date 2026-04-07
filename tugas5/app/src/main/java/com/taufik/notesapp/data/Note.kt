package com.taufik.notesapp.data

data class Note(
    val id: Int,
    val title: String,
    val content: String,
    val isFavorite: Boolean = false,
    val createdAt: String = ""
)

data class NotesUiState(
    val notes: List<Note> = listOf(
        Note(1, "Belajar Compose", "Hari ini belajar tentang Composable functions, Column, Row, dan Box.", false, "10 Mar 2026"),
        Note(2, "Tugas PAM", "Tugas minggu 5: Notes App dengan Navigation Compose.", true, "11 Mar 2026"),
        Note(3, "Ide Proyek", "Buat aplikasi manajemen tugas kuliah dengan fitur reminder.", true, "12 Mar 2026"),
        Note(4, "Catatan Kuliah", "State hoisting: state turun, event naik. ViewModel survive config change.", false, "13 Mar 2026"),
    )
)