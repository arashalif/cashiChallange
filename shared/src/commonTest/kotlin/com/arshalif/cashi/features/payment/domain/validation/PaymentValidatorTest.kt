package com.arshalif.cashi.features.payment.domain.validation

import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaymentValidatorTest {
    private val validator = PaymentValidator()
    private val now = Clock.System.now()

    @Test
    fun `valid email format passes validation`() {
        assertTrue(validator.isValidEmail("test@example.com"))
        assertTrue(validator.isValidEmail("user.name+tag@domain.co.uk"))
    }

    @Test
    fun `invalid email format fails validation`() {
        assertFalse(validator.isValidEmail("invalid-email"))
        assertFalse(validator.isValidEmail("@domain.com"))
        assertFalse(validator.isValidEmail("user@"))
    }

    @Test
    fun `valid payment with USD passes complete validation`() {
        val payment = Payment("test@example.com", 10.0, Currency.USD, now)
        val result = validator.validatePayment(payment)
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `valid payment with EUR passes complete validation`() {
        val payment = Payment("test@example.com", 50.0, Currency.EUR, now)
        val result = validator.validatePayment(payment)
        assertTrue(result is ValidationResult.Valid)
    }

    @Test
    fun `payment with invalid email fails validation`() {
        val payment = Payment("invalid-email", 10.0, Currency.USD, now)
        val result = validator.validatePayment(payment)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("Invalid email format"))
    }

    @Test
    fun `payment with amount below minimum fails validation`() {
        val payment = Payment("test@example.com", 0.005, Currency.USD, now)
        val result = validator.validatePayment(payment)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("Amount must be at least"))
    }

    @Test
    fun `payment with amount above maximum fails validation`() {
        val payment = Payment("test@example.com", 15000.0, Currency.USD, now)
        val result = validator.validatePayment(payment)
        assertTrue(result is ValidationResult.Invalid)
        assertTrue((result as ValidationResult.Invalid).message.contains("Amount exceeds maximum allowed"))
    }
} 