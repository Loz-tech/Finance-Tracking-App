## ADDED Requirements

### Requirement: Create transaction
The system SHALL allow users to create an expense transaction with an amount, optional note, date, and category assignment.

#### Scenario: Successful transaction creation
- **WHEN** user enters a positive amount, selects a category, and taps Save
- **THEN** the transaction is persisted locally and appears in the recent activity list on Home screen

#### Scenario: Missing required fields
- **WHEN** user taps Save without entering an amount or selecting a category
- **THEN** the system SHALL show a validation error and prevent saving

#### Scenario: Empty note
- **WHEN** user enters amount and category but leaves note empty
- **THEN** the system SHALL save the transaction with an empty note field

### Requirement: View transactions
The system SHALL display transactions grouped by date in chronological order.

#### Scenario: Recent activity on Home
- **WHEN** user opens the Home screen
- **THEN** the system SHALL show the 5 most recent transactions with category emoji, name, amount, and relative timestamp

#### Scenario: Empty transaction list
- **WHEN** no transactions exist
- **THEN** the system SHALL display an empty state message with a prompt to add a first transaction

### Requirement: Edit transaction
The system SHALL allow users to modify any field of an existing transaction.

#### Scenario: Edit transaction details
- **WHEN** user selects a transaction and changes its amount, category, note, or date
- **THEN** the updated values are persisted and reflected across all views

### Requirement: Delete transaction
The system SHALL allow users to delete a transaction with an undo option.

#### Scenario: Swipe to delete
- **WHEN** user swipes a transaction row in the History screen
- **THEN** the transaction is removed and an undo snackbar appears for 5 seconds

#### Scenario: Undo delete
- **WHEN** user taps Undo on the delete snackbar
- **THEN** the transaction is restored to its previous state

### Requirement: Transaction data model
The system SHALL store transaction amount as a decimal value with exact precision.

#### Scenario: Precision preservation
- **WHEN** a transaction of $12.99 is saved
- **THEN** the stored and displayed amount SHALL be exactly 12.99 with no rounding errors
