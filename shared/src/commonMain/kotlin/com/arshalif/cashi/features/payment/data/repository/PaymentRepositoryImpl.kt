package com.arshalif.cashi.features.payment.data.repository

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.core.remote.PaymentApiService
import com.arshalif.cashi.features.payment.data.model.toRequestDto
import com.arshalif.cashi.features.payment.data.model.toDomain
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.repository.PaymentRepository

class PaymentRepositoryImpl(
    private val apiService: PaymentApiService
) : PaymentRepository {
    
    override suspend fun sendPayment(payment: Payment): NetworkResult<Payment> {
        return try {
            val request = payment.toRequestDto()
            val response = apiService.sendPayment(request)
            if (response.success && response.payment != null) {
                NetworkResult.Success(response.payment.toDomain())
            } else {
                NetworkResult.Error(response.error ?: response.message)
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to send payment")
        }
    }
} 