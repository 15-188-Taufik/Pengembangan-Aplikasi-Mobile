package com.example.notesapp.data.platform

import kotlinx.coroutines.flow.Flow

expect class NetworkMonitor() {
    val isOnline: Flow<Boolean>
}
