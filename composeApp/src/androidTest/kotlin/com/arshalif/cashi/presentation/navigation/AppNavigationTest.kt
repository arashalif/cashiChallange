package com.arshalif.cashi.presentation.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import com.arshalif.cashi.presentation.screen.payment.PaymentViewModel
import com.arshalif.cashi.presentation.screen.transaction.TransactionViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
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
class AppNavigationTest : KoinTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var sendPaymentUseCase: SendPaymentUseCase
    private lateinit var paymentValidator: PaymentValidator
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        
        sendPaymentUseCase = mockk()
        paymentValidator = mockk()
        getTransactionsUseCase = mockk()
        
        // Setup default mock behaviors
        every { paymentValidator.isValidEmail(any()) } returns true
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(emptyList())
        
        val testModule = module {
            single { sendPaymentUseCase }
            single { paymentValidator }
            single { getTransactionsUseCase }
            single { PaymentViewModel(get(), get()) }
            single { TransactionViewModel(get()) }
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
    fun appNavigation_startsWithPaymentScreen() {
        composeTestRule.setContent {
            AppNavigation()
        }
        
        // Check that payment screen is displayed initially
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("View Transaction History").assertIsDisplayed()
    }
    
    @Test
    fun appNavigation_navigateToTransactionScreen() {
        composeTestRule.setContent {
            AppNavigation()
        }
        
        // Click on view transaction history
        composeTestRule.onNodeWithText("View Transaction History").performClick()
        
        composeTestRule.waitForIdle()
        
        // Check that transaction screen is displayed
        composeTestRule.onNodeWithText("Transaction History").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
    }
    
    @Test
    fun appNavigation_navigateBackToPaymentScreen() {
        composeTestRule.setContent {
            AppNavigation()
        }
        
        // Navigate to transaction screen
        composeTestRule.onNodeWithText("View Transaction History").performClick()
        composeTestRule.waitForIdle()
        
        // Navigate back to payment screen
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Check that payment screen is displayed again
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
    }
    
    @Test
    fun appNavigation_customNavController_worksCorrectly() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            AppNavigation(navController = navController)
        }
        
        // Check that payment screen is displayed initially
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
        
        // Navigate to transaction screen
        composeTestRule.onNodeWithText("View Transaction History").performClick()
        composeTestRule.waitForIdle()
        
        // Check that transaction screen is displayed
        composeTestRule.onNodeWithText("Transaction History").assertIsDisplayed()
        
        // Navigate back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Check that payment screen is displayed again
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
    }
    
    @Test
    fun appNavigation_multipleNavigations_workCorrectly() {
        composeTestRule.setContent {
            AppNavigation()
        }
        
        // Navigate back and forth multiple times
        repeat(3) {
            // Go to transactions
            composeTestRule.onNodeWithText("View Transaction History").performClick()
            composeTestRule.waitForIdle()
            composeTestRule.onNodeWithText("Transaction History").assertIsDisplayed()
            
            // Go back to payment
            composeTestRule.onNodeWithContentDescription("Back").performClick()
            composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
        }
    }
    
    @Test
    fun appNavigation_preservesScreenState() {
        composeTestRule.setContent {
            AppNavigation()
        }
        
        // Enter some text in payment screen
        composeTestRule.onNodeWithText("Recipient Email")
            .performClick()
        
        // Navigate to transaction screen and back
        composeTestRule.onNodeWithText("View Transaction History").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Check that we're back on payment screen
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
    }
} 