package com.taufik.notesapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taufik.notesapp.components.NoteCard
import com.taufik.notesapp.components.NotesTopBar
import com.taufik.notesapp.viewmodel.NotesViewModel

// ── NoteListScreen ──────────────────────────────────────────
@Composable
fun NoteListScreen(
    viewModel: NotesViewModel,
    onNoteClick: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { NotesTopBar(title = "Catatan Saya") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = Color(0xFF1A73E8),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah")
            }
        },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        if (uiState.notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Belum ada catatan.\nKlik + untuk menambah!",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(top = 8.dp)
            ) {
                items(uiState.notes) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onFavoriteToggle = { viewModel.toggleFavorite(note.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// ── FavoritesScreen ─────────────────────────────────────────
@Composable
fun FavoritesScreen(
    viewModel: NotesViewModel,
    onNoteClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites = uiState.notes.filter { it.isFavorite }

    Scaffold(
        topBar = { NotesTopBar(title = "Favorit") },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FavoriteBorder, null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Belum ada catatan favorit.", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(top = 8.dp)
            ) {
                items(favorites) { note ->
                    NoteCard(
                        note = note,
                        onClick = { onNoteClick(note.id) },
                        onFavoriteToggle = { viewModel.toggleFavorite(note.id) }
                    )
                }
            }
        }
    }
}

// ── ProfileTabScreen ─────────────────────────────────────────
@Composable
fun ProfileTabScreen() {
    Scaffold(
        topBar = { NotesTopBar(title = "Profil") },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A73E8))
            ) {
                Icon(
                    Icons.Default.Person, null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                "Nama Kamu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Mobile Developer · ITERA",
                fontSize = 13.sp,
                color = Color(0xFF00BFA6)
            )
            Spacer(Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D3D)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileInfoRow(Icons.Default.Email, "nama@student.itera.ac.id")
                    HorizontalDivider(color = Color(0xFF2C3E50))
                    ProfileInfoRow(Icons.Default.Phone, "+62 812-3456-7890")
                    HorizontalDivider(color = Color(0xFF2C3E50))
                    ProfileInfoRow(Icons.Default.LocationOn, "Lampung Selatan, Indonesia")
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(icon: ImageVector, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF1A73E8), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(value, color = Color.White, fontSize = 14.sp)
    }
}

// ── NoteDetailScreen ─────────────────────────────────────────
@Composable
fun NoteDetailScreen(
    noteId: Int,
    viewModel: NotesViewModel,
    onEditClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val note = uiState.notes.find { it.id == noteId }

    Scaffold(
        topBar = { NotesTopBar(title = "Detail Catatan", onBack = onBack) },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        if (note == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Catatan tidak ditemukan.", color = Color.Gray)
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                note.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(note.createdAt, fontSize = 12.sp, color = Color(0xFF4FC3F7))
                if (note.isFavorite) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Default.Favorite, null,
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF2C3E50))
            Spacer(Modifier.height(16.dp))
            Text(
                note.content,
                fontSize = 15.sp,
                color = Color.LightGray,
                lineHeight = 24.sp
            )
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { viewModel.toggleFavorite(noteId) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (note.isFavorite) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (note.isFavorite) Color(0xFFE57373) else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (note.isFavorite) "Unfavorite" else "Favorite",
                        color = Color.White
                    )
                }
                Button(
                    onClick = { onEditClick(noteId) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A73E8)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit")
                }
            }
        }
    }
}

// ── AddNoteScreen ────────────────────────────────────────────
@Composable
fun AddNoteScreen(
    onSave: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var title   by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Scaffold(
        topBar = { NotesTopBar(title = "Catatan Baru", onBack = onBack) },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = noteTextFieldColors()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi catatan...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 6,
                shape = RoundedCornerShape(12.dp),
                colors = noteTextFieldColors()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onSave(title, content) },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A73E8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Simpan Catatan", fontSize = 15.sp)
            }
        }
    }
}

// ── EditNoteScreen ───────────────────────────────────────────
@Composable
fun EditNoteScreen(
    noteId: Int,
    viewModel: NotesViewModel,
    onSave: (Int, String, String) -> Unit,
    onBack: () -> Unit
) {
    val note    = viewModel.getNoteById(noteId)
    var title   by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }

    Scaffold(
        topBar = { NotesTopBar(title = "Edit Catatan", onBack = onBack) },
        containerColor = Color(0xFF0D1B2A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Judul") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = noteTextFieldColors()
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Isi catatan...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                minLines = 6,
                shape = RoundedCornerShape(12.dp),
                colors = noteTextFieldColors()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { onSave(noteId, title, content) },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A73E8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Simpan Perubahan", fontSize = 15.sp)
            }
        }
    }
}

// ── Helper warna TextField ───────────────────────────────────
@Composable
fun noteTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Color(0xFF1A73E8),
    unfocusedBorderColor = Color(0xFF2C3E50),
    focusedLabelColor    = Color(0xFF1A73E8),
    unfocusedLabelColor  = Color.Gray,
    focusedTextColor     = Color.White,
    unfocusedTextColor   = Color.White,
    cursorColor          = Color(0xFF1A73E8)
)