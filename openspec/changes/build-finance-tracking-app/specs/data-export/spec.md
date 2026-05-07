## ADDED Requirements

### Requirement: Export to CSV
The system SHALL export all transactions to a CSV file in the device Downloads directory.

#### Scenario: Successful CSV export
- **WHEN** user taps "Export as CSV" in Settings
- **THEN** a CSV file named `ISpend_export_YYYY-MM-DD.csv` is written to `Downloads/ISpend/` containing all transactions with columns: Date, Category, Emoji, Amount, Note

#### Scenario: Empty data export
- **WHEN** user taps export with no transactions
- **THEN** the system SHALL show a message "No transactions to export" and not create an empty file

#### Scenario: Overwrite existing file
- **WHEN** an export file with the same name already exists
- **THEN** the system SHALL overwrite the existing file without prompting

### Requirement: Export to JSON
The system SHALL export all transactions to a JSON file in the device Downloads directory.

#### Scenario: Successful JSON export
- **WHEN** user taps "Export as JSON" in Settings
- **THEN** a JSON file named `ISpend_export_YYYY-MM-DD.json` is written to `Downloads/ISpend/` containing all transactions as a JSON array with date, category, emoji, amount, and note fields

#### Scenario: Valid JSON structure
- **WHEN** the JSON export file is created
- **THEN** it SHALL be valid, parseable JSON conforming to a consistent schema

### Requirement: Export feedback
The system SHALL provide feedback after export operations.

#### Scenario: Export success snackbar
- **WHEN** export completes successfully
- **THEN** a snackbar SHALL display "Exported to Downloads/ISpend/" with the filename

#### Scenario: Export permission denied
- **WHEN** the app lacks storage write permission (API 29+ scoped storage)
- **THEN** the export SHALL still succeed using MediaStore or SAF fallback, or show an appropriate error message

### Requirement: Export directory creation
The system SHALL create the ISpend directory in Downloads if it does not exist.

#### Scenario: First export
- **WHEN** user exports for the first time and `Downloads/ISpend/` does not exist
- **THEN** the directory is created automatically before writing the export file
