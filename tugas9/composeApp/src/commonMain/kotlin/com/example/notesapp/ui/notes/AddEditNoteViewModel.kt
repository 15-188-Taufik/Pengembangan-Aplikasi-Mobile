package com.example.notesapp.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.platform.NetworkMonitor
import com.example.notesapp.data.remote.GeminiService
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.data.settings.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChatMessage(val role: String, val content: String)

class AddEditNoteViewModel(
    private val repository: NoteRepository,
    private val geminiService: GeminiService,
    private val settingsManager: SettingsManager,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private var currentNoteId: Long? = null

    // AI Assistant States
    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    private val _aiResult = MutableStateFlow<String?>(null)
    val aiResult: StateFlow<String?> = _aiResult.asStateFlow()

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    fun loadNote(id: Long) {
        if (currentNoteId == id) return
        viewModelScope.launch {
            repository.getNoteById(id)?.let { note ->
                currentNoteId = note.id
                _title.value = note.title
                _content.value = note.content
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
    }

    fun saveNote(onSaved: () -> Unit) {
        viewModelScope.launch {
            val titleVal = _title.value
            val contentVal = _content.value
            if (titleVal.isNotBlank() || contentVal.isNotBlank()) {
                val id = currentNoteId
                if (id == null) {
                    repository.insertNote(titleVal, contentVal)
                } else {
                    repository.updateNote(id, titleVal, contentVal)
                }
            }
            onSaved()
        }
    }

    // AI Assistant quick actions
    fun clearAIState() {
        _aiError.value = null
        _aiResult.value = null
    }

    private suspend fun runAIPipelines(
        systemPrompt: String,
        prompt: String,
        onSuccess: (String) -> Unit
    ) {
        _aiLoading.value = true
        _aiError.value = null
        try {
            // Check Network Connection
            if (!networkMonitor.isOnline.first()) {
                _aiError.value = "Tidak ada koneksi internet. Silakan aktifkan koneksi Anda."
                _aiLoading.value = false
                return
            }

            val result = geminiService.generateContent(prompt, systemPrompt)
            result.fold(
                onSuccess = { text ->
                    onSuccess(text)
                },
                onFailure = { error ->
                    _aiError.value = error.message ?: "Terjadi kesalahan tidak dikenal saat menghubungi AI."
                }
            )
        } catch (e: Exception) {
            _aiError.value = e.message ?: "Terjadi kesalahan sistem saat menghubungi AI."
        } finally {
            _aiLoading.value = false
        }
    }

    fun summarizeNote() {
        val noteContent = _content.value
        if (noteContent.isBlank()) {
            _aiError.value = "Konten catatan Anda kosong. Tulis sesuatu terlebih dahulu sebelum merangkum."
            return
        }

        val systemPrompt = "You are an intelligent, expert note summary assistant. You specialize in synthesizing Indonesian and English texts into beautiful, structured, readable key takeaways and summaries."
        val prompt = "Rangkum catatan berikut ini secara profesional menggunakan poin-poin terstruktur yang rapi:\n\nCatatan:\n$noteContent"

        viewModelScope.launch {
            runAIPipelines(systemPrompt, prompt) { text ->
                _aiResult.value = text
            }
        }
    }

    fun enhanceNote() {
        val noteContent = _content.value
        if (noteContent.isBlank()) {
            _aiError.value = "Konten catatan Anda kosong. Tulis sesuatu sebelum menggunakan fitur ini."
            return
        }

        val systemPrompt = "You are a professional writing editor. You improve sentence structure, vocabulary, and grammar of notes while preserving the original meaning."
        val prompt = "Tulis ulang dan sempurnakan catatan berikut agar terdengar lebih profesional, rapi, dan mudah dibaca. Perbaiki jika ada kesalahan ejaan:\n\nCatatan:\n$noteContent"

        viewModelScope.launch {
            runAIPipelines(systemPrompt, prompt) { text ->
                _aiResult.value = text
            }
        }
    }

    fun translateNote(targetLang: String) {
        val noteContent = _content.value
        if (noteContent.isBlank()) {
            _aiError.value = "Konten catatan Anda kosong. Tulis sesuatu sebelum menerjemahkan."
            return
        }

        val systemPrompt = "You are an expert translator. You translate text accurately and naturally while maintaining original meaning and tone."
        val prompt = "Terjemahkan catatan berikut ke bahasa $targetLang secara akurat dan alami:\n\nCatatan:\n$noteContent"

        viewModelScope.launch {
            runAIPipelines(systemPrompt, prompt) { text ->
                _aiResult.value = text
            }
        }
    }

    fun brainstormNote() {
        val noteContent = _content.value
        val noteTitle = _title.value
        if (noteContent.isBlank() && noteTitle.isBlank()) {
            _aiError.value = "Judul atau konten catatan Anda kosong. Tulis gagasan awal terlebih dahulu."
            return
        }

        val systemPrompt = "You are a creative brainstorming assistant. You help people expand their thoughts, generate new ideas, and plan next actions."
        val prompt = "Berdasarkan catatan dengan judul '$noteTitle' dan isi '$noteContent', berikan 3 ide kreatif tambahan atau langkah nyata berikutnya yang relevan dalam bentuk poin-poin yang menarik."

        viewModelScope.launch {
            runAIPipelines(systemPrompt, prompt) { text ->
                _aiResult.value = text
            }
        }
    }

    // Conversational Chatbot
    fun sendChatMessage(message: String) {
        if (message.isBlank()) return

        val userMsg = ChatMessage("user", message)
        _chatHistory.value = _chatHistory.value + userMsg
        _aiLoading.value = true
        _aiError.value = null

        val noteTitle = _title.value
        val noteContent = _content.value

        val systemPrompt = "You are an intelligent writing co-pilot and AI assistant. The user is currently editing a note with TITLE: '$noteTitle' and CONTENT: '$noteContent'. " +
                "You must help them answer questions, expand their thoughts, or explain concepts related to the note using it as context. " +
                "Be brief, engaging, polite, and answer in the language used by the user."

        // Format history into the prompt to support multi-turn conversation
        val prompt = buildString {
            append("Berikut adalah riwayat percakapan sebelumnya:\n")
            _chatHistory.value.forEach { msg ->
                val roleName = if (msg.role == "user") "User" else "AI"
                append("$roleName: ${msg.content}\n")
            }
            append("User: $message\n")
            append("AI:")
        }

        viewModelScope.launch {
            try {
                if (!networkMonitor.isOnline.first()) {
                    _aiError.value = "Tidak ada koneksi internet. Gagal mengirim pesan."
                    _aiLoading.value = false
                    return@launch
                }

                val result = geminiService.generateContent(prompt, systemPrompt)
                result.fold(
                    onSuccess = { text ->
                        val assistantMsg = ChatMessage("model", text)
                        _chatHistory.value = _chatHistory.value + assistantMsg
                    },
                    onFailure = { error ->
                        _aiError.value = error.message ?: "Terjadi kesalahan tidak dikenal saat menghubungi AI."
                    }
                )
            } catch (e: Exception) {
                _aiError.value = e.message ?: "Terjadi kesalahan sistem."
            } finally {
                _aiLoading.value = false
            }
        }
    }

    fun clearChatHistory() {
        _chatHistory.value = emptyList()
    }

    // Apply Actions
    fun appendResultToNote() {
        _aiResult.value?.let { result ->
            val currentText = _content.value
            _content.value = if (currentText.isBlank()) {
                result
            } else {
                "$currentText\n\n=== AI Assistant ===\n$result"
            }
        }
    }

    fun replaceNoteWithResult() {
        _aiResult.value?.let { result ->
            _content.value = result
        }
    }
}
