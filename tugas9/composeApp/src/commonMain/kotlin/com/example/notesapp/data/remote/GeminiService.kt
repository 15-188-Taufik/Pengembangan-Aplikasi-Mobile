package com.example.notesapp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiService {
    private val client = HttpClient()

    companion object {
        private const val API_KEY = "AQ.Ab8RN6LrWJAnkg_7z6Q7ovIrHKWrSP3hVydV8Udb0vsZ6mUXkA"
        // Ganti nama model di sini jika Anda ingin menggunakan model lain (contoh: gemini-3.5-flash, gemini-2.5-flash, gemini-1.5-flash, dll)
        private const val MODEL_NAME = "gemini-3.5-flash"
    }

    suspend fun generateContent(
        prompt: String,
        systemPrompt: String? = null
    ): Result<String> = withContext(Dispatchers.Default) {
        try {
            val apiKey = API_KEY
            if (apiKey.isBlank()) {
                return@withContext Result.failure(Exception("Gemini API Key is empty!"))
            }

            val urlString = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            
            // Build the JSON body manually to keep it lightweight and KMP serialization-independent
            val requestBody = buildString {
                append("{")
                if (!systemPrompt.isNullOrBlank()) {
                    append("\"systemInstruction\": {")
                    append("\"parts\": [{\"text\": ${escapeJsonString(systemPrompt)}}]")
                    append("},")
                }
                append("\"contents\": [{")
                append("\"parts\": [{\"text\": ${escapeJsonString(prompt)}}]")
                append("}]")
                append("}")
            }

            val response = client.post(urlString) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (response.status == HttpStatusCode.OK) {
                val responseText = response.bodyAsText()
                val extractedText = parseGeminiResponse(responseText)
                if (extractedText != null) {
                    Result.success(extractedText)
                } else {
                    Result.failure(Exception("Failed to parse Gemini response. Raw: $responseText"))
                }
            } else {
                val errorBody = response.bodyAsText()
                val parsedError = parseGeminiError(errorBody) ?: errorBody
                Result.failure(Exception("API Error: $parsedError"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun escapeJsonString(str: String): String {
        return buildString {
            append("\"")
            for (c in str) {
                when (c) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> {
                        if (c.code < 32) {
                            append(String.format("\\u%04x", c.code))
                        } else {
                            append(c)
                        }
                    }
                }
            }
            append("\"")
        }
    }

    private fun parseGeminiResponse(json: String): String? {
        // Find "text" element within candidate: {"candidates": [{"content": {"parts": [{"text": "CONTENT"}]}}]}
        val textMarker = "\"text\":"
        var index = json.indexOf(textMarker)
        if (index == -1) return null
        
        index += textMarker.length
        // Move to the opening quote
        while (index < json.length && json[index] != '"') {
            index++
        }
        if (index >= json.length) return null
        index++ // Skip the opening quote
        
        val sb = StringBuilder()
        var escaped = false
        while (index < json.length) {
            val c = json[index]
            if (escaped) {
                when (c) {
                    'n' -> sb.append('\n')
                    't' -> sb.append('\t')
                    'r' -> sb.append('\r')
                    '\\' -> sb.append('\\')
                    '"' -> sb.append('"')
                    '/' -> sb.append('/')
                    'u' -> {
                        if (index + 4 < json.length) {
                            val hex = json.substring(index + 1, index + 5)
                            try {
                                sb.append(hex.toInt(16).toChar())
                            } catch (e: Exception) {
                                sb.append("\\u").append(hex)
                            }
                            index += 4
                        }
                    }
                    else -> sb.append(c)
                }
                escaped = false
            } else if (c == '\\') {
                escaped = true
            } else if (c == '"') {
                break // End of JSON string content
            } else {
                sb.append(c)
            }
            index++
        }
        return sb.toString()
    }

    private fun parseGeminiError(errorJson: String): String? {
        // Attempts to extract the detailed error message from response: {"error": {"message": "DETAILS"}}
        val messageMarker = "\"message\":"
        var index = errorJson.indexOf(messageMarker)
        if (index == -1) return null
        
        index += messageMarker.length
        while (index < errorJson.length && errorJson[index] != '"') {
            index++
        }
        if (index >= errorJson.length) return null
        index++
        
        val sb = StringBuilder()
        var escaped = false
        while (index < errorJson.length) {
            val c = errorJson[index]
            if (escaped) {
                when (c) {
                    'n' -> sb.append('\n')
                    't' -> sb.append('\t')
                    '"' -> sb.append('"')
                    '\\' -> sb.append('\\')
                    else -> sb.append(c)
                }
                escaped = false
            } else if (c == '\\') {
                escaped = true
            } else if (c == '"') {
                break
            } else {
                sb.append(c)
            }
            index++
        }
        return sb.toString()
    }
}
