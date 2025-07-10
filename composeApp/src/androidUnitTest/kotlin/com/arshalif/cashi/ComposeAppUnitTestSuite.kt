package com.arshalif.cashi

import com.arshalif.cashi.presentation.screen.payment.PaymentViewModelTest
import com.arshalif.cashi.presentation.screen.transaction.TransactionViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    PaymentViewModelTest::class,
    TransactionViewModelTest::class
)
class ComposeAppUnitTestSuite 