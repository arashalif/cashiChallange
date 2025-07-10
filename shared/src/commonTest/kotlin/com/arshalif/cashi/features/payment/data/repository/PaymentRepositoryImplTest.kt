package com.arshalif.cashi.features.payment.data.repository

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.data.remote.MockApiService
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertTrue

class PaymentRepositoryImplTest {
    
    @Test
    fun `sendPayment returns success with valid payment`() = runTest {
        // Given
        val apiService = MockApiService()
        val repository = PaymentRepositoryImpl(apiService)
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 100.0,
            currency = Currency.USD,
            timestamp = Clock.System.now()
        )
        
        // When
        val result = repository.sendPayment(payment)
        
        // Then
        assertTrue(result is NetworkResult.Success)
        val successResult = result as NetworkResult.Success
        assertTrue(successResult.data.recipientEmail == "test@example.com")
        assertTrue(successResult.data.amount == 100.0)
        assertTrue(successResult.data.currency == Currency.USD)
    }
    
    @Test
    fun `sendPayment handles different currencies`() = runTest {
        // Given
        val apiService = MockApiService()
        val repository = PaymentRepositoryImpl(apiService)
        val payment = Payment(
            recipientEmail = "test@example.com",
            amount = 50.0,
            currency = Currency.EUR,
            timestamp = Clock.System.now()
        )
        
        // When
        val result = repository.sendPayment(payment)
        
        // Then
        assertTrue(result is NetworkResult.Success)
        val successResult = result as NetworkResult.Success
        assertTrue(successResult.data.currency == Currency.EUR)
    }
} 