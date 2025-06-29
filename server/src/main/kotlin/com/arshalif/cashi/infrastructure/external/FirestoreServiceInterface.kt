package com.arshalif.cashi.infrastructure.external

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto

interface FirestoreServiceInterface {
    suspend fun savePayment(payment: PaymentDto): Result<Unit>
    suspend fun getTransactions(): Result<List<TransactionDto>>
} 