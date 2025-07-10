package com.arshalif.cashi.presentation.screen.payment

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Clock
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PaymentScreenTest : KoinTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var sendPaymentUseCase: SendPaymentUseCase
    private lateinit var paymentValidator: PaymentValidator
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        
        sendPaymentUseCase = mockk()
        paymentValidator = mockk()
        
        // Setup default mock behaviors
        every { paymentValidator.isValidEmail(any()) } returns true
        
        val testModule = module {
            single { sendPaymentUseCase }
            single { paymentValidator }
            single { PaymentViewModel(get(), get()) }
        }
        
        startKoin {
            modules(testModule)
        }
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }
    
    @Test
    fun paymentScreen_initialState_displaysCorrectly() {
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Check form fields
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amount (${Currency.USD.symbol})").assertIsDisplayed()
        composeTestRule.onNodeWithText("View Transaction History").assertIsDisplayed()
        
        // Check that both "Send Payment" texts exist (header and button)
        composeTestRule.onAllNodesWithText("Send Payment").assertCountEquals(2)
        
        // Send button should be disabled initially - use a more specific selector
        composeTestRule.onAllNodesWithText("Send Payment")[1].assertIsNotEnabled()
    }
    
    @Test
    fun paymentScreen_emailInput_updatesCorrectly() {
        every { paymentValidator.isValidEmail("test@example.com") } returns true
        
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Find email input and enter text
        composeTestRule.onNodeWithText("Recipient Email")
            .performTextInput("test@example.com")
        
        // Check that email is displayed
        composeTestRule.onNodeWithText("test@example.com").assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_amountInput_updatesCorrectly() {
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Find amount input and enter text
        composeTestRule.onNodeWithText("Amount (${Currency.USD.symbol})")
            .performTextInput("100.50")
        
        // Check that amount is displayed
        composeTestRule.onNodeWithText("100.50").assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_invalidEmail_showsError() {
        every { paymentValidator.isValidEmail("invalid-email") } returns false
        
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Enter invalid email
        composeTestRule.onNodeWithText("Recipient Email")
            .performTextInput("invalid-email")
        
        // Check error message appears
        composeTestRule.onNodeWithText("Please enter a valid email address")
            .assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_invalidAmount_showsError() {
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Enter invalid amount
        composeTestRule.onNodeWithText("Amount (${Currency.USD.symbol})")
            .performTextInput("invalid")
        
        // Check error message appears
        composeTestRule.onNodeWithText("Please enter a valid number")
            .assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_validForm_enablesSendButton() {
        every { paymentValidator.isValidEmail("test@example.com") } returns true
        
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Fill valid email
        composeTestRule.onNodeWithText("Recipient Email")
            .performTextInput("test@example.com")
        
        // Fill valid amount
        composeTestRule.onNodeWithText("Amount (${Currency.USD.symbol})")
            .performTextInput("100.00")
        
        // Send button should now be enabled - use a more specific selector
        composeTestRule.onAllNodesWithText("Send Payment")[1].assertIsEnabled()
    }
    
    @Test
    fun paymentScreen_currencySelection_displaysAllOptions() {
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Check all currency options are displayed
        composeTestRule.onNodeWithText("$ USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("€ EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("£ GBP").assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_currencyChange_updatesAmountLabel() {
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Select EUR currency
        composeTestRule.onNodeWithText("€ EUR").performClick()
        
        // Check amount label updates
        composeTestRule.onNodeWithText("Amount (${Currency.EUR.symbol})")
            .assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_successfulPayment_showsSuccessState() {
        val testPayment = Payment(
            recipientEmail = "test@example.com",
            amount = 100.0,
            currency = Currency.USD,
            timestamp = Clock.System.now()
        )
        
        every { paymentValidator.isValidEmail("test@example.com") } returns true
        coEvery { sendPaymentUseCase(any()) } returns NetworkResult.Success(testPayment)
        
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Fill form and submit
        composeTestRule.onNodeWithText("Recipient Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Amount (${Currency.USD.symbol})")
            .performTextInput("100.00")
        
        composeTestRule.onAllNodesWithText("Send Payment")[1].performClick()
        
        // Wait for success state
        composeTestRule.waitForIdle()
        
        // Check success message
        composeTestRule.onNodeWithText("Payment Sent Successfully!")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Send Another")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("View Transactions")
            .assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_paymentError_showsErrorState() {
        every { paymentValidator.isValidEmail("test@example.com") } returns true
        coEvery { sendPaymentUseCase(any()) } returns NetworkResult.Error("Payment failed")
        
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Fill form and submit
        composeTestRule.onNodeWithText("Recipient Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Amount (${Currency.USD.symbol})")
            .performTextInput("100.00")
        
        composeTestRule.onAllNodesWithText("Send Payment")[1].performClick()
        
        // Wait for error state
        composeTestRule.waitForIdle()
        
        // Check error message
        composeTestRule.onNodeWithText("Payment failed")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again")
            .assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_loadingState_showsProgressIndicator() {
        every { paymentValidator.isValidEmail("test@example.com") } returns true
        coEvery { sendPaymentUseCase(any()) } returns NetworkResult.Loading
        
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Fill form and submit
        composeTestRule.onNodeWithText("Recipient Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Amount (${Currency.USD.symbol})")
            .performTextInput("100.00")
        
        composeTestRule.onAllNodesWithText("Send Payment")[1].performClick()
        
        // Check loading indicator appears
        composeTestRule.onNodeWithText("Processing payment...")
            .assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_currencyLimitsInfo_displaysCorrectly() {
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Check USD limits are shown initially
        composeTestRule.onNodeWithText("Range: ${Currency.USD.symbol}0.01 - ${Currency.USD.symbol}10,000")
            .assertIsDisplayed()
        
        // Change to EUR
        composeTestRule.onNodeWithText("€ EUR").performClick()
        
        // Check EUR limits are shown
        composeTestRule.onNodeWithText("Range: ${Currency.EUR.symbol}0.01 - ${Currency.EUR.symbol}8,500")
            .assertIsDisplayed()
        
        // Change to GBP
        composeTestRule.onNodeWithText("£ GBP").performClick()
        
        // Check GBP limits are shown
        composeTestRule.onNodeWithText("Range: ${Currency.GBP.symbol}0.01 - ${Currency.GBP.symbol}8,000")
            .assertIsDisplayed()
    }
    
    @Test
    fun paymentScreen_navigationToTransactions_works() {
        var navigationCalled = false
        
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = { navigationCalled = true })
        }
        
        // Click view transactions button
        composeTestRule.onNodeWithText("View Transaction History").performClick()
        
        // Check navigation was called
        assert(navigationCalled)
    }
    
    @Test
    fun paymentScreen_resetAfterSuccess_returnsToInitialState() {
        val testPayment = Payment(
            recipientEmail = "test@example.com",
            amount = 100.0,
            currency = Currency.USD,
            timestamp = Clock.System.now()
        )
        
        every { paymentValidator.isValidEmail("test@example.com") } returns true
        coEvery { sendPaymentUseCase(any()) } returns NetworkResult.Success(testPayment)
        
        composeTestRule.setContent {
            PaymentScreen(onNavigateToTransactions = {})
        }
        
        // Fill form and submit
        composeTestRule.onNodeWithText("Recipient Email")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Amount (${Currency.USD.symbol})")
            .performTextInput("100.00")
        
        composeTestRule.onAllNodesWithText("Send Payment")[1].performClick()
        composeTestRule.waitForIdle()
        
        // Click send another payment
        composeTestRule.onNodeWithText("Send Another").performClick()
        
        // Check we're back to initial state
        composeTestRule.onAllNodesWithText("Send Payment").assertCountEquals(2)
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
    }
} 