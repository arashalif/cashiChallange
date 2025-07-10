package com.arshalif.cashi


import com.arshalif.cashi.presentation.screen.payment.PaymentScreenTest
import com.arshalif.cashi.presentation.screen.transaction.TransactionScreenTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainActivityTest::class,
    PaymentScreenTest::class,
    TransactionScreenTest::class
)
class ComposeAppTestSuite 