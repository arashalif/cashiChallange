# BDD (Behavior-Driven Development) Tests

This directory contains the BDD testing implementation for the CashiChallenge payment system.

## Overview

The BDD tests validate the payment processing flow from a user behavior perspective, ensuring that:
- Valid payments are processed successfully
- Invalid inputs are properly rejected with appropriate error messages
- Transaction history is maintained and accessible

## Files

### 1. `PaymentStepDefinitions.kt`
Contains Cucumber step definitions that implement the Given/When/Then steps:

**Given Steps:**
- `thePaymentSystemIsAvailable()` - Initializes system state
- `theTransactionHistoryIsAccessible()` - Prepares transaction access
- `iHaveAValidEmail(email)` - Sets valid email for payment
- `iHaveAnInvalidEmail(email)` - Sets invalid email for testing validation
- `iHaveAValidAmountOf(amount)` - Sets valid payment amount
- `iHaveAnInvalidAmountOf(amount)` - Sets invalid amount for testing
- `iSelectCurrency(currency)` - Sets payment currency

**When Steps:**
- `iSubmitThePayment()` - Processes the payment submission
- `iRequestTheTransactionHistory()` - Retrieves transaction history

**Then Steps:**
- `thePaymentShouldBeProcessedSuccessfully()` - Verifies successful payment
- `thePaymentShouldBeRejected()` - Verifies payment rejection
- `iShouldSeeASuccessMessage()` - Checks for success feedback
- `iShouldSeeAnErrorMessage(message)` - Validates error messages
- `theTransactionShouldAppearInTheHistory()` - Confirms transaction recording
- `iShouldReceiveAListOfTransactions()` - Validates transaction retrieval

### 2. `SimpleBddTest.kt`
Direct unit tests that use the step definitions without Cucumber feature files:

**Test Scenarios:**
- `testValidPaymentFlow()` - End-to-end valid payment processing
- `testInvalidEmailRejection()` - Email validation failure handling
- `testNegativeAmountRejection()` - Amount validation testing
- `testTransactionHistory()` - Transaction history functionality
- `testEmailValidationWithVariousFormats()` - Comprehensive email validation

### 3. `CucumberTestRunner.kt`
Cucumber test runner configuration for feature file execution (currently simplified due to feature file path issues).

### 4. `payment.feature` (in resources/features/)
Gherkin feature file defining user scenarios in natural language.

## Dependencies

- **Cucumber (7.15.0)**: BDD framework for feature files and step definitions
- **Kotest (5.8.0)**: Advanced Kotlin testing framework for enhanced assertions
- **JUnit**: Base testing framework for test execution

## Usage

### Running BDD Tests
```bash
# Run all BDD tests
./gradlew shared:testDebugUnitTest --tests="*SimpleBddTest*"

# Run all tests including BDD
./gradlew shared:test
```

### Test Coverage
The BDD tests cover:
- ✅ Payment form validation (email, amount, currency)
- ✅ Successful payment processing
- ✅ Error handling and user feedback
- ✅ Transaction history management
- ✅ API integration with MockPaymentApiService

## Architecture

The BDD tests integrate with:
- **Domain Layer**: PaymentValidator for business rule validation
- **Data Layer**: MockPaymentApiService for API simulation
- **Models**: Payment, Transaction, Currency, and response DTOs

This ensures that BDD tests validate the complete user journey while maintaining isolation from external dependencies. 