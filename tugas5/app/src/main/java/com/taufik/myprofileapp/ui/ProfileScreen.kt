package com.taufik.myprofileapp.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taufik.myprofileapp.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    // Collect StateFlow sebagai Compose State
    val uiState by viewModel.uiState.collectAsState()

    // Warna background berubah sesuai dark/light mode
    val bgColor = if (uiState.isDarkMode) Color(0xFF0D1B2A) else Color(0xFFF5F5F5)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── 1. HEADER ──
            ProfileHeader(
                name = uiState.name,
                title = uiState.title,
                isDarkMode = uiState.isDarkMode,
                onEditClick = { viewModel.openEditMode() },
                onDarkModeToggle = { viewModel.toggleDarkMode() }
            )

            // ── 2. FORM EDIT (muncul saat isEditMode = true) ──
            AnimatedVisibility(
                visible = uiState.isEditMode,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                EditProfileForm(
                    currentName = uiState.name,
                    currentBio = uiState.bio,
                    onSave = { name, bio ->
                        viewModel.saveProfile(name, bio)
                    },
                    onCancel = { viewModel.closeEditMode() }
                )
            }

            // ── 3. BIO ──
            ProfileCard(title = "Tentang Saya") {
                Text(
                    text = uiState.bio,
                    color = Color.Gray
                )
            }

            // ── 4. INFORMASI KONTAK ──
            ProfileCard(title = "Informasi Kontak") {
                InfoItem(
                    icon = Icons.Default.Email,
                    label = "EMAIL",
                    value = uiState.email,
                    iconColor = Color(0xFF4FC3F7)
                )
                HorizontalDivider(color = Color(0xFF2C3E50))
                InfoItem(
                    icon = Icons.Default.Phone,
                    label = "TELEPON",
                    value = uiState.phone,
                    iconColor = Color(0xFF81C784)
                )
                HorizontalDivider(color = Color(0xFF2C3E50))
                InfoItem(
                    icon = Icons.Default.LocationOn,
                    label = "LOKASI",
                    value = uiState.location,
                    iconColor = Color(0xFFFF8A65)
                )
            }

            // ── 5. DARK MODE CARD ──
            ProfileCard(title = "Pengaturan") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.isDarkMode) Icons.Default.DarkMode
                                          else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = Color(0xFF1A73E8)
                        )
                        Text(
                            text = if (uiState.isDarkMode) "Dark Mode" else "Light Mode",
                            color = if (uiState.isDarkMode) Color.White else Color.Black
                        )
                    }
                    // Switch dark mode — state disimpan di ViewModel
                    Switch(
                        checked = uiState.isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF1A73E8)
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
