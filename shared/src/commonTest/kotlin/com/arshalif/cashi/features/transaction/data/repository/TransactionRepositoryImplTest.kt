package com.arshalif.cashi.features.transaction.data.repository

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.data.remote.MockApiService
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TransactionRepositoryImplTest {
    
    @Test
    fun `getTransactions returns list of transactions`() = runTest {
        // Given
        val apiService = MockApiService()
        val repository = TransactionRepositoryImpl(apiService)
        
        // When
        val result = repository.getTransactions()
        
        // Then
        assertTrue(result is NetworkResult.Success)
        val transactions = (result as NetworkResult.Success).data
        assertTrue(transactions.isNotEmpty())
        assertTrue(transactions.size == 5) // Mock service returns 5 transactions
        
        // Verify first transaction structure
        val firstTransaction = transactions.first()
        assertTrue(firstTransaction.recipientEmail.contains("@example.com"))
        assertTrue(firstTransaction.amount > 0.0)
        assertTrue(listOf("USD", "EUR", "GBP").contains(firstTransaction.currency.code))
    }
    
    @Test
    fun `getTransactions handles different currencies`() = runTest {
        // Given
        val apiService = MockApiService()
        val repository = TransactionRepositoryImpl(apiService)
        
        // When
        val result = repository.getTransactions()
        
        // Then
        assertTrue(result is NetworkResult.Success)
        val transactions = (result as NetworkResult.Success).data
        
        // Check that we have transactions with different currencies
        val currencies = transactions.map { it.currency.code }.toSet()
        assertTrue(currencies.isNotEmpty())
        
        // All currencies should be supported ones
        currencies.forEach { currency ->
            assertTrue(listOf("USD", "EUR", "GBP").contains(currency))
        }
    }
} 