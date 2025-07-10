package com.arshalif.cashi.repository

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.repository.PaymentRepository
import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.infrastructure.external.FirestoreServiceInterface
import kotlinx.datetime.Clock

class ServerPaymentRepository(
    private val firestoreService: FirestoreServiceInterface
) : PaymentRepository {
    
    override suspend fun sendPayment(payment: Payment): NetworkResult<Payment> {
        return try {
            // Convert domain model to DTO
            val paymentDto = PaymentDto(
                id = generatePaymentId(),
                recipientEmail = payment.recipientEmail,
                amount = payment.amount,
                currency = payment.currency.name,
                timestamp = payment.timestamp.toString()
            )
            
            // Save to Firestore
            firestoreService.savePayment(paymentDto).fold(
                onSuccess = {
                    // Return success with the original payment
                    NetworkResult.Success(payment)
                },
                onFailure = { error ->
                    NetworkResult.Error("Failed to save payment: ${error.message}")
                }
            )
        } catch (e: Exception) {
            NetworkResult.Error("Payment processing failed: ${e.message}")
        }
    }
    
    private fun generatePaymentId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }
} 