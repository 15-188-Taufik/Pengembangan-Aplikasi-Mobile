package com.taufik.notesapp.viewmodel

import androidx.lifecycle.ViewModel
import com.taufik.notesapp.data.Note
import com.taufik.notesapp.data.NotesUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    fun getNoteById(id: Int): Note? =
        _uiState.value.notes.find { it.id == id }

    fun addNote(title: String, content: String) {
        if (title.isBlank()) return
        val newId = (_uiState.value.notes.maxOfOrNull { it.id } ?: 0) + 1
        _uiState.update {
            it.copy(
                notes = it.notes + Note(
                    id = newId,
                    title = title,
                    content = content,
                    createdAt = "Baru saja"
                )
            )
        }
    }

    fun editNote(id: Int, title: String, content: String) {
        _uiState.update { state ->
            state.copy(
                notes = state.notes.map { note ->
                    if (note.id == id) note.copy(title = title, content = content)
                    else note
                }
            )
        }
    }

    fun toggleFavorite(id: Int) {
        _uiState.update { state ->
            state.copy(
                notes = state.notes.map { note ->
                    if (note.id == id) note.copy(isFavorite = !note.isFavorite)
                    else note
                }
            )
        }
    }

    fun deleteNote(id: Int) {
        _uiState.update { state ->
            state.copy(notes = state.notes.filter { it.id != id })
        }
    }
}