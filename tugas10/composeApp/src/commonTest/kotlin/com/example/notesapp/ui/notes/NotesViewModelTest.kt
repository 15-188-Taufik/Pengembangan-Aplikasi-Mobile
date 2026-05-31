package com.example.notesapp.ui.notes

import app.cash.turbine.test
import com.example.notesapp.data.platform.NetworkMonitor
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.db.Note
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    private val repository: NoteRepository = mockk(relaxed = true)
    private val networkMonitor: NetworkMonitor = mockk()
    
    private val isOnlineFlow = MutableStateFlow(true)
    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val searchFlow = MutableStateFlow<List<Note>>(emptyList())

    private lateinit var viewModel: NotesViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { networkMonitor.isOnline } returns isOnlineFlow
        every { repository.getAllNotes() } returns notesFlow
        every { repository.searchNotes(any()) } returns searchFlow
        
        viewModel = NotesViewModel(repository, networkMonitor)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isCorrect() = runTest {
        // Test case 1: Initial states are set up correctly
        assertTrue(viewModel.isOnline.value)
        assertEquals("", viewModel.searchQuery.value)
        assertTrue(viewModel.notes.value.isEmpty())
    }

    @Test
    fun onSearchQueryChange_updatesSearchQueryState() = runTest {
        // Test case 2: Changing search query updates the searchQuery state flow
        viewModel.onSearchQueryChange("Kotlin")
        assertEquals("Kotlin", viewModel.searchQuery.value)
    }

    @Test
    fun deleteNote_callsRepositoryDelete() = runTest {
        // Test case 3: deleteNote triggers NoteRepository.deleteNote
        val targetId = 42L
        
        viewModel.deleteNote(targetId)
        
        coVerify(exactly = 1) { repository.deleteNote(targetId) }
    }

    @Test
    fun notesStateFlow_emitsRepositoryNotes() = runTest {
        // Test case 4: notes state flow exposes notes from repository
        val mockNotes = listOf(
            Note(1L, "Note 1", "Content 1", 100L, 100L),
            Note(2L, "Note 2", "Content 2", 200L, 200L)
        )
        notesFlow.value = mockNotes
        
        viewModel.notes.test {
            assertEquals(mockNotes, awaitItem())
        }
    }

    @Test
    fun isOnlineFlow_emitsStatusChanges_usingTurbine() = runTest {
        // Test case 5 (Turbine): isOnline reactively emits true then false
        viewModel.isOnline.test {
            assertTrue(awaitItem())
            
            isOnlineFlow.value = false
            assertFalse(awaitItem())
            
            isOnlineFlow.value = true
            assertTrue(awaitItem())
        }
    }

    @Test
    fun notesFlow_emitsSearchResultsDynamically_usingTurbine() = runTest {
        // Test case 6 (Turbine): notes state flow reactively updates when query changes
        val allNotes = listOf(Note(1L, "All 1", "Content", 100L, 100L))
        val searchResults = listOf(Note(2L, "Search 1", "Match", 200L, 200L))
        
        notesFlow.value = allNotes
        searchFlow.value = searchResults
        
        viewModel.notes.test {
            // Initial emit with empty query (loads allNotes)
            assertEquals(allNotes, awaitItem())
            
            // Switch search query to "Search"
            viewModel.onSearchQueryChange("Search")
            assertEquals(searchResults, awaitItem())
            
            // Clear search query (loads allNotes again)
            viewModel.onSearchQueryChange("")
            assertEquals(allNotes, awaitItem())
        }
    }
}
