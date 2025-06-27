package com.arshalif.cashi.features.transaction.data.model

import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.domain.model.TransactionStatus
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val timestamp: String,
    val status: String,
    val transactionType: String
)

fun TransactionDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        recipientEmail = recipientEmail,
        amount = amount,
        currency = Currency.fromCode(currency) ?: Currency.USD,
        timestamp = Instant.parse(timestamp),
        status = TransactionStatus.valueOf(status.uppercase()),
        transactionType = transactionType
    )
} 