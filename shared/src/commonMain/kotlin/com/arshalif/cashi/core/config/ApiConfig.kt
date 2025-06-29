package com.arshalif.cashi.core.config

object ApiConfig {
    // For local development with USB debugging (adb reverse tcp:8080 tcp:8080)
    const val BASE_URL = "http://localhost:8080"
    
    // Alternative configurations for different scenarios:
    // const val BASE_URL = "http://10.0.2.2:8080" // For Android emulator
    // const val BASE_URL = "http://192.168.1.100:8080" // For physical device on same network
    // const val BASE_URL = "https://api.example.com" // For production
} 