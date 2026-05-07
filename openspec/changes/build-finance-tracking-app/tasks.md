## 1. Foundation & Dependencies

- [x] 1.1 Verify package name is `com.financetracker` across manifest, build files, and source
- [x] 1.2 Add Room, Hilt, Navigation Compose, DataStore dependencies to `gradle/libs.versions.toml` and `app/build.gradle.kts`
- [x] 1.3 Enable KSP plugin for Room and Hilt code generation
- [x] 1.4 Create Hilt `Application` class (`FinanceApp.kt`) with `@HiltAndroidApp`

## 2. Data Layer — Database & Entities

- [x] 2.1 Define `TransactionEntity` with Room annotations (UUID, amount as BigDecimal→String, note, date, categoryId, createdAt)
- [x] 2.2 Define `CategoryEntity` with Room annotations (UUID, name, emoji, colorHex, isDefault, sortOrder)
- [x] 2.3 Define `BudgetEntity` with Room annotations (UUID, categoryId nullable, yearMonth, limitAmount)
- [x] 2.4 Create Room TypeConverters for UUID, LocalDate, BigDecimal, Instant
- [x] 2.5 Define `TransactionDao` with queries: insert, update, delete, getByDateRange, getByCategory, searchByNote, getDailyTotals
- [x] 2.6 Define `CategoryDao` with queries: getAll, getById, insert, update, delete, getDefaultCategories
- [x] 2.7 Define `BudgetDao` with queries: getByYearMonth, upsert, getCategoryBudgets
- [x] 2.8 Create `AppDatabase` abstract class (version 1) with all entities and DAOs

## 3. Data Layer — Preferences & Repositories

- [x] 3.1 Create `UserPreferences` data class and `SettingsDataStore` using DataStore Preferences (theme mode, accent color)
- [x] 3.2 Define domain models: `Transaction`, `Category`, `Budget`, `Period` in `domain/model/`
- [x] 3.3 Define repository interfaces: `TransactionRepository`, `CategoryRepository`, `BudgetRepository`, `SettingsRepository`
- [x] 3.4 Implement `TransactionRepositoryImpl` (wraps TransactionDao)
- [x] 3.5 Implement `CategoryRepositoryImpl` with default category seeding on first launch
- [x] 3.6 Implement `BudgetRepositoryImpl` with month carry-forward logic
- [x] 3.7 Implement `SettingsRepositoryImpl` (wraps DataStore)
- [x] 3.8 Create Hilt `AppModule` providing Database, DAOs, DataStore, and all repository bindings

## 4. Theme System

- [x] 4.1 Define custom Material 3 color tokens in `ui/theme/Color.kt` (teal seed + accent variants, OKLCH-inspired)
- [x] 4.2 Implement Light, Dark, and OLED `ColorScheme` objects in `ui/theme/Theme.kt`
- [x] 4.3 Implement dynamic theme switching using `SettingsRepository` theme preference
- [x] 4.4 Define typography scale in `ui/theme/Type.kt` (Google Sans / Roboto)

## 5. Navigation Shell

- [x] 5.1 Define `Screen` sealed class with all routes (Home, AddTransaction sheet, History, Search, Analytics, Categories, Budget, Calendar, Settings)
- [x] 5.2 Implement `AppNavHost` with bottom nav destinations (Home, Analytics, Search, Settings) and sub-screen routes
- [x] 5.3 Create shared `BottomNavBar` composable with 4 tabs and emoji icons
- [x] 5.4 Create shared `TopAppBar` composable with back button support
- [x] 5.5 Create shared `AppFAB` composable triggering Add Transaction sheet
- [x] 5.6 Wire navigation in `MainActivity` with theme-aware `FinanceAppTheme` wrapper

## 6. Home Screen

- [x] 6.1 Implement `HomeViewModel` — fetch total spent this month, budget progress, 5 most recent transactions
- [x] 6.2 Build budget summary card: total spent vs. budget with progress bar
- [x] 6.3 Implement `DonutChart` composable using Compose Canvas (animated drawing, category segments with labels)
- [x] 6.4 Build recent activity list with category emoji, name, amount, and relative timestamp
- [x] 6.5 Implement empty state: prompt to add first transaction
- [x] 6.6 Wire FAB → navigate to Add Transaction bottom sheet

## 7. Add Transaction Sheet

- [x] 7.1 Implement `AddTransactionViewModel` — validate inputs, save transaction via repository
- [x] 7.2 Build bottom sheet layout with slide-up animation
- [x] 7.3 Build 4-column emoji category grid with selection highlight
- [x] 7.4 Build amount input field (numeric, currency-formatted)
- [x] 7.5 Build note text field with placeholder
- [x] 7.6 Build date picker row (shows selected date, opens Material3 DatePickerDialog)
- [x] 7.7 Build Save button with validation (amount > 0 and category selected required)
- [x] 7.8 Wire sheet to accept optional pre-filled data for edit mode

## 8. History Screen

- [x] 8.1 Implement `HistoryViewModel` — fetch transactions grouped by date, month navigation state
- [x] 8.2 Build month selector header with left/right arrows showing current month label
- [x] 8.3 Build date-grouped LazyColumn with sticky date headers ("Today", "Yesterday", "May 5, 2026")
- [x] 8.4 Build transaction row composable (emoji, category name, note, amount, time)
- [x] 8.5 Implement swipe-to-delete with undo snackbar (5-second duration)
- [x] 8.6 Wire transaction tap → open Add Transaction sheet in edit mode

