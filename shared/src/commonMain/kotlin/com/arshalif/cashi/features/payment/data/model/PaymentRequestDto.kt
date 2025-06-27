package com.arshalif.cashi.features.payment.data.model

import com.arshalif.cashi.features.payment.domain.model.Payment
import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequestDto(
    val recipientEmail: String,
    val amount: Double,
    val currency: String
)

fun Payment.toRequestDto(): PaymentRequestDto {
    return PaymentRequestDto(
        recipientEmail = recipientEmail,
        amount = amount,
        currency = currency.code
    )
} 