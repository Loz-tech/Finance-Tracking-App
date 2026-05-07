## Why

Users need a fast, offline-first expense tracker that respects privacy — no accounts, no cloud, no ads. Existing solutions either require internet access, lock features behind subscriptions, or have cluttered UIs. ISpend delivers a polished Material Design 3 Android experience that opens and logs expenses in seconds, with all data staying on-device.

## What Changes

- Build a complete native Android application from the existing HTML design prototype and Hermes implementation plan
- Implement 9 screens: Home, Add Transaction, History, Search, Analytics, Categories, Budget, Calendar, Settings
- Add offline-first local storage via Room (SQLite) for transactions, categories, and budgets
- Implement Material 3 theming with Light, Dark, and OLED modes plus accent color customization
- Add custom Canvas-based charts (donut chart, bar chart) for spending analytics
- Add calendar heat map with spending intensity visualization
- Support CSV/JSON data export for backup and spreadsheet use
- Seed default emoji-tagged categories on first launch
- Support full-text search, category filtering, and date-range filtering

## Capabilities

### New Capabilities
- `transaction-management`: Create, read, update, and delete expense transactions with amount, note, date, and category assignment
- `category-management`: Pre-populated emoji-tagged categories with user ability to add, edit, and delete custom categories
- `budget-management`: Set total monthly budget and per-category limits with overspending warnings
- `history-search`: Chronological transaction history grouped by date with month navigation, full-text search, and category/date filtering
- `analytics-dashboard`: Period-based spending analytics with donut chart (category breakdown), bar chart (weekday averages), and summary statistics
- `calendar-heatmap`: Monthly calendar view with color-coded daily spending intensity and day detail drill-down
- `settings-theming`: Theme toggle (Light/Dark/OLED), accent color picker, data export (CSV/JSON), and reset all data
- `data-export`: Export transactions to CSV and JSON formats to device Downloads directory

### Modified Capabilities
<!-- No existing capabilities to modify — this is a greenfield project -->

## Impact

- Affected code: Entire `app/` module — new Compose screens, ViewModels, Room database, repositories, Hilt DI, DataStore preferences, navigation graph
- Dependencies added: Room, Hilt, Jetpack Navigation Compose, DataStore, Compose BOM (Material3)
- Existing scaffold: Package already renamed to `com.financetracker`; `MainActivity.kt` exists with basic Compose setup
- No breaking changes (greenfield project)
- Target: Android API 24+, Kotlin 2.x, Gradle 8.x
