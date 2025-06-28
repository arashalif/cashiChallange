package com.arshalif.cashi.core.remote

interface PaymentApiService {
    suspend fun sendPayment(request: com.arshalif.cashi.features.payment.data.model.PaymentRequestDto): PaymentResponse
    suspend fun getTransactionHistory(): TransactionsResponse
} 