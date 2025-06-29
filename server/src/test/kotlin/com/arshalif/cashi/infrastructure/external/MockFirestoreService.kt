package com.arshalif.cashi.infrastructure.external

import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.transaction.data.model.TransactionDto
import java.util.concurrent.ConcurrentHashMap

class MockFirestoreService : FirestoreServiceInterface {
    // In-memory storage for tests
    private val transactions = ConcurrentHashMap<String, TransactionDto>()
    
    override suspend fun savePayment(payment: PaymentDto): Result<Unit> {
        // Convert payment to transaction and store in memory
        val transaction = TransactionDto(
            id = payment.id,
            recipientEmail = payment.recipientEmail,
            amount = payment.amount,
            currency = payment.currency,
            timestamp = payment.timestamp
        )
        
        transactions[payment.id] = transaction
        println("MOCK: Saved payment ${payment.id} to in-memory storage (not real Firestore)")
        return Result.success(Unit)
    }
    
    override suspend fun getTransactions(): Result<List<TransactionDto>> {
        val sortedTransactions = transactions.values.toList()
            .sortedByDescending { it.timestamp }
        
        println("MOCK: Retrieved ${sortedTransactions.size} transactions from in-memory storage (not real Firestore)")
        return Result.success(sortedTransactions)
    }
    
    // Helper method to clear test data between tests
    fun clearTestData() {
        transactions.clear()
        println("MOCK: Cleared all test data")
    }
} 