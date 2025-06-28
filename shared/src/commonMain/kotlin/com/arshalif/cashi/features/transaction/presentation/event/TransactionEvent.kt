package com.arshalif.cashi.features.transaction.presentation.event

sealed class TransactionEvent {
    object LoadTransactions : TransactionEvent()
    object RefreshTransactions : TransactionEvent()
    object ResetState : TransactionEvent()
} 