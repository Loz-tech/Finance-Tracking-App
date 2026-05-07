## ADDED Requirements

### Requirement: Monthly calendar view
The system SHALL display a 7-column monthly calendar grid with day cells color-coded by spending intensity.

#### Scenario: Month navigation
- **WHEN** user swipes left or right on the calendar
- **THEN** the displayed month changes to the previous or next month respectively

#### Scenario: Month label
- **WHEN** the calendar is displayed
- **THEN** the current month and year (e.g., "May 2026") SHALL be shown in the header

### Requirement: Spending intensity heat map
The system SHALL apply color intensity to calendar day cells based on daily total spending relative to the monthly maximum.

#### Scenario: Five intensity tiers
- **WHEN** daily spending amounts vary
- **THEN** day cells SHALL use one of 5 color intensity levels: none (no spend), very low, low, medium, high

#### Scenario: Relative intensity
- **WHEN** the highest spending day in the month is $100
- **THEN** a $50 day SHALL be at medium intensity and a $10 day at very low intensity

#### Scenario: No spending on a day
- **WHEN** a day has zero transactions
- **THEN** the day cell SHALL use the default surface color with no intensity tint

### Requirement: Day detail card
The system SHALL display a summary card when a calendar day is selected, showing the day's total spending and transaction list.

#### Scenario: Tap day with transactions
- **WHEN** user taps a day cell that has transactions
- **THEN** a detail card SHALL appear below the calendar showing total amount for that day and a list of its transactions

#### Scenario: Tap day with no transactions
- **WHEN** user taps a day cell with no transactions
- **THEN** the detail card SHALL show "No expenses" for that day

#### Scenario: Deselect day
- **WHEN** user taps the same day cell again
- **THEN** the detail card SHALL be dismissed

### Requirement: Today indicator
The system SHALL visually distinguish the current day on the calendar.

#### Scenario: Current day marker
- **WHEN** the displayed month contains today's date
- **THEN** today's cell SHALL have a distinct border or ring indicator regardless of spending intensity

### Requirement: Future dates
The system SHALL render future dates as non-interactive cells.

#### Scenario: Future date display
- **WHEN** the calendar shows a month containing future dates
- **THEN** future date cells SHALL appear muted and not respond to taps
