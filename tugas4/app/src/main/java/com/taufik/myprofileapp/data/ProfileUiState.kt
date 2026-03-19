package com.taufik.myprofileapp.data

// Data class untuk menyimpan semua UI state profil
data class ProfileUiState(
    val name: String = "Nama Kamu",
    val title: String = "Mobile Dev · ITERA",
    val bio: String = "Mahasiswa Teknik Informatika ITERA yang passionate di mobile development.",
    val email: String = "nama@student.itera.ac.id",
    val phone: String = "+62 812-3456-7890",
    val location: String = "Lampung Selatan, Indonesia",
    val isDarkMode: Boolean = true,
    val isEditMode: Boolean = false
)
