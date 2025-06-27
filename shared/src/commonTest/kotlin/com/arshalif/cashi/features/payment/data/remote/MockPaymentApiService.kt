package com.arshalif.cashi.features.payment.data.remote

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import kotlinx.datetime.Clock
import kotlin.random.Random

class MockPaymentApiService : PaymentApiService {
    
    override suspend fun sendPayment(request: PaymentRequestDto): PaymentDto {
        // Simulate network delay
        kotlinx.coroutines.delay(500)
        
        return PaymentDto(
            id = generateTransactionId(),
            recipientEmail = request.recipientEmail,
            amount = request.amount,
            currency = request.currency,
            timestamp = Clock.System.now().toString()
        )
    }
    
    override suspend fun getTransactionHistory(): List<TransactionDto> {
        // Simulate network delay
        kotlinx.coroutines.delay(300)
        
        return listOf(
            TransactionDto(
                id = generateTransactionId(),
                recipientEmail = "john@example.com",
                amount = 100.0,
                currency = "USD",
                timestamp = Clock.System.now().toString(),
                status = "COMPLETED",
                transactionType = "PAYMENT"
            ),
            TransactionDto(
                id = generateTransactionId(),
                recipientEmail = "jane@example.com",
                amount = 250.0,
                currency = "EUR",
                timestamp = Clock.System.now().toString(),
                status = "COMPLETED",
                transactionType = "PAYMENT"
            ),
            TransactionDto(
                id = generateTransactionId(),
                recipientEmail = "bob@example.com",
                amount = 75.0,
                currency = "GBP",
                timestamp = Clock.System.now().toString(),
                status = "PENDING",
                transactionType = "PAYMENT"
            )
        )
    }
    
    private fun generateTransactionId(): String {
        return "TXN${Random.nextInt(100000, 999999)}"
    }
} 