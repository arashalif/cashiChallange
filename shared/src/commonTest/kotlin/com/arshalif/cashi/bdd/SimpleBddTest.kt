package com.arshalif.cashi.bdd

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import kotlin.test.assertEquals
import kotlinx.coroutines.runBlocking

/**
 * Simple BDD test to verify our step definitions work
 * without relying on Cucumber feature files
 */
class SimpleBddTest {
    
    private val stepDefinitions = PaymentStepDefinitions()
    
    @Test
    fun testValidPaymentFlow() = runBlocking {
        // Given
        stepDefinitions.thePaymentSystemIsAvailable()
        stepDefinitions.theTransactionHistoryIsAccessible()
        stepDefinitions.iHaveAValidEmail("john.doe@example.com")
        stepDefinitions.iHaveAValidAmountOf(100.50)
        stepDefinitions.iSelectCurrency("USD")
        
        // When
        stepDefinitions.iSubmitThePayment()
        
        // Then
        stepDefinitions.thePaymentShouldBeProcessedSuccessfully()
        stepDefinitions.iShouldSeeASuccessMessage()
        stepDefinitions.theTransactionShouldAppearInTheHistory()
    }
    
    @Test
    fun testInvalidEmailRejection() = runBlocking {
        // Given
        stepDefinitions.thePaymentSystemIsAvailable()
        stepDefinitions.iHaveAnInvalidEmail("invalid-email")
        stepDefinitions.iHaveAValidAmountOf(50.0)
        stepDefinitions.iSelectCurrency("USD")
        
        // When
        stepDefinitions.iSubmitThePayment()
        
        // Then
        stepDefinitions.thePaymentShouldBeRejected()
        stepDefinitions.iShouldSeeAnErrorMessage("Invalid email format")
    }
    
    @Test
    fun testNegativeAmountRejection() = runBlocking {
        // Given
        stepDefinitions.thePaymentSystemIsAvailable()
        stepDefinitions.iHaveAValidEmail("jane.smith@example.com")
        stepDefinitions.iHaveAnInvalidAmountOf(-10.0)
        stepDefinitions.iSelectCurrency("USD")
        
        // When
        stepDefinitions.iSubmitThePayment()
        
        // Then
        stepDefinitions.thePaymentShouldBeRejected()
        stepDefinitions.iShouldSeeAnErrorMessage("amount")
    }
    
    @Test
    fun testTransactionHistory() = runBlocking {
        // Given
        stepDefinitions.thereAreExistingTransactionsInTheSystem()
        
        // When
        stepDefinitions.iRequestTheTransactionHistory()
        
        // Then
        stepDefinitions.iShouldReceiveAListOfTransactions()
        stepDefinitions.eachTransactionShouldHaveAnEmailAmountAndCurrency()
        stepDefinitions.transactionsShouldBeSortedByDateDescending()
    }
    
    @Test
    fun testEmailValidationWithVariousFormats() {
        val testEmails = mapOf(
            "user@example.com" to true,
            "test.email@domain.co.uk" to true,
            "user+tag@example.org" to true,
            "user123@test-domain.com" to true,
            "invalid-email" to false,
            "@example.com" to false,
            "user@" to false,
            "user@.com" to false,
            "user space@example.com" to false  // Space in email should be invalid
        )
        
        val stepDefs = PaymentStepDefinitions()
        testEmails.forEach { (email, expectedValid) ->
            val actualValid = stepDefs.paymentValidator.isValidEmail(email)
            assertEquals(
                expectedValid, 
                actualValid,
                "Email validation failed for: $email. Expected: $expectedValid, Actual: $actualValid"
            )
        }
    }
} 