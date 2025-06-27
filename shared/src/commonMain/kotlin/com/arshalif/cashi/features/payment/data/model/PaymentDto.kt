package com.arshalif.cashi.features.payment.data.model

import com.arshalif.cashi.features.payment.domain.model.Currency
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class PaymentDto(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val timestamp: String
)

fun PaymentDto.toDomain(): com.arshalif.cashi.features.payment.domain.model.Payment {
    return com.arshalif.cashi.features.payment.domain.model.Payment(
        recipientEmail = recipientEmail,
        amount = amount,
        currency = Currency.fromCode(currency) ?: Currency.USD,
        timestamp = Instant.parse(timestamp)
    )
} 