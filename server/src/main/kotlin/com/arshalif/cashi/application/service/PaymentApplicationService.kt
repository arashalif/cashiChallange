package com.arshalif.cashi.application.service

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.infrastructure.external.FirestoreServiceInterface
import kotlinx.datetime.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class PaymentApplicationService(
    private val sendPaymentUseCase: SendPaymentUseCase,
    private val firestoreService: FirestoreServiceInterface
) {
    // In-memory storage for fallback
    private val payments = ConcurrentHashMap<String, PaymentDto>()
    private val paymentIdCounter = AtomicLong(1)
    
    suspend fun processPayment(
        recipientEmail: String,
        amount: Double,
        currency: String
    ): PaymentDto {
        val paymentId = paymentIdCounter.getAndIncrement().toString()
        val timestamp = Clock.System.now()
        val currencyEnum = Currency.valueOf(currency.uppercase())
        
        // Create domain payment object
        val payment = Payment(
            recipientEmail = recipientEmail,
            amount = amount,
            currency = currencyEnum,
            timestamp = timestamp
        )
        
        // Use the shared use case for business logic
        val result = sendPaymentUseCase(payment)
        
        when (result) {
            is NetworkResult.Error -> {
                throw Exception("Payment processing failed: ${result.message}")
            }
            is NetworkResult.Success -> {
                // Continue with processing
            }
            is NetworkResult.Loading -> {
                throw Exception("Payment processing is still loading")
            }
        }
        
        // Create DTO for storage
        val paymentDto = PaymentDto(
            id = paymentId,
            recipientEmail = recipientEmail,
            amount = amount,
            currency = currencyEnum.name,
            timestamp = timestamp.toString()
        )
        
        // Store in memory (fallback)
        payments[paymentId] = paymentDto
        
        // Save to Firestore
        firestoreService.savePayment(paymentDto).onFailure { error ->
            println("!!! Failed to save payment to Firestore: ${error.message}")
        }
        
        return paymentDto
    }
} 