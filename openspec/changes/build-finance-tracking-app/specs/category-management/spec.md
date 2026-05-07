## ADDED Requirements

### Requirement: Default categories on first launch
The system SHALL seed a set of default emoji-tagged expense categories when the app launches for the first time.

#### Scenario: First launch seeding
- **WHEN** the app launches with an empty categories table
- **THEN** a predefined set of categories (e.g., 🍔 Food, 🚗 Transport, 🏠 Housing, 🎮 Entertainment, 🛒 Shopping, 💊 Health, 📚 Education, 📦 Other) is created

#### Scenario: Subsequent launches
- **WHEN** the app launches and categories already exist
- **THEN** no additional default categories are created

### Requirement: Add custom category
The system SHALL allow users to create custom categories with a name and emoji.

#### Scenario: Create custom category
- **WHEN** user taps FAB on Categories screen and enters a name and selects an emoji
- **THEN** the new category is persisted and appears in the category list and transaction category picker

#### Scenario: Duplicate category name
- **WHEN** user tries to create a category with a name that already exists
- **THEN** the system SHALL show an error and prevent duplicate creation

### Requirement: Edit category
The system SHALL allow users to edit an existing category's name and emoji.

#### Scenario: Rename category
- **WHEN** user edits a category name
- **THEN** the new name is reflected across all transactions assigned to that category

### Requirement: Delete category
The system SHALL allow users to delete a category, reassigning its transactions to a fallback category.

#### Scenario: Delete category with transactions
- **WHEN** user deletes a category that has existing transactions
- **THEN** the system SHALL prompt the user to confirm and reassign those transactions to "Other" category

#### Scenario: Delete unused category
- **WHEN** user deletes a category with no transactions
- **THEN** the category is removed immediately without reassignment prompt

### Requirement: Category with budget awareness
The system SHALL display spending progress per category against its budget limit.

#### Scenario: Over-budget category
- **WHEN** a category's spending exceeds its monthly budget
- **THEN** the category card SHALL display with an error/red styling and an "over budget" indicator

#### Scenario: Under-budget category
- **WHEN** a category's spending is within its monthly budget
- **THEN** the category card SHALL display a progress bar showing spent vs. limit with neutral styling
