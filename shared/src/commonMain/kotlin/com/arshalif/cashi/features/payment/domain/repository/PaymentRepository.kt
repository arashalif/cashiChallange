package com.arshalif.cashi.features.payment.domain.repository

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.model.Payment

interface PaymentRepository {
    suspend fun sendPayment(payment: Payment): NetworkResult<Payment>
} 