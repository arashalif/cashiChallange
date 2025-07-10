package com.arshalif.cashi.service

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.data.model.PaymentDto
import com.arshalif.cashi.features.payment.data.model.toRequestDto
import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.presentation.state.PaymentUiState
import kotlinx.datetime.Clock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

class PaymentApplicationService(
    private val sendPaymentUseCase: SendPaymentUseCase
) {
    
    suspend fun processPayment(
        payment: Payment
    ): NetworkResult<Payment> {
        // Use the shared use case for business logic and persistence
        val result: NetworkResult<Payment> = sendPaymentUseCase(payment)
        return result
    }
} 