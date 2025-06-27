package com.arshalif.cashi.core.remote

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto

interface PaymentApiService {
    suspend fun sendPayment(request: PaymentRequestDto): PaymentDto
    suspend fun getTransactionHistory(): List<TransactionDto>
} 