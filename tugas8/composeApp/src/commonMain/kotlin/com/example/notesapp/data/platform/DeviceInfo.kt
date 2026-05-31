package com.example.notesapp.data.platform

expect class DeviceInfo() {
    val osName: String
    val osVersion: String
    val deviceModel: String
    val cpuArch: String
    val manufacturer: String
}
