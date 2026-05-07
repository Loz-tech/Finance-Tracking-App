## ADDED Requirements

### Requirement: Theme mode selection
The system SHALL support three theme modes: Light, Dark, and OLED Black.

#### Scenario: Switch to Dark mode
- **WHEN** user selects the Dark theme chip in Settings
- **THEN** the entire app UI SHALL switch to dark color scheme immediately

#### Scenario: Switch to OLED mode
- **WHEN** user selects the OLED Black theme chip in Settings
- **THEN** the app background and surfaces SHALL become pure black (#000000) with reduced surface tint overlays

#### Scenario: Theme persistence
- **WHEN** user closes and reopens the app
- **THEN** the previously selected theme mode SHALL be restored

#### Scenario: Default theme
- **WHEN** the app launches for the first time
- **THEN** Light mode SHALL be the default theme

### Requirement: Accent color picker
The system SHALL allow users to select from a set of pre-defined accent colors.

#### Scenario: Change accent color
- **WHEN** user selects a different accent color from the color picker in Settings
- **THEN** the primary color across all app elements (FAB, charts, selected tabs, progress bars) SHALL update to the new accent

#### Scenario: Accent color persistence
- **WHEN** user closes and reopens the app
- **THEN** the previously selected accent color SHALL be restored

#### Scenario: Minimum accent options
- **WHEN** user opens the accent color picker
- **THEN** at least 6 accent color options SHALL be available (including the default teal)

### Requirement: Reset all data
The system SHALL allow users to delete all transactions, custom categories, and budgets with a confirmation step.

#### Scenario: Reset with confirmation
- **WHEN** user taps "Reset all data" in Settings and confirms the action
- **THEN** all transactions, custom categories, and budgets are permanently deleted and default categories are re-seeded

#### Scenario: Cancel reset
- **WHEN** user taps "Reset all data" but dismisses the confirmation dialog
- **THEN** no data is deleted

### Requirement: System dark mode follow
The system SHALL respect the system-wide dark mode setting as the initial theme.

#### Scenario: First launch on dark-mode device
- **WHEN** the app launches for the first time on a device with system dark mode enabled
- **THEN** the app SHALL start in Dark mode

### Requirement: OLED mode in battery saver
The system MAY default to OLED Black mode when the device is in battery saver mode.

#### Scenario: Battery saver activation
- **WHEN** battery saver is enabled and no user theme preference is set
- **THEN** the app SHALL use OLED Black mode to conserve power on OLED displays
