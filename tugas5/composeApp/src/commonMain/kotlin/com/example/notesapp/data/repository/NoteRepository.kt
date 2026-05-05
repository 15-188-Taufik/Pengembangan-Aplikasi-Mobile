package com.example.notesapp.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.notesapp.db.Note
import com.example.notesapp.db.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class NoteRepository(database: NotesDatabase) {
    private val queries = database.noteQueries

    fun getAllNotes(): Flow<List<Note>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.Default)
    }

    suspend fun getNoteById(id: Long): Note? {
        return queries.selectById(id).executeAsOneOrNull()
    }

    fun searchNotes(query: String): Flow<List<Note>> {
        return queries.search(query).asFlow().mapToList(Dispatchers.Default)
    }

    suspend fun insertNote(title: String, content: String) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.insert(title, content, now, now)
    }

    suspend fun updateNote(id: Long, title: String, content: String) {
        val now = Clock.System.now().toEpochMilliseconds()
        queries.update(title, content, now, id)
    }

    suspend fun deleteNote(id: Long) {
        queries.delete(id)
    }
}
