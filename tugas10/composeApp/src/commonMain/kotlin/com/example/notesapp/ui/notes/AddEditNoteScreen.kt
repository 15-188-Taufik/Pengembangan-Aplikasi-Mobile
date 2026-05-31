package com.example.notesapp.ui.notes

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    viewModel: AddEditNoteViewModel,
    noteId: Long?,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()
    val aiError by viewModel.aiError.collectAsState()
    val aiResult by viewModel.aiResult.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()

    var isAiSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(noteId) {
        if (noteId != null && noteId != -1L) {
            viewModel.loadNote(noteId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == null || noteId == -1L) "Add Note" else "Edit Note", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Sparkling AI Assistant button
                    IconButton(
                        onClick = { 
                            viewModel.clearAIState()
                            isAiSheetOpen = true 
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF7F52FF), Color(0xFFE244DE), Color(0xFF4285F4))
                                    ),
                                    shape = CircleShape
                                )
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Assistant",
                                    tint = Color(0xFF7F52FF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    IconButton(onClick = { viewModel.saveNote(onBack) }) {
                        Icon(Icons.Default.Check, contentDescription = "Save Note")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = content,
                onValueChange = viewModel::onContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Content") }
            )
        }
    }

    // AI Assistant Bottom Sheet
    if (isAiSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        
        ModalBottomSheet(
            onDismissRequest = { isAiSheetOpen = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AIModalContent(
                aiLoading = aiLoading,
                aiError = aiError,
                aiResult = aiResult,
                chatHistory = chatHistory,
                onSummarize = { viewModel.summarizeNote() },
                onEnhance = { viewModel.enhanceNote() },
                onTranslate = { lang -> viewModel.translateNote(lang) },
                onBrainstorm = { viewModel.brainstormNote() },
                onSendChat = { msg -> viewModel.sendChatMessage(msg) },
                onClearChat = { viewModel.clearChatHistory() },
                onAppendResult = { 
                    viewModel.appendResultToNote()
                    scope.launch { sheetState.hide() }.invokeOnCompletion { 
                        isAiSheetOpen = false 
                    }
                },
                onReplaceNote = { 
                    viewModel.replaceNoteWithResult()
                    scope.launch { sheetState.hide() }.invokeOnCompletion { 
                        isAiSheetOpen = false 
                    }
                },
                onCopyResult = { text ->
                    clipboardManager.setText(AnnotatedString(text))
                },
                onSettingsClick = {
                    isAiSheetOpen = false
                    onSettingsClick()
                },
                onClearAIError = { viewModel.clearAIState() }
            )
        }
    }
}

@Composable
fun AIModalContent(
    aiLoading: Boolean,
    aiError: String?,
    aiResult: String?,
    chatHistory: List<ChatMessage>,
    onSummarize: () -> Unit,
    onEnhance: () -> Unit,
    onTranslate: (String) -> Unit,
    onBrainstorm: () -> Unit,
    onSendChat: (String) -> Unit,
    onClearChat: () -> Unit,
    onAppendResult: () -> Unit,
    onReplaceNote: () -> Unit,
    onCopyResult: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onClearAIError: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // AI Title Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFF7F52FF)
            )
            Text(
                text = "Gemini Note Assistant",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Tab Navigation
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Quick Tools")
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text("Ask AI")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Body Content based on active tab
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> QuickToolsTab(
                    aiLoading = aiLoading,
                    aiError = aiError,
                    aiResult = aiResult,
                    onSummarize = onSummarize,
                    onEnhance = onEnhance,
                    onTranslate = onTranslate,
                    onBrainstorm = onBrainstorm,
                    onAppendResult = onAppendResult,
                    onReplaceNote = onReplaceNote,
                    onCopyResult = onCopyResult,
                    onSettingsClick = onSettingsClick,
                    onClearAIError = onClearAIError
                )
                1 -> ChatbotTab(
                    aiLoading = aiLoading,
                    aiError = aiError,
                    chatHistory = chatHistory,
                    onSendChat = onSendChat,
                    onClearChat = onClearChat,
                    onSettingsClick = onSettingsClick,
                    onClearAIError = onClearAIError
                )
            }
        }
    }
}

