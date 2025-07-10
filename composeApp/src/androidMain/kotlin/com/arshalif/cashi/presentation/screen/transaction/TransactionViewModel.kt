package com.arshalif.cashi.presentation.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.transaction.domain.usecase.GetTransactionsUseCase
import com.arshalif.cashi.features.transaction.presentation.event.TransactionEvent
import com.arshalif.cashi.features.transaction.presentation.state.TransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Initial)
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()
    
    init {
        loadTransactions()
    }
    
    fun handleEvent(event: TransactionEvent) {
        when (event) {
            is TransactionEvent.LoadTransactions -> {
                loadTransactions()
            }
            is TransactionEvent.RefreshTransactions -> {
                loadTransactions()
            }
            is TransactionEvent.ResetState -> {
                _uiState.value = TransactionUiState.Initial
            }
        }
    }
    
    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading
            
            try {
                val result = getTransactionsUseCase()
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.value = TransactionUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = TransactionUiState.Error(result.message ?: "Failed to load transactions")
                    }
                    is NetworkResult.Loading -> {
                        _uiState.value = TransactionUiState.Loading
                    }
                }
            } catch (e: Exception) {
                _uiState.value = TransactionUiState.Error(e.message ?: "Failed to load transactions")
            }
        }
    }
} 
