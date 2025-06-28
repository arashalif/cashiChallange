package com.arshalif.cashi.presentation.screen.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arshalif.cashi.core.utils.DateTimeUtils.formatDate
import com.arshalif.cashi.features.transaction.domain.model.Transaction
import com.arshalif.cashi.features.transaction.presentation.event.TransactionEvent
import com.arshalif.cashi.features.transaction.presentation.state.TransactionUiState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    onNavigateToPayment: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.handleEvent(TransactionEvent.LoadTransactions)
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Transaction History") },
            navigationIcon = {
                IconButton(onClick = onNavigateToPayment) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        viewModel.handleEvent(TransactionEvent.RefreshTransactions)
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        )
        
        // Content
        when (uiState) {
            is TransactionUiState.Initial -> {
                // Show loading initially
                LoadingState()
            }
            
            is TransactionUiState.Loading -> {
                LoadingState()
            }
            
            is TransactionUiState.Success -> {
                TransactionList(
                    transactions = (uiState as TransactionUiState.Success).transactions,
                    onRefresh = {
                        viewModel.handleEvent(TransactionEvent.RefreshTransactions)
                    }
                )
            }
            
            is TransactionUiState.Error -> {
                ErrorState(
                    message = (uiState as TransactionUiState.Error).message,
                    onRetry = {
                        viewModel.handleEvent(TransactionEvent.LoadTransactions)
                    }
                )
            }
        }
    }
}

@Composable
private fun TransactionList(
    transactions: List<Transaction>,
    onRefresh: () -> Unit
) {
    if (transactions.isEmpty()) {
        EmptyState(onRefresh = onRefresh)
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${transaction.currency.symbol}${transaction.amount}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = transaction.currency.code,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Transaction details
            Text(
                text = "To: ${transaction.recipientEmail}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Date: ${formatDate(transaction.timestamp)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading transactions...")
        }
    }
}

@Composable
private fun EmptyState(
    onRefresh: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No Transactions",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "You haven't made any transactions yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error Loading Transactions",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
} 