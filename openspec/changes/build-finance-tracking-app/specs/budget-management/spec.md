## ADDED Requirements

### Requirement: Set total monthly budget
The system SHALL allow users to set a total monthly spending limit.

#### Scenario: Set total budget
- **WHEN** user enters a total monthly budget amount and saves
- **THEN** the budget is persisted and the Home screen SHALL display total spent vs. total budget with a progress indicator

#### Scenario: No budget set
- **WHEN** no total monthly budget is defined
- **THEN** the Home screen SHALL show total spending without a progress bar and prompt the user to set a budget

### Requirement: Set per-category budget limits
The system SHALL allow users to set spending limits for individual categories.

#### Scenario: Category budget with slider
- **WHEN** user adjusts a category's budget slider on the Budget screen
- **THEN** the limit is persisted immediately and reflected in the Categories screen progress bars

#### Scenario: Preset budget chips
- **WHEN** user taps a preset budget chip (e.g., $100, $200, $500)
- **THEN** the corresponding category's budget slider is set to that value

### Requirement: Monthly budget scope
The system SHALL scope budgets to specific calendar months.

#### Scenario: Budget for current month
- **WHEN** user sets a budget in May 2026
- **THEN** the budget applies only to transactions dated within May 2026

#### Scenario: Month transition
- **WHEN** the calendar month changes
- **THEN** budgets from the previous month SHALL be automatically carried forward as defaults for the new month

### Requirement: Overspending warning
The system SHALL visually warn users when spending exceeds budget limits.

#### Scenario: Total budget exceeded
- **WHEN** total spending for the month exceeds the total budget
- **THEN** the Home screen budget card SHALL display with warning styling

#### Scenario: Category budget exceeded
- **WHEN** a category's spending exceeds its limit
- **THEN** the category appears with error styling in both Categories and Budget screens
