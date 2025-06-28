package com.arshalif.cashi.presentation.screen.payment

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import com.arshalif.cashi.features.payment.domain.model.Currency
import com.arshalif.cashi.features.payment.domain.model.Payment
import com.arshalif.cashi.features.payment.presentation.event.PaymentEvent
import com.arshalif.cashi.features.payment.presentation.state.PaymentUiState
import com.arshalif.cashi.features.payment.presentation.state.PaymentFormState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onNavigateToTransactions: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.handleEvent(PaymentEvent.ResetState)
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(
                top = WindowInsets.systemBars
                    .only(WindowInsetsSides.Top)
                    .asPaddingValues()
                    .calculateTopPadding()
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "Send Payment",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        when (uiState) {
            is PaymentUiState.Initial -> {
                PaymentForm(
                    formState = formState,
                    onEmailChange = { email ->
                        viewModel.handleEvent(PaymentEvent.EmailChanged(email))
                    },
                    onAmountChange = { amount ->
                        viewModel.handleEvent(PaymentEvent.AmountChanged(amount))
                    },
                    onCurrencyChange = { currency ->
                        viewModel.handleEvent(PaymentEvent.CurrencyChanged(currency))
                    },
                    onSendPayment = {
                        viewModel.handleEvent(PaymentEvent.SendPayment)
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // View Transactions Button
                Button(
                    onClick = onNavigateToTransactions,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("View Transaction History")
                }
            }
            
            is PaymentUiState.Loading -> {
                LoadingState()
            }
            
            is PaymentUiState.Success -> {
                SuccessState(
                    payment = (uiState as PaymentUiState.Success).payment,
                    onSendAnother = {
                        viewModel.handleEvent(PaymentEvent.ResetState)
                    },
                    onViewTransactions = onNavigateToTransactions
                )
            }
            
            is PaymentUiState.Error -> {
                ErrorState(
                    message = (uiState as PaymentUiState.Error).message,
                    onRetry = {
                        viewModel.handleEvent(PaymentEvent.ResetState)
                    }
                )
            }
        }
    }
}

@Composable
private fun PaymentForm(
    formState: PaymentFormState,
    onEmailChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (Currency) -> Unit,
    onSendPayment: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Email Field
            OutlinedTextField(
                value = formState.recipientEmail,
                onValueChange = onEmailChange,
                label = { Text("Recipient Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = !formState.isEmailValid && formState.recipientEmail.isNotBlank(),
                supportingText = {
                    if (!formState.isEmailValid && formState.recipientEmail.isNotBlank()) {
                        Text("Please enter a valid email address")
                    }
                }
            )
            
            // Amount Field
            OutlinedTextField(
                value = formState.amount,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = !formState.isAmountValid && formState.amount.isNotBlank(),
                supportingText = {
                    if (!formState.isAmountValid && formState.amount.isNotBlank()) {
                        Text("Please enter a valid amount")
                    }
                }
            )
            
            // Currency Selection
            CurrencySelector(
                selectedCurrency = formState.selectedCurrency,
                onCurrencySelected = onCurrencyChange
            )
            
            // Send Button
            Button(
                onClick = onSendPayment,
                modifier = Modifier.fillMaxWidth(),
                enabled = formState.isValid && !formState.isLoading
            ) {
                if (formState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Send Payment")
            }
        }
    }
}

@Composable
private fun CurrencySelector(
    selectedCurrency: Currency,
    onCurrencySelected: (Currency) -> Unit
) {
    Column {
        Text(
            text = "Currency",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Currency.values().forEach { currency ->
                FilterChip(
                    onClick = { onCurrencySelected(currency) },
                    label = { Text("${currency.symbol} ${currency.code}") },
                    selected = currency == selectedCurrency,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Processing payment...")
    }
}

@Composable
private fun SuccessState(
    payment: Payment,
    onSendAnother: () -> Unit,
    onViewTransactions: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Payment Sent Successfully!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Amount: ${payment.currency.symbol}${payment.amount}",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = "To: ${payment.recipientEmail}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSendAnother,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Send Another")
                }
                
                OutlinedButton(
                    onClick = onViewTransactions,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View Transactions")
                }
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Payment Failed",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Try Again")
            }
        }
    }
} 