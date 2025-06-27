package com.arshalif.cashi.features.payment.domain.usecase

import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.repository.PaymentRepository
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.payment.domain.validation.ValidationResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class SendPaymentUseCaseTest {
    private val now = Clock.System.now()
    
    @Test
    fun `invoke with valid payment calls repository`() = runTest {
        val validPayment = Payment("test@example.com", 10.0, Currency.USD, now)
        val mockRepository = object : PaymentRepository {
            override suspend fun sendPayment(payment: Payment): NetworkResult<Payment> {
                return NetworkResult.Success(payment)
            }
        }
        
        val mockValidator = object : PaymentValidator() {
            override fun validatePayment(payment: Payment): ValidationResult {
                return ValidationResult.Valid
            }
        }
        
        val useCase = SendPaymentUseCase(mockRepository, mockValidator)
        val result = useCase(validPayment)
        
        assertTrue(result is NetworkResult.Success)
    }
    
    @Test
    fun `invoke with invalid payment returns failure`() = runTest {
        val invalidPayment = Payment("invalid-email", 10.0, Currency.USD, now)
        val mockRepository = object : PaymentRepository {
            override suspend fun sendPayment(payment: Payment): NetworkResult<Payment> {
                fail("Should not be called")
            }
        }
        
        val mockValidator = object : PaymentValidator() {
            override fun validatePayment(payment: Payment): ValidationResult {
                return ValidationResult.Invalid("Invalid email format")
            }
        }
        
        val useCase = SendPaymentUseCase(mockRepository, mockValidator)
        val result = useCase(invalidPayment)
        
        assertTrue(result is NetworkResult.Error)
        assertTrue((result as NetworkResult.Error).message.contains("Invalid email format"))
    }
} 