package com.taufik.myprofileapp.viewmodel

import androidx.lifecycle.ViewModel
import com.taufik.myprofileapp.data.ProfileUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    // Private mutable state — hanya bisa diubah dari dalam ViewModel
    private val _uiState = MutableStateFlow(ProfileUiState())

    // Public read-only state — dikonsumsi oleh UI
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // ── Toggle dark/light mode ──
    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    // ── Buka form edit profile ──
    fun openEditMode() {
        _uiState.update { it.copy(isEditMode = true) }
    }

    // ── Tutup form edit (batal) ──
    fun closeEditMode() {
        _uiState.update { it.copy(isEditMode = false) }
    }

    // ── Simpan perubahan nama dan bio ──
    fun saveProfile(newName: String, newBio: String) {
        _uiState.update {
            it.copy(
                name = newName.ifBlank { it.name },
                bio = newBio.ifBlank { it.bio },
                isEditMode = false
            )
        }
    }
}
