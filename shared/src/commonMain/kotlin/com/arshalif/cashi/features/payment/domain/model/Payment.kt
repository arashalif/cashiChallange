package com.arshalif.cashi.features.payment.domain.model

import kotlinx.datetime.Instant

data class Payment(
    val recipientEmail: String,
    val amount: Double,
    val currency: Currency,
    val timestamp: Instant
) 