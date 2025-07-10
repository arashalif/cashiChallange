package com.arshalif.cashi.presentation.screen.transaction

import app.cash.turbine.test
import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import com.arshalif.cashi.features.transaction.presentation.event.TransactionEvent
import com.arshalif.cashi.features.transaction.presentation.state.TransactionUiState
import com.arshalif.cashi.features.payment.domain.model.Currency
import io.mockk.coEvery
import io.mockk.coVerify
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {
    
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var viewModel: TransactionViewModel
    
    private val testDispatcher = StandardTestDispatcher()
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getTransactionsUseCase = mockk()
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be correct and load transactions`() = runTest {
        val testTransactions = listOf(
            Transaction(
                id = "1",
                recipientEmail = "test@example.com",
                amount = 100.0,
                currency = Currency.USD,
                timestamp = Instant.fromEpochMilliseconds(1234567890L)
            )
        )
        
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(testTransactions)
        
        // Create ViewModel - should automatically load transactions
        viewModel = TransactionViewModel(getTransactionsUseCase)
        
        viewModel.uiState.test {
            assertEquals(TransactionUiState.Initial, awaitItem())
            assertEquals(TransactionUiState.Loading, awaitItem())
            
            advanceUntilIdle()
            
            val successState = awaitItem() as TransactionUiState.Success
            assertEquals(testTransactions, successState.transactions)
        }
        
        coVerify { getTransactionsUseCase() }
    }
    
    @Test
    fun `loadTransactions should handle success result`() = runTest {
        val testTransactions = listOf(
            Transaction(
                id = "1",
                recipientEmail = "test1@example.com",
                amount = 100.0,
                currency = Currency.USD,
                timestamp = Instant.fromEpochMilliseconds(1234567890L)
            ),
            Transaction(
                id = "2",
                recipientEmail = "test2@example.com",
                amount = 250.50,
                currency = Currency.EUR,
                timestamp = Instant.fromEpochMilliseconds(1234567890L)
            )
        )
        
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(testTransactions)
        
        viewModel = TransactionViewModel(getTransactionsUseCase)
        
        viewModel.uiState.test {
            awaitItem() // initial
            awaitItem() // loading
            advanceUntilIdle()
            
            val successState = awaitItem() as TransactionUiState.Success
            assertEquals(2, successState.transactions.size)
            assertEquals("test1@example.com", successState.transactions[0].recipientEmail)
            assertEquals("test2@example.com", successState.transactions[1].recipientEmail)
        }
    }
    
    @Test
    fun `loadTransactions should handle error result`() = runTest {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Error("Network error")
        
        viewModel = TransactionViewModel(getTransactionsUseCase)
        
        viewModel.uiState.test {
            awaitItem() // initial
            awaitItem() // loading
            advanceUntilIdle()
            
            val errorState = awaitItem() as TransactionUiState.Error
            assertEquals("Network error", errorState.message)
        }
    }
    
    @Test
    fun `loadTransactions should handle exception`() = runTest {
        coEvery { getTransactionsUseCase() } throws RuntimeException("Unexpected error")
        
        viewModel = TransactionViewModel(getTransactionsUseCase)
        
        viewModel.uiState.test {
            awaitItem() // initial
            awaitItem() // loading
            advanceUntilIdle()
            
            val errorState = awaitItem() as TransactionUiState.Error
            assertEquals("Unexpected error", errorState.message)
        }
    }
    
    @Test
    fun `handleEvent LoadTransactions should reload transactions`() = runTest {
        val initialTransactions = listOf(
            Transaction(
                id = "1",
                recipientEmail = "test@example.com",
                amount = 100.0,
                currency = Currency.USD,
                timestamp = Instant.fromEpochMilliseconds(1234567890L)
            )
        )
        
        val newTransactions = listOf(
            Transaction(
                id = "2",
                recipientEmail = "new@example.com",
                amount = 200.0,
                currency = Currency.EUR,
                timestamp = Instant.fromEpochMilliseconds(1234567890L)
            )
        )
        
        coEvery { getTransactionsUseCase() } returnsMany listOf(
            NetworkResult.Success(initialTransactions),
            NetworkResult.Success(newTransactions)
        )
        
        viewModel = TransactionViewModel(getTransactionsUseCase)
        advanceUntilIdle()
        
        viewModel.uiState.test {
            // Skip to current state
            val currentState = awaitItem() as TransactionUiState.Success
            assertEquals(1, currentState.transactions.size)
            
            // Trigger reload
            viewModel.handleEvent(TransactionEvent.LoadTransactions)
            
            assertEquals(TransactionUiState.Loading, awaitItem())
            advanceUntilIdle()
            
            val newState = awaitItem() as TransactionUiState.Success
            assertEquals(1, newState.transactions.size)
            assertEquals("new@example.com", newState.transactions[0].recipientEmail)
        }
        
        coVerify(exactly = 2) { getTransactionsUseCase() }
    }
    
    @Test
    fun `handleEvent RefreshTransactions should reload transactions`() = runTest {
        val transactions = listOf(
            Transaction(
                id = "1",
                recipientEmail = "test@example.com",
                amount = 100.0,
                currency = Currency.USD,
                timestamp = Instant.fromEpochMilliseconds(1234567890L)
            )
        )
        
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(transactions)
        
        viewModel = TransactionViewModel(getTransactionsUseCase)
        advanceUntilIdle()
        
        viewModel.uiState.test {
            awaitItem() // current state
            
            // Trigger refresh
            viewModel.handleEvent(TransactionEvent.RefreshTransactions)
            
            assertEquals(TransactionUiState.Loading, awaitItem())
            advanceUntilIdle()
            
            val refreshedState = awaitItem() as TransactionUiState.Success
            assertEquals(transactions, refreshedState.transactions)
        }
        
        coVerify(exactly = 2) { getTransactionsUseCase() }
    }
    
    @Test
    fun `handleEvent ResetState should reset ui state to initial`() = runTest {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(emptyList())
        
        viewModel = TransactionViewModel(getTransactionsUseCase)
        advanceUntilIdle()
        
        viewModel.uiState.test {
            awaitItem() // current state
            
            viewModel.handleEvent(TransactionEvent.ResetState)
            assertEquals(TransactionUiState.Initial, awaitItem())
        }
    }
    
    @Test
    fun `loading state should be set during transaction loading`() = runTest {
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(emptyList())
        
        viewModel = TransactionViewModel(getTransactionsUseCase)
        
        viewModel.uiState.test {
            assertEquals(TransactionUiState.Initial, awaitItem())
            assertEquals(TransactionUiState.Loading, awaitItem())
            
            advanceUntilIdle()
            
            val finalState = awaitItem()
            assertTrue(finalState is TransactionUiState.Success)
        }
    }
    
    @Test
    fun `multiple refresh calls should handle properly`() = runTest {
        val transactions = listOf(
            Transaction(
                id = "1",
                recipientEmail = "test@example.com",
                amount = 100.0,
                currency = Currency.USD,
                timestamp = Instant.fromEpochMilliseconds(1234567890L)
            )
        )
        
        coEvery { getTransactionsUseCase() } returns NetworkResult.Success(transactions)
        
        viewModel = TransactionViewModel(getTransactionsUseCase)
        advanceUntilIdle()
        
        // Trigger multiple refreshes
        viewModel.handleEvent(TransactionEvent.RefreshTransactions)
        viewModel.handleEvent(TransactionEvent.RefreshTransactions)
        viewModel.handleEvent(TransactionEvent.LoadTransactions)
        
        advanceUntilIdle()
        
        viewModel.uiState.test {
            val finalState = awaitItem() as TransactionUiState.Success
            assertEquals(transactions, finalState.transactions)
        }
        
        coVerify(atLeast = 4) { getTransactionsUseCase() }
    }
} 