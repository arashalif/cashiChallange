package com.arshalif.cashi.features.transaction.domain.model

import com.arshalif.cashi.features.payment.domain.model.Currency
import kotlinx.datetime.Instant

data class Transaction(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: Currency,
    val timestamp: Instant,
)
