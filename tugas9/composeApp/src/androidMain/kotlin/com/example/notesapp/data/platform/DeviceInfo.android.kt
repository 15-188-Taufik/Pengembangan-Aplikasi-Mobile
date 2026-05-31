package com.example.notesapp.data.platform

actual class DeviceInfo actual constructor() {
    actual val osName: String = "Android"
    actual val osVersion: String = android.os.Build.VERSION.RELEASE
    actual val deviceModel: String = android.os.Build.MODEL
    actual val cpuArch: String = android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"
    actual val manufacturer: String = android.os.Build.MANUFACTURER
}
