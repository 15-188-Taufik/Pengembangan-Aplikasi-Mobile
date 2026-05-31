package com.example.notesapp.data.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.notesapp.db.NotesDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NoteRepositoryTest {
    private lateinit var driver: SqlDriver
    private lateinit var database: NotesDatabase
    private lateinit var repository: NoteRepository

    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        NotesDatabase.Schema.create(driver)
        database = NotesDatabase(driver)
        repository = NoteRepository(database)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun insertNote_addsNoteToDatabase() = runTest {
        repository.insertNote("Test Title", "Test Content")
        
        val notes = repository.getAllNotes().first()
        assertEquals(1, notes.size)
        assertEquals("Test Title", notes[0].title)
        assertEquals("Test Content", notes[0].content)
    }

    @Test
    fun getAllNotes_returnsNotesSortedByLatestUpdated() = runTest {
        repository.insertNote("Note 1", "Content 1")
        kotlinx.coroutines.delay(10)
        repository.insertNote("Note 2", "Content 2")
        
        val notes = repository.getAllNotes().first()
        assertEquals(2, notes.size)
        assertEquals("Note 2", notes[0].title)
        assertEquals("Note 1", notes[1].title)
    }

    @Test
    fun getNoteById_returnsCorrectNote() = runTest {
        repository.insertNote("Target", "Content")
        val allNotes = repository.getAllNotes().first()
        val targetId = allNotes[0].id

        val foundNote = repository.getNoteById(targetId)
        assertNotNull(foundNote)
        assertEquals("Target", foundNote.title)
        
        val nonExistentNote = repository.getNoteById(9999L)
        assertNull(nonExistentNote)
    }

    @Test
    fun updateNote_modifiesNoteAndUpdatesTimestamp() = runTest {
        repository.insertNote("Original", "Original Content")
        val originalNote = repository.getAllNotes().first()[0]
        
        kotlinx.coroutines.delay(10)
        
        repository.updateNote(originalNote.id, "Updated Title", "Updated Content")
        
        val updatedNote = repository.getNoteById(originalNote.id)
        assertNotNull(updatedNote)
        assertEquals("Updated Title", updatedNote.title)
        assertEquals("Updated Content", updatedNote.content)
        assertTrue(updatedNote.updated_at >= originalNote.updated_at)
    }

    @Test
    fun deleteNote_removesNoteSuccessfully() = runTest {
        repository.insertNote("To Be Deleted", "Content")
        val note = repository.getAllNotes().first()[0]
        
        repository.deleteNote(note.id)
        
        val notes = repository.getAllNotes().first()
        assertTrue(notes.isEmpty())
    }

    @Test
    fun searchNotes_returnsMatchingNotes() = runTest {
        repository.insertNote("Apple Pie", "Baked with love")
        repository.insertNote("Banana Bread", "Apple slices on top")
        repository.insertNote("Cherry Cake", "Delicious dessert")
        
        val searchApple = repository.searchNotes("Apple").first()
        assertEquals(2, searchApple.size)
        assertTrue(searchApple.any { it.title == "Apple Pie" })
        assertTrue(searchApple.any { it.title == "Banana Bread" })

        val searchCherry = repository.searchNotes("Cake").first()
        assertEquals(1, searchCherry.size)
        assertEquals("Cherry Cake", searchCherry[0].title)

        val searchNonExistent = repository.searchNotes("Grape").first()
        assertTrue(searchNonExistent.isEmpty())
    }
}
