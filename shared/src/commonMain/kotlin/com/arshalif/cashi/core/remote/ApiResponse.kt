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

// Payment response matching server format
@Serializable
data class PaymentResponse(
    val success: Boolean,
    val message: String,
    val payment: com.arshalif.cashi.features.payment.data.model.PaymentDto? = null,
    val error: String? = null
)

// Transactions response matching server format
@Serializable
data class TransactionsResponse(
    val success: Boolean,
    val transactions: List<com.arshalif.cashi.features.transaction.data.model.TransactionDto>,
    val error: String? = null
) 