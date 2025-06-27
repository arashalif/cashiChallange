package com.arshalif.cashi.features.transaction.data.repository

import com.arshalif.cashi.features.payment.data.remote.MockPaymentApiService
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TransactionRepositoryImplTest {
    
    @Test
    fun `getTransactions returns list of transactions`() = runTest {
        // Given
        val apiService = MockPaymentApiService()
        val repository = TransactionRepositoryImpl(apiService)
        
        // When
        val transactions = repository.getTransactions().first()
        
        // Then
        assertTrue(transactions.isNotEmpty())
        assertTrue(transactions.size == 3) // Mock service returns 3 transactions
        
        // Verify first transaction
        val firstTransaction = transactions.first()
        assertTrue(firstTransaction.recipientEmail == "john@example.com")
        assertTrue(firstTransaction.amount == 100.0)
        assertTrue(firstTransaction.currency.code == "USD")
    }
    
    @Test
    fun `getTransactions handles different currencies`() = runTest {
        // Given
        val apiService = MockPaymentApiService()
        val repository = TransactionRepositoryImpl(apiService)
        
        // When
        val transactions = repository.getTransactions().first()
        
        // Then
        val currencies = transactions.map { it.currency.code }
        assertTrue(currencies.contains("USD"))
        assertTrue(currencies.contains("EUR"))
        assertTrue(currencies.contains("GBP"))
    }
} 