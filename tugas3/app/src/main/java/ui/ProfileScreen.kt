@file:Suppress("SpellCheckingInspection")

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
            .verticalScroll(rememberScrollState())
    ) {
        // ── 1. Header ──
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically()
        ) {
            ProfileHeader(
                name = "TAUFIK HIDAYAT NST",
                title = "Mobile Dev · ITERA"
            )
        }

        // ── 2. Bio ──
        ProfileCard(title = "Tentang Saya") {
            Text(
                text = "Mahasiswa Teknik Informatika ITERA " +
                        "yang passionate di mobile development.",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 22.sp
            )
        }

        // ── 3. Kontak (pakai InfoItem) ──
        ProfileCard(title = "Informasi Kontak") {
            InfoItem(Icons.Default.Email, "EMAIL",
                "taufik.123140188@student.itera.ac.id")
            HorizontalDivider()
            InfoItem(Icons.Default.Phone, "TELEPON",
                "+62 895-1795-4410")
            HorizontalDivider()
            InfoItem(Icons.Default.LocationOn, "LOKASI",
                "Bandar Lampung, Indonesia")
        }

        // ── 4. Tombol Aksi ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { },
                modifier = Modifier.weight(1f)
            ) { Text("Edit Profile") }

            OutlinedButton(
                onClick = { },
                modifier = Modifier.weight(1f)
            ) { Text("Share") }
        }

        Spacer(Modifier.height(24.dp))
    }
}
