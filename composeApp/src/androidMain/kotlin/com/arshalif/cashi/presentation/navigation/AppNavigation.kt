package com.arshalif.cashi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arshalif.cashi.presentation.screen.payment.PaymentScreen
import com.arshalif.cashi.presentation.screen.transaction.TransactionScreen

sealed class Screen(val route: String) {
    object Payment : Screen("payment")
    object Transaction : Screen("transaction")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Payment.route
    ) {
        composable(Screen.Payment.route) {
            PaymentScreen(
                onNavigateToTransactions = {
                    navController.navigate(Screen.Transaction.route)
                }
            )
        }
        
        composable(Screen.Transaction.route) {
            TransactionScreen(
                onNavigateToPayment = {
                    navController.popBackStack()
                }
            )
        }
    }
} 