## 9. Search Screen

- [x] 9.1 Implement `SearchViewModel` — full-text search across notes and category names, category filter state
- [x] 9.2 Build search bar with active-state styling and clear button
- [x] 9.3 Build filter row with category chips (multi-select)
- [x] 9.4 Build search results list reusing transaction row composable
- [x] 9.5 Implement empty state: "No transactions found" when search matches nothing

## 10. Analytics Screen

- [x] 10.1 Implement `AnalyticsViewModel` — aggregate spending by period (week/month/year), category totals, weekday averages
- [x] 10.2 Build period toggle chips (Week / Month / Year)
- [x] 10.3 Build summary stat boxes: Total Spent, Daily Average, Transaction Count
- [x] 10.4 Build category breakdown donut chart reusing `DonutChart` with percentage labels
- [x] 10.5 Implement `BarChart` composable using Compose Canvas (7 bars Mon–Sun with tap-to-highlight)
- [x] 10.6 Ensure consistent category color mapping across all charts

## 11. Categories Screen

- [x] 11.1 Implement `CategoriesViewModel` — load categories with spending progress per budget
- [x] 11.2 Build category cards: emoji, name, spent/limit progress bar, over-budget styling
- [x] 11.3 Build FAB → open Add Category dialog (name input + emoji picker)
- [x] 11.4 Implement edit category dialog (name and emoji changeable)
- [x] 11.5 Implement delete category with confirmation dialog and transaction reassignment to "Other"
- [x] 11.6 Build emoji picker grid for category creation/editing

## 12. Budget Screen

- [x] 12.1 Implement `BudgetViewModel` — load/save total budget and per-category limits
- [x] 12.2 Build total monthly budget input with currency formatting
- [x] 12.3 Build per-category budget sliders with spent/limit display
- [x] 12.4 Build preset budget chips ($50, $100, $200, $500) that set slider values
- [x] 12.5 Implement budget auto-carry-forward to new months
- [x] 12.6 Show overspending warning styling on categories exceeding limits

## 13. Calendar Screen

- [x] 13.1 Implement `CalendarViewModel` — compute daily totals for displayed month, intensity tiers
- [x] 13.2 Build month pager with swipe gesture detection and month/year header
- [x] 13.3 Build 7-column calendar grid with day-of-week headers (S M T W T F S)
- [x] 13.4 Implement 5-tier heat map intensity coloring on day cells
- [x] 13.5 Build day detail card: selected day's total and transaction list
- [x] 13.6 Implement today indicator (distinct border/ring)
- [x] 13.7 Mute future dates as non-interactive

## 14. Settings Screen

- [x] 14.1 Implement `SettingsViewModel` — load/save preferences, trigger export
- [x] 14.2 Build theme selector row with Light/Dark/OLED chips
- [x] 14.3 Build accent color picker row with 6–8 color swatches
- [x] 14.4 Build export section: "Export as CSV" and "Export as JSON" buttons
- [x] 14.5 Build "Reset all data" button with confirmation dialog
- [x] 14.6 Implement OLED mode auto-activation when battery saver is on (best effort)

## 15. Data Export

- [x] 15.1 Implement `CsvExporter` — serialize all transactions to CSV with columns: Date, Category, Emoji, Amount, Note
- [x] 15.2 Implement `JsonExporter` — serialize all transactions to JSON array with consistent schema
- [x] 15.3 Create `Downloads/ISpend/` directory on first export
- [x] 15.4 Show success/error snackbar after export
- [x] 15.5 Handle empty data case: show "No transactions to export" instead of empty file

## 16. Domain Logic — Use Cases

- [x] 16.1 Implement `GetMonthlySummaryUseCase` — aggregate totals, daily averages, category breakdowns
- [x] 16.2 Implement `SearchTransactionsUseCase` — full-text search with category filter merging
- [x] 16.3 Implement `CalculateBudgetProgressUseCase` — per-category spending vs. limit with over-budget detection
- [x] 16.4 Implement `GetDailyTotalsUseCase` — compute daily spend for calendar heat map intensity tiers

## 17. Polish & Testing

- [ ] 17.1 Verify theme switching (Light ↔ Dark ↔ OLED) works instantly across all screens
- [ ] 17.2 Verify accent color changes propagate to FAB, charts, selected tabs, progress bars
- [ ] 17.3 Verify theme and accent preferences persist across app restarts
- [ ] 17.4 Test empty states on all screens: Home, History, Search, Analytics, Categories, Calendar
- [ ] 17.5 Test add transaction flow: category selection, amount input, date picker, save
- [ ] 17.6 Test edit and delete transaction flows including undo
- [ ] 17.7 Test category CRUD: add, edit, delete with transaction reassignment
- [ ] 17.8 Test budget setting and overspending indicators
- [ ] 17.9 Test CSV and JSON export produces valid files in Downloads/ISpend/
- [ ] 17.10 Write unit tests for Room DAOs
- [ ] 17.11 Write unit tests for repository implementations
- [ ] 17.12 Write unit tests for use cases
- [ ] 17.13 Write Compose UI test for navigation flow through all bottom nav tabs
- [ ] 17.14 Write Compose UI test for add transaction → appears in Home recent list
- [ ] 17.15 Verify `./gradlew test` and `./gradlew connectedCheck` pass
