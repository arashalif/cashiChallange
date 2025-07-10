package com.arshalif.cashi.presentation.screen.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arshalif.cashi.core.network.NetworkResult
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.domain.usecase.SendPaymentUseCase
import com.arshalif.cashi.features.payment.domain.validation.PaymentValidator
import com.arshalif.cashi.features.payment.presentation.event.PaymentEvent
import com.arshalif.cashi.features.payment.presentation.state.PaymentFormState
import com.arshalif.cashi.features.payment.presentation.state.PaymentUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import com.arshalif.cashi.features.payment.domain.validation.DefaultCurrencyValidator
import com.arshalif.cashi.features.payment.domain.validation.ValidationResult

class PaymentViewModel(
    private val sendPaymentUseCase: SendPaymentUseCase,
    private val paymentValidator: PaymentValidator
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Initial)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()
    
    private val _formState = MutableStateFlow(
        PaymentFormState(
            recipientEmail = "",
            amount = "",
            selectedCurrency = Currency.USD,
            isEmailValid = true,
            isAmountValid = true,
            isLoading = false
        )
    )
    val formState: StateFlow<PaymentFormState> = _formState.asStateFlow()
    
    fun handleEvent(event: PaymentEvent) {
        when (event) {
            is PaymentEvent.EmailChanged -> updateEmail(event.email)
            is PaymentEvent.AmountChanged -> updateAmount(event.amount)
            is PaymentEvent.CurrencyChanged -> updateCurrency(event.currency)
            is PaymentEvent.SendPayment -> sendPayment()
            is PaymentEvent.ResetState -> resetState()
        }
    }
    
    private fun updateEmail(email: String) {
        _formState.update { currentState ->
            val isValid = paymentValidator.isValidEmail(email)
            currentState.copy(
                recipientEmail = email,
                isEmailValid = email.isBlank() || isValid,
                emailErrorMessage = if (email.isNotBlank() && !isValid) "Please enter a valid email address" else null
            )
        }
    }
    
    private fun updateAmount(amount: String) {
        _formState.update { currentState ->
            val isValidFormat = amount.isNotBlank() && amount.toDoubleOrNull() != null
            val amountValue = amount.toDoubleOrNull() ?: 0.0
            
            val validationMessage = if (amount.isNotBlank() && isValidFormat) {
                val currencyValidator = DefaultCurrencyValidator()
                val validationResult = currencyValidator.validateAmount(amountValue, currentState.selectedCurrency)
                if (validationResult is ValidationResult.Invalid) {
                    validationResult.message
                } else null
            } else if (amount.isNotBlank() && !isValidFormat) {
                "Please enter a valid number"
            } else null
            
            val isValid = amount.isBlank() || (isValidFormat && validationMessage == null)
            
            currentState.copy(
                amount = amount,
                isAmountValid = isValid,
                amountErrorMessage = validationMessage
            )
        }
    }
    
    private fun updateCurrency(currency: Currency) {
        _formState.update { currentState ->
            val newState = currentState.copy(selectedCurrency = currency)
            
            // Re-validate amount with new currency
            if (newState.amount.isNotBlank()) {
                val amountValue = newState.amount.toDoubleOrNull()
                if (amountValue != null) {
                    val currencyValidator = DefaultCurrencyValidator()
                    val validationResult = currencyValidator.validateAmount(amountValue, currency)
                    newState.copy(
                        isAmountValid = validationResult is ValidationResult.Valid,
                        amountErrorMessage = if (validationResult is ValidationResult.Invalid) validationResult.message else null
                    )
                } else {
                    newState
                }
            } else {
                newState
            }
        }
    }
    
    private fun sendPayment() {
        val currentFormState = _formState.value
        
        if (!currentFormState.isValid) {
            _uiState.value = PaymentUiState.Error("Please fill all fields correctly")
            return
        }
        
        val amount = currentFormState.amount.toDoubleOrNull()
        if (amount == null) {
            _uiState.value = PaymentUiState.Error("Invalid amount")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = PaymentUiState.Loading
            _formState.update { it.copy(isLoading = true) }
            
            try {
                val payment = Payment(
                    recipientEmail = currentFormState.recipientEmail,
                    amount = amount,
                    currency = currentFormState.selectedCurrency,
                    timestamp = Clock.System.now()
                )
                
                val result = sendPaymentUseCase(payment)
                when (result) {
                    is NetworkResult.Success -> {
                        _uiState.value = PaymentUiState.Success(result.data)
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = PaymentUiState.Error(result.message ?: "Payment failed")
                    }
                    is NetworkResult.Loading -> {
                        _uiState.value = PaymentUiState.Loading
                    }
                }
            } catch (e: Exception) {
                _uiState.value = PaymentUiState.Error(e.message ?: "Payment failed")
            } finally {
                _formState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    private fun resetState() {
        _uiState.value = PaymentUiState.Initial
        _formState.value = PaymentFormState(
            recipientEmail = "",
            amount = "",
            selectedCurrency = Currency.USD,
            isEmailValid = true,
            isAmountValid = true,
            emailErrorMessage = null,
            amountErrorMessage = null,
            isLoading = false
        )
    }
} 