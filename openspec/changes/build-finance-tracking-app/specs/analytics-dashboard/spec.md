## ADDED Requirements

### Requirement: Period toggle
The system SHALL allow switching analytics view between Week, Month, and Year periods.

#### Scenario: Period selection
- **WHEN** user taps the Week/Month/Year toggle
- **THEN** all charts and summary statistics update to reflect the selected period

#### Scenario: Default period
- **WHEN** user opens the Analytics screen for the first time
- **THEN** the Month period is selected by default

### Requirement: Summary statistics
The system SHALL display total spending, average daily spending, and transaction count for the selected period.

#### Scenario: Stat boxes
- **WHEN** user views the Analytics screen
- **THEN** three stat boxes SHALL show: Total Spent, Daily Average, and Number of Transactions for the selected period

#### Scenario: No transactions in period
- **WHEN** selected period has no transactions
- **THEN** all stat boxes SHALL show zero values

### Requirement: Category breakdown donut chart
The system SHALL render a donut chart showing spending distribution across categories for the selected period.

#### Scenario: Donut chart with multiple categories
- **WHEN** transactions exist across multiple categories in the selected period
- **THEN** the donut chart SHALL show proportional colored segments with category labels and percentage values

#### Scenario: Single category
- **WHEN** only one category has transactions in the selected period
- **THEN** the donut chart SHALL show a full ring in that category's color

#### Scenario: No data
- **WHEN** no transactions exist for the selected period
- **THEN** the donut chart SHALL show an empty ring with a "No data" message centered

### Requirement: Weekday spending bar chart
The system SHALL render a bar chart showing average spending per day of the week for the selected period.

#### Scenario: Weekday bar chart
- **WHEN** user views the Analytics screen
- **THEN** a bar chart SHALL display 7 bars (Mon–Sun) with heights proportional to average daily spending

#### Scenario: Bar chart tap
- **WHEN** user taps a day bar
- **THEN** the selected bar SHALL highlight and display the exact average amount for that weekday

### Requirement: Chart consistency
Charts across the app SHALL use consistent category color mapping.

#### Scenario: Same category, same color
- **WHEN** the Food category appears in the Home donut chart, Analytics donut chart, and Category screen
- **THEN** it SHALL use the same color across all views
