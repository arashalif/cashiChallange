package com.arshalif.cashi.presentation.screen.payment

import app.cash.turbine.test
import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.payment.presentation.event.PaymentEvent
import com.arshalif.cashi.features.payment.presentation.state.PaymentUiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PaymentViewModelTest {
    
    private lateinit var sendPaymentUseCase: SendPaymentUseCase
    private lateinit var paymentValidator: PaymentValidator
    private lateinit var viewModel: PaymentViewModel
    
    private val testDispatcher = StandardTestDispatcher()
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        sendPaymentUseCase = mockk()
        paymentValidator = mockk()
        
        // Setup default mock behaviors
        every { paymentValidator.isValidEmail(any()) } returns true
        
        viewModel = PaymentViewModel(sendPaymentUseCase, paymentValidator)
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be correct`() = runTest {
        viewModel.uiState.test {
            assertEquals(PaymentUiState.Initial, awaitItem())
        }
        
        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("", state.recipientEmail)
            assertEquals("", state.amount)
            assertEquals(Currency.USD, state.selectedCurrency)
            assertTrue(state.isEmailValid)
            assertTrue(state.isAmountValid)
            assertFalse(state.isLoading)
        }
    }
    
    @Test
    fun `handleEvent EmailChanged should update email and validate`() = runTest {
        every { paymentValidator.isValidEmail("test@example.com") } returns true
        every { paymentValidator.isValidEmail("invalid-email") } returns false
        
        viewModel.formState.test {
            awaitItem() // initial state
            
            // Valid email
            viewModel.handleEvent(PaymentEvent.EmailChanged("test@example.com"))
            val validState = awaitItem()
            assertEquals("test@example.com", validState.recipientEmail)
            assertTrue(validState.isEmailValid)
            assertNull(validState.emailErrorMessage)
            
            // Invalid email
            viewModel.handleEvent(PaymentEvent.EmailChanged("invalid-email"))
            val invalidState = awaitItem()
            assertEquals("invalid-email", invalidState.recipientEmail)
            assertFalse(invalidState.isEmailValid)
            assertNotNull(invalidState.emailErrorMessage)
        }
    }
    
    @Test
    fun `handleEvent AmountChanged should update amount and validate`() = runTest {
        viewModel.formState.test {
            awaitItem() // initial state
            
            // Valid amount
            viewModel.handleEvent(PaymentEvent.AmountChanged("100.50"))
            val validState = awaitItem()
            assertEquals("100.50", validState.amount)
            assertTrue(validState.isAmountValid)
            assertNull(validState.amountErrorMessage)
            
            // Invalid amount (non-numeric)
            viewModel.handleEvent(PaymentEvent.AmountChanged("invalid"))
            val invalidState = awaitItem()
            assertEquals("invalid", invalidState.amount)
            assertFalse(invalidState.isAmountValid)
            assertEquals("Please enter a valid number", invalidState.amountErrorMessage)
        }
    }
    
    @Test
    fun `handleEvent CurrencyChanged should update currency and revalidate amount`() = runTest {
        viewModel.formState.test {
            awaitItem() // initial state
            
            // Set amount first
            viewModel.handleEvent(PaymentEvent.AmountChanged("100.00"))
            awaitItem()
            
            // Change currency
            viewModel.handleEvent(PaymentEvent.CurrencyChanged(Currency.EUR))
            val state = awaitItem()
            assertEquals(Currency.EUR, state.selectedCurrency)
            assertEquals("100.00", state.amount)
        }
    }
    
    @Test
    fun `sendPayment should work with valid form data`() = runTest {
        val testPayment = Payment(
            recipientEmail = "test@example.com",
            amount = 100.0,
            currency = Currency.USD,
            timestamp = Instant.fromEpochMilliseconds(1234567890L)
        )
        
        coEvery { sendPaymentUseCase(any()) } returns NetworkResult.Success(testPayment)
        
        // Setup valid form
        viewModel.handleEvent(PaymentEvent.EmailChanged("test@example.com"))
        viewModel.handleEvent(PaymentEvent.AmountChanged("100.00"))
        
        advanceUntilIdle()
        
        viewModel.uiState.test {
            awaitItem() // initial state
            
            viewModel.handleEvent(PaymentEvent.SendPayment)
            
            assertEquals(PaymentUiState.Loading, awaitItem())
            val successState = awaitItem() as PaymentUiState.Success
            assertEquals(testPayment, successState.payment)
        }
    }
    
    @Test
    fun `sendPayment should show error with invalid form data`() = runTest {
        every { paymentValidator.isValidEmail("") } returns false
        
        viewModel.uiState.test {
            awaitItem() // initial state
            
            viewModel.handleEvent(PaymentEvent.SendPayment)
            
            val errorState = awaitItem() as PaymentUiState.Error
            assertEquals("Please fill all fields correctly", errorState.message)
        }
    }
    
    @Test
    fun `sendPayment should handle network error`() = runTest {
        coEvery { sendPaymentUseCase(any()) } returns NetworkResult.Error("Network error")
        
        // Setup valid form
        viewModel.handleEvent(PaymentEvent.EmailChanged("test@example.com"))
        viewModel.handleEvent(PaymentEvent.AmountChanged("100.00"))
        
        advanceUntilIdle()
        
        viewModel.uiState.test {
            awaitItem() // initial state
            
            viewModel.handleEvent(PaymentEvent.SendPayment)
            
            assertEquals(PaymentUiState.Loading, awaitItem())
            val errorState = awaitItem() as PaymentUiState.Error
            assertEquals("Network error", errorState.message)
        }
    }
    
    @Test
    fun `sendPayment should handle exception`() = runTest {
        coEvery { sendPaymentUseCase(any()) } throws RuntimeException("Unexpected error")
        
        // Setup valid form
        viewModel.handleEvent(PaymentEvent.EmailChanged("test@example.com"))
        viewModel.handleEvent(PaymentEvent.AmountChanged("100.00"))
        
        advanceUntilIdle()
        
        viewModel.uiState.test {
            awaitItem() // initial state
            
            viewModel.handleEvent(PaymentEvent.SendPayment)
            
            assertEquals(PaymentUiState.Loading, awaitItem())
            val errorState = awaitItem() as PaymentUiState.Error
            assertEquals("Unexpected error", errorState.message)
        }
    }
    
    @Test
    fun `resetState should reset both ui and form state`() = runTest {
        // Setup some state first
        viewModel.handleEvent(PaymentEvent.EmailChanged("test@example.com"))
        viewModel.handleEvent(PaymentEvent.AmountChanged("100.00"))
        
        advanceUntilIdle()
        
        // Reset state
        viewModel.handleEvent(PaymentEvent.ResetState)
        
        viewModel.uiState.test {
            assertEquals(PaymentUiState.Initial, awaitItem())
        }
        
        viewModel.formState.test {
            val state = awaitItem()
            assertEquals("", state.recipientEmail)
            assertEquals("", state.amount)
            assertEquals(Currency.USD, state.selectedCurrency)
            assertTrue(state.isEmailValid)
            assertTrue(state.isAmountValid)
            assertFalse(state.isLoading)
            assertNull(state.emailErrorMessage)
            assertNull(state.amountErrorMessage)
        }
    }
    
    @Test
    fun `form state isValid should return true only when all fields are valid`() = runTest {
        every { paymentValidator.isValidEmail("test@example.com") } returns true
        
        viewModel.formState.test {
            // Initial state - invalid (empty fields)
            val initialState = awaitItem()
            assertFalse(initialState.isValid)
            
            // Add valid email - still invalid (no amount)
            viewModel.handleEvent(PaymentEvent.EmailChanged("test@example.com"))
            val emailState = awaitItem()
            assertFalse(emailState.isValid)
            
            // Add valid amount - should be valid now
            viewModel.handleEvent(PaymentEvent.AmountChanged("100.00"))
            val validState = awaitItem()
            assertTrue(validState.isValid)
        }
    }

} 