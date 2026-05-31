package com.example.notesapp.ui.notes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.notesapp.db.Note
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.BeforeTest

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NotesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: NotesViewModel = mockk(relaxed = true)
    
    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())
    private val searchQueryFlow = MutableStateFlow("")
    private val isOnlineFlow = MutableStateFlow(true)

    @BeforeTest
    fun setup() {
        every { viewModel.notes } returns notesFlow
        every { viewModel.searchQuery } returns searchQueryFlow
        every { viewModel.isOnline } returns isOnlineFlow
    }

    @Test
    fun displaysNotesTitleAndOnlineStatus() {
        composeTestRule.setContent {
            NotesScreen(
                viewModel = viewModel,
                onAddNote = {},
                onNoteClick = {},
                onSettingsClick = {}
            )
        }

        composeTestRule.onNodeWithText("Notes").assertIsDisplayed()
        composeTestRule.onNodeWithText("Online").assertIsDisplayed()
    }

    @Test
    fun displaysNotesListItems() {
        val mockNotes = listOf(
            Note(1L, "Meeting Title", "Discuss final project", 100L, 100L),
            Note(2L, "Shopping List", "Buy milk and bread", 200L, 200L)
        )
        notesFlow.value = mockNotes

        composeTestRule.setContent {
            NotesScreen(
                viewModel = viewModel,
                onAddNote = {},
                onNoteClick = {},
                onSettingsClick = {}
            )
        }

        composeTestRule.onNodeWithText("Meeting Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Discuss final project").assertIsDisplayed()
        composeTestRule.onNodeWithText("Shopping List").assertIsDisplayed()
        composeTestRule.onNodeWithText("Buy milk and bread").assertIsDisplayed()
    }

    @Test
    fun displaysEmptyStateTextWhenNotesEmpty() {
        notesFlow.value = emptyList()

        composeTestRule.setContent {
            NotesScreen(
                viewModel = viewModel,
                onAddNote = {},
                onNoteClick = {},
                onSettingsClick = {}
            )
        }

        composeTestRule.onNodeWithText("Belum ada catatan.\nTekan + untuk membuat baru.").assertIsDisplayed()
    }

    @Test
    fun clickDeleteNoteIcon_triggersViewModelDelete() {
        val note = Note(42L, "Delete Me", "Some trash", 100L, 100L)
        notesFlow.value = listOf(note)

        composeTestRule.setContent {
            NotesScreen(
                viewModel = viewModel,
                onAddNote = {},
                onNoteClick = {},
                onSettingsClick = {}
            )
        }

        // Click delete icon for the note
        composeTestRule.onNodeWithContentDescription("Delete Note").performClick()

        // Verify view model is notified
        verify(exactly = 1) { viewModel.deleteNote(42L) }
    }
}
