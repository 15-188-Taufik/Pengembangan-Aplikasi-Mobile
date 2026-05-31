package com.example.notesapp.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditNoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private var currentNoteId: Long? = null

    fun loadNote(id: Long) {
        if (currentNoteId == id) return
        viewModelScope.launch {
            repository.getNoteById(id)?.let { note ->
                currentNoteId = note.id
                _title.value = note.title
                _content.value = note.content
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
    }

    fun saveNote(onSaved: () -> Unit) {
        viewModelScope.launch {
            val titleVal = _title.value
            val contentVal = _content.value
            if (titleVal.isNotBlank() || contentVal.isNotBlank()) {
                val id = currentNoteId
                if (id == null) {
                    repository.insertNote(titleVal, contentVal)
                } else {
                    repository.updateNote(id, titleVal, contentVal)
                }
            }
            onSaved()
        }
    }
}
