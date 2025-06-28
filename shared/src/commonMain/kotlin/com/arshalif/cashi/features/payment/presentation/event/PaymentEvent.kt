package com.arshalif.cashi.features.payment.presentation.event

import com.arshalif.cashi.features.payment.domain.model.Currency

sealed class PaymentEvent {
    data class EmailChanged(val email: String) : PaymentEvent()
    data class AmountChanged(val amount: String) : PaymentEvent()
    data class CurrencyChanged(val currency: Currency) : PaymentEvent()
    object SendPayment : PaymentEvent()
    object ResetState : PaymentEvent()
} 