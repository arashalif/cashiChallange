Feature: Payment Processing
  As a user of the Cashi mobile app
  I want to send payments securely
  So that I can transfer money to recipients

  Background:
    Given the payment system is available
    And the transaction history is accessible

  Scenario: Successfully send a valid payment
    Given I have a valid email "john.doe@example.com"
    And I have a valid amount of 100.50
    And I select currency "USD"
    When I submit the payment
    Then the payment should be processed successfully
    And the transaction should appear in the history
    And I should see a success message

  Scenario: Reject payment with invalid email format
    Given I have an invalid email "invalid-email"
    And I have a valid amount of 50.00
    And I select currency "USD"
    When I submit the payment
    Then the payment should be rejected
    And I should see an error message "Invalid email format"

  Scenario: Reject payment with missing email domain
    Given I have an invalid email "user@"
    And I have a valid amount of 25.75
    And I select currency "EUR"
    When I submit the payment
    Then the payment should be rejected
    And I should see an error message "Invalid email format"

  Scenario: Reject payment with zero amount
    Given I have a valid email "jane.smith@example.com"
    And I have an invalid amount of 0.00
    And I select currency "GBP"
    When I submit the payment
    Then the payment should be rejected
    And I should see an error message "Amount must be greater than 0"

  Scenario: Reject payment with negative amount
    Given I have a valid email "bob.wilson@example.com"
    And I have an invalid amount of -10.00
    And I select currency "USD"
    When I submit the payment
    Then the payment should be rejected
    And I should see an error message "Amount must be greater than 0"

  Scenario: Reject payment with unsupported currency
    Given I have a valid email "alice.brown@example.com"
    And I have a valid amount of 75.25
    And I select an unsupported currency "XYZ"
    When I submit the payment
    Then the payment should be rejected
    And I should see an error message "Unsupported currency"

  Scenario Outline: Send payments with different valid currencies
    Given I have a valid email "user@example.com"
    And I have a valid amount of <amount>
    And I select currency "<currency>"
    When I submit the payment
    Then the payment should be processed successfully
    And the transaction should appear in the history with currency "<currency>"

    Examples:
      | amount | currency |
      | 100.00 | USD      |
      | 85.50  | EUR      |
      | 75.25  | GBP      |
      | 10000  | JPY      |
      | 150.75 | CAD      |

  Scenario: Retrieve transaction history
    Given there are existing transactions in the system
    When I request the transaction history
    Then I should receive a list of transactions
    And each transaction should have an email, amount, and currency
    And transactions should be sorted by date descending

  Scenario: Handle empty transaction history
    Given there are no transactions in the system
    When I request the transaction history
    Then I should receive an empty list
    And I should see a message "No transactions found"

  Scenario: Validate email with various formats
    Given I test email validation with the following emails:
      | email                    | valid |
      | user@example.com         | true  |
      | test.email@domain.co.uk  | true  |
      | user+tag@example.org     | true  |
      | user123@test-domain.com  | true  |
      | invalid-email            | false |
      | @example.com             | false |
      | user@                    | false |
      | user@.com                | false |
      | user..dot@example.com    | false |
    Then the email validation should return the expected results 