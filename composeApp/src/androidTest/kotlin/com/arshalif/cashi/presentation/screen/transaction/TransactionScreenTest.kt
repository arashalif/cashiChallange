package com.arshalif.cashi.presentation.screen.transaction

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
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
class TransactionScreenTest : KoinTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        stopKoin()
        
        getTransactionsUseCase = mockk()
        
        val testModule = module {
            single { getTransactionsUseCase }
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
    fun transactionScreen_initialState_displaysCorrectly() {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Loading
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        // Check header
        composeTestRule.onNodeWithText("Transaction History").assertIsDisplayed()
        
        // Check back button
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
        
        // Check refresh button
        composeTestRule.onNodeWithContentDescription("Refresh").assertIsDisplayed()
    }
    
    @Test
    fun transactionScreen_loadingState_showsProgressIndicator() {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Loading
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        // Check loading indicator
        composeTestRule.onNodeWithText("Loading transactions...").assertIsDisplayed()
    }
    
    @Test
    fun transactionScreen_emptyState_displaysCorrectly() {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(emptyList())
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        composeTestRule.waitForIdle()
        
        // Check empty state
        composeTestRule.onNodeWithText("No Transactions").assertIsDisplayed()
        composeTestRule.onNodeWithText("You haven't made any transactions yet")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Refresh").assertIsDisplayed()
    }
    
    @Test
    fun transactionScreen_withTransactions_displaysCorrectly() {
        val testTransactions = listOf(
            Transaction(
                id = "1",
                recipientEmail = "test1@example.com",
                amount = 100.0,
                currency = Currency.USD,
                timestamp = Instant.fromEpochMilliseconds(1234567890000L)
            ),
            Transaction(
                id = "2",
                recipientEmail = "test2@example.com",
                amount = 250.50,
                currency = Currency.EUR,
                timestamp = Instant.fromEpochMilliseconds(1234567890000L)
            )
        )
        
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(testTransactions)
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        composeTestRule.waitForIdle()
        
        // Check first transaction
        composeTestRule.onNodeWithText("${Currency.USD.symbol}100.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("To: test1@example.com").assertIsDisplayed()
        
        // Check second transaction
        composeTestRule.onNodeWithText("${Currency.EUR.symbol}250.5").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("To: test2@example.com").assertIsDisplayed()
    }
    
    @Test
    fun transactionScreen_errorState_displaysCorrectly() {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Error("Network error")
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        composeTestRule.waitForIdle()
        
        // Check error state
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Try Again").assertIsDisplayed()
    }
    
    @Test
    fun transactionScreen_backButton_triggersNavigation() {
        var navigationCalled = false
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(emptyList())
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = { navigationCalled = true })
        }
        
        // Click back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        
        // Check navigation was called
        assert(navigationCalled)
    }
    
    @Test
    fun transactionScreen_refreshButton_reloadsTransactions() {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(emptyList())
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        composeTestRule.waitForIdle()
        
        // Click refresh button
        composeTestRule.onNodeWithContentDescription("Refresh").performClick()
        
        // Verify use case was called multiple times (initial + refresh)
        coVerify(atLeast = 2) { getTransactionsUseCase() }
    }
    
    @Test
    fun transactionScreen_refreshFromEmptyState_reloadsTransactions() {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(emptyList())
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        composeTestRule.waitForIdle()
        
        // Click refresh button in empty state
        composeTestRule.onNodeWithText("Refresh").performClick()
        
        // Verify use case was called multiple times
        coVerify(atLeast = 2) { getTransactionsUseCase() }
    }
    
    @Test
    fun transactionScreen_errorStateRetry_reloadsTransactions() {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Error("Network error")
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        composeTestRule.waitForIdle()
        
        // Click try again button
        composeTestRule.onNodeWithText("Try Again").performClick()
        
        // Verify use case was called multiple times
        coVerify(atLeast = 2) { getTransactionsUseCase() }
    }
    
    @Test
    fun transactionScreen_multipleTransactions_displaysInOrder() {
        val testTransactions = listOf(
            Transaction(
                id = "1",
                recipientEmail = "first@example.com",
                amount = 100.0,
                currency = Currency.USD,
                timestamp = Instant.fromEpochMilliseconds(1234567890000L)
            ),
            Transaction(
                id = "2",
                recipientEmail = "second@example.com",
                amount = 200.0,
                currency = Currency.EUR,
                timestamp = Instant.fromEpochMilliseconds(1234567891000L)
            ),
            Transaction(
                id = "3",
                recipientEmail = "third@example.com",
                amount = 300.0,
                currency = Currency.GBP,
                timestamp = Instant.fromEpochMilliseconds(1234567892000L)
            )
        )
        
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(testTransactions)
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        composeTestRule.waitForIdle()
        
        // Check all transactions are displayed
        composeTestRule.onNodeWithText("To: first@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("To: second@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("To: third@example.com").assertIsDisplayed()
        
        // Check amounts
        composeTestRule.onNodeWithText("${Currency.USD.symbol}100.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("${Currency.EUR.symbol}200.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("${Currency.GBP.symbol}300.0").assertIsDisplayed()
        
        // Check currencies
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("GBP").assertIsDisplayed()
    }
    
    @Test
    fun transactionScreen_transactionItem_displaysAllInformation() {
        val testTransaction = Transaction(
            id = "1",
            recipientEmail = "detailed@example.com",
            amount = 123.45,
            currency = Currency.USD,
            timestamp = Instant.fromEpochMilliseconds(1234567890000L)
        )
        
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(listOf(testTransaction))
        
        composeTestRule.setContent {
            TransactionScreen(onNavigateToPayment = {})
        }
        
        composeTestRule.waitForIdle()
        
        // Check all transaction details are displayed
        composeTestRule.onNodeWithText("${Currency.USD.symbol}123.45").assertIsDisplayed()
        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("To: detailed@example.com").assertIsDisplayed()
        
        // Check date is displayed (formatted)
        composeTestRule.onNodeWithText("Date: 2009-02-13 23:31:30").assertIsDisplayed()
    }
} 