package com.arshalif.cashi.features.payment.data.remote

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.data.model.PaymentRequestDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import com.arshalif.cashi.core.remote.PaymentApiService
import com.arshalif.cashi.core.remote.PaymentResponse
import com.arshalif.cashi.core.remote.TransactionsResponse
import kotlinx.datetime.Clock
import kotlin.random.Random

class MockPaymentApiService : PaymentApiService {
    
    override suspend fun sendPayment(request: PaymentRequestDto): PaymentResponse {
        // Simulate network delay
        kotlinx.coroutines.delay(500)
        
        val payment = PaymentDto(
            id = generateTransactionId(),
            recipientEmail = request.recipientEmail,
            amount = request.amount,
            currency = request.currency,
            timestamp = Clock.System.now().toString()
        )
        
        return PaymentResponse(
            success = true,
            message = "Payment processed successfully",
            payment = payment
        )
    }
    
    override suspend fun getTransactionHistory(): TransactionsResponse {
        // Simulate network delay
        kotlinx.coroutines.delay(300)
        
        val transactions = (1..5).map { index ->
            TransactionDto(
                id = generateTransactionId(),
                recipientEmail = "user$index@example.com",
                amount = Random.nextDouble(10.0, 1000.0),
                currency = listOf("USD", "EUR", "GBP").random(),
                timestamp = Clock.System.now().toString()
            )
        }
        
        return TransactionsResponse(
            success = true,
            transactions = transactions
        )
    }
    
    private fun generateTransactionId(): String {
        return "TXN${Random.nextInt(100000, 999999)}"
    }
} 