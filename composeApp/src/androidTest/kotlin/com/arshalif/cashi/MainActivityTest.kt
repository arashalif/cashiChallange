package com.arshalif.cashi

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
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
class MainActivityTest : KoinTest {
    
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
    fun app_launches_displaysPaymentScreen() {
        composeTestRule.setContent {
            App()
        }
        
        // Check that the app displays the payment screen
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("View Transaction History").assertIsDisplayed()
        // Check that both "Send Payment" texts exist (header and button)
        composeTestRule.onAllNodesWithText("Send Payment").assertCountEquals(2)
    }
    
    @Test 
    fun koin_dependency_injection_isSetupCorrectly() {
        composeTestRule.setContent {
            App()
        }
        
        // Verify that DI is working by checking UI elements that depend on ViewModels
        composeTestRule.onNodeWithText("Recipient Email").assertIsDisplayed()
    }
    
    @Test
    fun navigation_components_areInitialized() {
        composeTestRule.setContent {
            App()
        }
        
        // Verify navigation is working
        composeTestRule.onNodeWithText("View Transaction History").assertIsDisplayed()
    }
} 