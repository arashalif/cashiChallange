package com.arshalif.cashi.core.remote

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto

interface ApiService {
    suspend fun sendPayment(request: PaymentRequestDto): ApiResponse<PaymentDto>
    suspend fun getTransactionHistory(): ApiResponse<List<TransactionDto>>
} 