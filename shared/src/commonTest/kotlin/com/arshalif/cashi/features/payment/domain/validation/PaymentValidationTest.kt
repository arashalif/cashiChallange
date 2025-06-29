package com.arshalif.cashi.features.payment.domain.validation

import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PaymentValidationTest {
    
    private val paymentValidator = PaymentValidator()
    
    @Test
    fun `valid email should pass validation`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@test.com",
            "user_name@test-domain.com"
        )
        
        validEmails.forEach { email ->
            assertTrue(
                paymentValidator.isValidEmail(email),
                "Email '$email' should be valid"
            )
        }
    }
    
    @Test
    fun `invalid email should fail validation`() {
        val invalidEmails = listOf(
            "invalid-email",
            "@example.com",
            "user@",
            "user.example.com",
            "",
            "user@.com",
            "user@domain.",
            "user name@example.com"
        )
        
        invalidEmails.forEach { email ->
            assertFalse(
                paymentValidator.isValidEmail(email),
                "Email '$email' should be invalid"
            )
        }
    }
    
    @Test
    fun `valid payment should pass validation`() {
        val validPayment = Payment(
            recipientEmail = "test@example.com",
            amount = 100.50,
            currency = Currency.USD,
            timestamp = Clock.System.now()
        )
        
        val result = paymentValidator.validatePayment(validPayment)
        
        assertTrue(result is ValidationResult.Valid, "Valid payment should pass validation")
    }
    
    @Test
    fun `payment with invalid email should fail validation`() {
        val invalidPayment = Payment(
            recipientEmail = "invalid-email",
            amount = 100.50,
            currency = Currency.USD,
            timestamp = Clock.System.now()
        )
        
        val result = paymentValidator.validatePayment(invalidPayment)
        
        assertTrue(result is ValidationResult.Invalid, "Payment with invalid email should fail")
        assertEquals("Invalid email format", (result as ValidationResult.Invalid).message)
    }
    
    @Test
    fun `payment with zero amount should fail validation`() {
        val zeroAmountPayment = Payment(
            recipientEmail = "test@example.com",
            amount = 0.0,
            currency = Currency.USD,
            timestamp = Clock.System.now()
        )
        
        val result = paymentValidator.validatePayment(zeroAmountPayment)
        
        assertTrue(result is ValidationResult.Invalid, "Payment with zero amount should fail")
        assertTrue(
            (result as ValidationResult.Invalid).message.contains("Amount must be at least"),
            "Error message should mention minimum amount validation"
        )
    }
    
    @Test
    fun `payment with negative amount should fail validation`() {
        val negativeAmountPayment = Payment(
            recipientEmail = "test@example.com",
            amount = -50.0,
            currency = Currency.USD,
            timestamp = Clock.System.now()
        )
        
        val result = paymentValidator.validatePayment(negativeAmountPayment)
        
        assertTrue(result is ValidationResult.Invalid, "Payment with negative amount should fail")
        assertTrue(
            (result as ValidationResult.Invalid).message.contains("Amount must be at least"),
            "Error message should mention minimum amount validation"
        )
    }
    
    @Test
    fun `supported currencies should pass validation`() {
        val supportedCurrencies = listOf(Currency.USD, Currency.EUR, Currency.GBP)
        
        supportedCurrencies.forEach { currency ->
            val payment = Payment(
                recipientEmail = "test@example.com",
                amount = 100.0,
                currency = currency,
                timestamp = Clock.System.now()
            )
            
            val result = paymentValidator.validatePayment(payment)
            
            assertTrue(
                result is ValidationResult.Valid,
                "Payment with currency $currency should be valid"
            )
        }
    }
    
    @Test
    fun `edge case amounts should be handled correctly`() {
        val edgeCaseAmounts = mapOf(
            0.01 to true,  // Minimum valid amount
            0.001 to false, // Below minimum amount
            9999.99 to true, // Large but valid amount for USD
            15000.0 to false // Above maximum for USD (10,000)
        )
        
        edgeCaseAmounts.forEach { (amount, shouldBeValid) ->
            val payment = Payment(
                recipientEmail = "test@example.com",
                amount = amount,
                currency = Currency.USD,
                timestamp = Clock.System.now()
            )
            
            val result = paymentValidator.validatePayment(payment)
            
            if (shouldBeValid) {
                assertTrue(
                    result is ValidationResult.Valid,
                    "Payment with amount $amount should be valid"
                )
            } else {
                assertTrue(
                    result is ValidationResult.Invalid,
                    "Payment with amount $amount should be invalid"
                )
            }
        }
    }
} 