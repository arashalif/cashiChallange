package com.arshalif.cashi.presentation.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import com.arshalif.cashi.features.transaction.presentation.event.TransactionEvent
import com.arshalif.cashi.features.transaction.presentation.state.TransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Initial)
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    fun handleEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.LoadTransactions -> loadTransactions()
            is TransactionEvent.RefreshTransactions -> refreshTransactions()
            is TransactionEvent.ResetState -> resetState()
        }
    }
    
    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading
            
            try {
                getTransactionsUseCase()
                    .catch { error ->
                        _uiState.value = TransactionUiState.Error(error.message ?: "Failed to load transactions")
                    }
                    .collect { transactions ->
                        _uiState.value = TransactionUiState.Success(transactions)
                    }
            } catch (e: Exception) {
                _uiState.value = TransactionUiState.Error(e.message ?: "Failed to load transactions")
            }
        }
    }
    
    private fun refreshTransactions() {
        loadTransactions()
    }
    
    private fun resetState() {
        _uiState.value = TransactionUiState.Initial
    }
} 
