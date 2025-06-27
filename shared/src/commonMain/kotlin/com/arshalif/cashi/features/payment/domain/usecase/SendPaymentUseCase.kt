package com.arshalif.cashi.features.payment.domain.usecase

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.repository.PaymentRepository
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator

class SendPaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val paymentValidator: PaymentValidator
) {
    suspend operator fun invoke(payment: Payment): NetworkResult<Payment> {
        // Validate payment before sending
        val validationResult = paymentValidator.validatePayment(payment)
        if (!validationResult.isValid) {
            return NetworkResult.Error(validationResult.errorMessage ?: "Invalid payment data")
        }
        
        return paymentRepository.sendPayment(payment)
    }
} 