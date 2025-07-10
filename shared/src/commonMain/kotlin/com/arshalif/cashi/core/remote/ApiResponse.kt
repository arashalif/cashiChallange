package com.arshalif.cashi.core.remote

import kotlinx.serialization.Serializable

// Generic API response wrapper
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: String? = null
)