@Composable
fun QuickToolsTab(
    aiLoading: Boolean,
    aiError: String?,
    aiResult: String?,
    onSummarize: () -> Unit,
    onEnhance: () -> Unit,
    onTranslate: (String) -> Unit,
    onBrainstorm: () -> Unit,
    onAppendResult: () -> Unit,
    onReplaceNote: () -> Unit,
    onCopyResult: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onClearAIError: () -> Unit
) {
    var showTranslationLanguages by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!aiLoading && aiError == null && aiResult == null) {
            // Quick Tools Selection Grid
            Text(
                text = "Pilih alat cerdas untuk mengolah catatan Anda:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ToolCard(
                    title = "Rangkum",
                    description = "Buat ringkasan ringkas terstruktur",
                    icon = Icons.Default.MenuBook,
                    color = Color(0xFFE8F5E9),
                    contentColor = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f),
                    onClick = onSummarize
                )
                ToolCard(
                    title = "Sempurnakan",
                    description = "Perbaiki ejaan & bahasa",
                    icon = Icons.Default.AutoAwesome,
                    color = Color(0xFFE1F5FE),
                    contentColor = Color(0xFF0288D1),
                    modifier = Modifier.weight(1f),
                    onClick = onEnhance
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ToolCard(
                    title = "Terjemahkan",
                    description = "Ubah bahasa catatan",
                    icon = Icons.Default.Translate,
                    color = Color(0xFFEDE7F6),
                    contentColor = Color(0xFF5E35B1),
                    modifier = Modifier.weight(1f),
                    onClick = { showTranslationLanguages = !showTranslationLanguages }
                )
                ToolCard(
                    title = "Ide Kreatif",
                    description = "Brainstorm gagasan relevan",
                    icon = Icons.Default.Lightbulb,
                    color = Color(0xFFFFFDE7),
                    contentColor = Color(0xFFF57F17),
                    modifier = Modifier.weight(1f),
                    onClick = onBrainstorm
                )
            }

            if (showTranslationLanguages) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Pilih bahasa tujuan:",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LanguageChip(label = "Inggris", onClick = { onTranslate("Inggris") })
                        LanguageChip(label = "Indonesia", onClick = { onTranslate("Indonesia") })
                        LanguageChip(label = "Jepang", onClick = { onTranslate("Jepang") })
                        LanguageChip(label = "Arab", onClick = { onTranslate("Arab") })
                    }
                }
            }
        }

        // Loading view
        if (aiLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF7F52FF),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AI sedang meramu konten...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Error view
        if (aiError != null && !aiLoading) {
            ErrorCard(
                errorMessage = aiError,
                onSettingsClick = onSettingsClick,
                onDismiss = onClearAIError
            )
        }

        // Result view
        if (aiResult != null && !aiLoading && aiError == null) {
            var copyClicked by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hasil Analisis AI:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { 
                            onCopyResult(aiResult)
                            copyClicked = true
                        }) {
                            Icon(
                                imageVector = if (copyClicked) Icons.Default.Done else Icons.Default.ContentCopy,
                                contentDescription = "Copy text",
                                tint = if (copyClicked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = onClearAIError) {
                            Icon(Icons.Default.Close, contentDescription = "Ulangi")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        item {
                            Text(
                                text = aiResult,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Apply actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplaceNote,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FindReplace, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ganti Catatan")
                    }
                    Button(
                        onClick = onAppendResult,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F52FF))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sematkan di Bawah")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatbotTab(
    aiLoading: Boolean,
    aiError: String?,
    chatHistory: List<ChatMessage>,
    onSendChat: (String) -> Unit,
    onClearChat: () -> Unit,
    onSettingsClick: () -> Unit,
    onClearAIError: () -> Unit
) {
    var textInput by remember { mutableStateOf(TextFieldValue("")) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(chatHistory.size, aiLoading) {
        if (chatHistory.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(chatHistory.size)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat History List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (chatHistory.isEmpty()) {
                // Welcoming Empty Chat UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tanya AI tentang Catatan Ini",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "AI mengerti isi catatan Anda. Coba tanyakan gagasan utama atau minta bantuan menulis.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Preset suggestions chips
                    Text(
                        text = "Pertanyaan Cepat:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SuggestionChip(
                            label = "Gagasan Utama?",
                            onClick = { onSendChat("Apa gagasan utama dari catatan saya ini?") }
                        )
                        SuggestionChip(
                            label = "Langkah Nyata?",
                            onClick = { onSendChat("Berikan saran langkah nyata berikutnya berdasarkan tulisan saya ini.") }
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(chatHistory) { msg ->
                        ChatBubble(message = msg)
                    }
                    
                    if (aiLoading) {
                        item {
                            TypingIndicator()
                        }
                    }

                    if (aiError != null && !aiLoading) {
                        item {
                            ErrorCard(
                                errorMessage = aiError,
                                onSettingsClick = onSettingsClick,
                                onDismiss = onClearAIError
                            )
                        }
                    }
                }
            }
        }

        // Input bottom bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (chatHistory.isNotEmpty()) {
                IconButton(onClick = onClearChat) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Bersihkan percakapan",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Tanya asisten...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (textInput.text.isNotBlank()) {
                                onSendChat(textInput.text)
                                textInput = TextFieldValue("")
                            }
                        },
                        enabled = !aiLoading && textInput.text.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Kirim",
                            tint = if (textInput.text.isNotBlank() && !aiLoading) Color(0xFF7F52FF) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ToolCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun LanguageChip(label: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun SuggestionChip(label: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFF7F52FF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            modifier = Modifier
                .padding(vertical = 4.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF7F52FF), shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFFE244DE), shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFF4285F4), shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "AI sedang mengetik...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ErrorCard(
    errorMessage: String,
    onSettingsClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Gangguan Sistem",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            if (errorMessage.contains("API Key") || errorMessage.contains("belum diatur")) {
                Button(
                    onClick = onSettingsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pengaturan Key")
                }
            }
        }
    }
}
