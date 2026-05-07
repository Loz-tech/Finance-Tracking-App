## ADDED Requirements

### Requirement: Chronological transaction history
The system SHALL display all transactions grouped by date in reverse chronological order with sticky date headers.

#### Scenario: Date-grouped list
- **WHEN** user opens the History screen
- **THEN** transactions are grouped under date headers (e.g., "Today", "Yesterday", "May 5, 2026") and sorted newest first

#### Scenario: Month navigation
- **WHEN** user taps the month selector
- **THEN** the system SHALL allow navigating to any past month and show only transactions from that month

#### Scenario: Sticky date headers
- **WHEN** user scrolls through the history list
- **THEN** the current date header SHALL remain pinned to the top of the list until the next date group scrolls into view

### Requirement: Full-text search
The system SHALL allow users to search transactions by note text and category name.

#### Scenario: Search by note
- **WHEN** user types "groceries" in the search bar
- **THEN** all transactions whose note contains "groceries" (case-insensitive) are displayed

#### Scenario: Search by category
- **WHEN** user types "food" in the search bar
- **THEN** all transactions whose category name contains "food" are displayed

#### Scenario: No search results
- **WHEN** search query matches no transactions
- **THEN** the system SHALL display an empty state with "No transactions found" message

### Requirement: Category filter
The system SHALL allow filtering transactions by one or more categories.

#### Scenario: Single category filter
- **WHEN** user selects a category chip filter
- **THEN** only transactions belonging to that category are displayed

#### Scenario: Multiple category filters
- **WHEN** user selects multiple category chip filters
- **THEN** transactions matching any of the selected categories are displayed

#### Scenario: Clear filters
- **WHEN** user deselects all category chips
- **THEN** all transactions are shown again

### Requirement: Combined search and filter
Search and category filters SHALL work together additively.

#### Scenario: Search with category filter
- **WHEN** user searches "coffee" with the "Food" category filter active
- **THEN** only Food-category transactions whose note contains "coffee" are displayed
