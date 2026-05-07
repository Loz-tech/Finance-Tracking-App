# ISpend — Finance Tracking App: Implementation Plan

## 1. Goal

Transform the existing HTML design prototype into a fully functional, offline-first native Android application named **FinanceTrackingApp** (package `com.financetracker`).

## 2. Design Analysis

The `design/` folder contains a 9-screen HTML prototype (`01-home.html` + `screens/*.html`) styled with custom Material Design 3 tokens (teal seed, OKLCH color space).

### Screens Inventory
| # | Screen | Key UI Elements | Complexity |
|---|--------|-----------------|------------|
| 1 | **Home** | Multi-ring donut chart (SVG), recent activity list, FAB, bottom nav | Medium |
| 2 | **Add Transaction** | Bottom sheet, emoji category grid (4-col), amount input, note field, date picker, save button | Medium |
| 3 | **History** | Month selector, date-grouped transaction list, sticky date headers, swipe hints | Medium |
| 4 | **Search** | Search bar with active-state chips, filter row, result list, empty state | Low |
| 5 | **Analytics** | Period toggle (Week/Month/Year), summary stat boxes, donut chart, bar chart (weekday spending) | High |
| 6 | **Categories** | Emoji-tagged category cards with progress bars, over-budget styling, FAB for add | Low |
| 7 | **Budget** | Total budget summary with progress bar, per-category sliders with preset chips | Medium |
| 8 | **Calendar** | Month pager, 7-column heat-map calendar, day summary card, intensity legend | High |
| 9 | **Settings** | Theme selector (Light/Dark/OLED), accent color picker, export/reset setting rows | Low |

### Design Tokens Used
- **Color**: OKLCH-based custom palette (primary teal, secondary muted, tertiary purple, error red)
- **Elevation**: 3 levels (`elevation-1`, `elevation-2`, `elevation-3`)
- **Shape**: `radius-xs` through `radius-xl`, full-rounded
- **Typography**: Google Sans / Roboto, 11px–36px scale
- **Motion**: Sheet slide-up (300ms), active-scale (0.96), hover lifts

### Interaction Patterns
- Bottom navigation with 4 tabs: Home, Analytics, Search, Settings
- FAB on Home & Categories triggers Add Transaction bottom sheet
- Back navigation on History, Categories, Budget, Calendar via top app-bar back button
- Calendar heat map uses 5 intensity tiers mapped to daily spend

## 3. Feature Requirements

### Core Features (MVP)
1. **Transaction CRUD** — Create, read, update, delete expenses with amount, note, date, category.
2. **Category Management** — Pre-populated emoji-tagged categories; user can add/edit/delete custom categories.
3. **Monthly Budgeting** — Set a total monthly budget + per-category budgets. Track spent vs. remaining.
4. **History View** — Chronological list grouped by date, with month navigation.
5. **Search & Filter** — Full-text search across transaction names/notes; filter by category or date range.
6. **Analytics Dashboard** — Donut chart (category breakdown), bar chart (weekday averages), summary stats.
7. **Calendar Heat Map** — Monthly calendar with color-coded spend intensity; tap a day to see transactions.
8. **Settings** — Theme toggle (Light / Dark / OLED black), accent color picker, data export (CSV/JSON), reset all data.

### Non-Functional Requirements
- **Offline-first**: All data stored locally; no network required.
- **Performance**: Launch to usable UI < 500 ms on mid-range Android (minSdk 24).
- **Accessibility**: Compose semantics, content descriptions for emoji icons, scalable text.
- **Backup**: Optional export to Downloads; later: auto-backup via Android Backup Service.

## 4. Tech Stack

| Layer | Technology | Rationale |
|-------|-----------|-----------|
| **Language** | Kotlin 2.x | Modern, null-safe, coroutines-native |
| **UI** | Jetpack Compose + Material3 | Matches design system exactly; dynamic theming support |
| **DI** | Hilt | Standard Android DI; reduces boilerplate |
| **Local DB** | Room (SQLite) | Structured relational data; migrations; Kotlin DSL |
| **Preferences** | DataStore (Proto / Preferences) | Type-safe settings; async; replace SharedPreferences |
| **Navigation** | Jetpack Navigation Compose | Single-activity; deep links; type-safe (Kotlin DSL) |
| **Charts** | Compose Canvas / custom Layout | Donut & bar charts are simple enough to draw without heavy libs (keeps APK lean). If complexity grows, evaluate `vico` or `MPAndroidChart`. |
| **Calendar** | Custom Compose layout | 7-column grid; day cell selection & heat tint |
| **Image/Emoji** | Text composables with emoji font | No image loading library needed |
| **Testing** | JUnit 5, Compose UI tests (TestRule), Espresso for integration, MockK | Standard Android test pyramid |
| **Build** | Gradle 8.x with Version Catalog (`libs.versions.toml`) | Already scaffolded; maintain consistency |

### Existing Project Gaps to Fix
- Package name currently `com.example.financetrackingapp` → rename to `com.financetracker`.
- Add Room, Hilt, DataStore, Navigation dependencies to `libs.versions.toml` and `app/build.gradle.kts`.
- Enable KAPT/KSP for Room and Hilt codegen.

## 5. Architecture

### High-Level Pattern
**MVVM + Repository + Clean-ish Layers**

```
UI Layer (Compose screens + ViewModels)
    │
Domain Layer (Use cases / Interactors — optional thin layer for complex ops)
    │
Data Layer (Repositories + Room DAOs + DataStore)
    │
Platform (Android Framework: SQLite, Filesystem)
```

### Module / Package Structure
```
com.financetracker
├── data
│   ├── local
│   │   ├── db
│   │   │   ├── AppDatabase.kt
│   │   │   ├── TransactionDao.kt
│   │   │   ├── CategoryDao.kt
│   │   │   ├── BudgetDao.kt
│   │   │   └── converter/   (Date, UUID TypeConverters)
│   │   ├── entity
│   │   │   ├── TransactionEntity.kt
│   │   │   ├── CategoryEntity.kt
│   │   │   └── BudgetEntity.kt
│   │   └── prefs
│   │       ├── SettingsDataStore.kt
│   │       └── UserPreferences.kt
│   └── repository
│       ├── TransactionRepositoryImpl.kt
│       ├── CategoryRepositoryImpl.kt
│       ├── BudgetRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
├── domain
│   ├── model
│   │   ├── Transaction.kt
│   │   ├── Category.kt
│   │   ├── Budget.kt
│   │   └── Period.kt
│   ├── repository (interfaces)
│   │   ├── TransactionRepository.kt
│   │   ├── CategoryRepository.kt
│   │   ├── BudgetRepository.kt
│   │   └── SettingsRepository.kt
│   └── usecase
│       ├── AddTransactionUseCase.kt
│       ├── GetMonthlySummaryUseCase.kt
│       ├── SearchTransactionsUseCase.kt
│       └── ... (1:1 with complex operations)
├── ui
│   ├── theme
│   │   ├── Color.kt          (custom OKLCH-inspired tokens)
│   │   ├── Theme.kt          (Light/Dark/OLED dynamic palette)
│   │   └── Type.kt
│   ├── navigation
│   │   ├── AppNavHost.kt
│   │   └── Screen.kt         (sealed class routes)
│   ├── home
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   ├── addtransaction
│   │   ├── AddTransactionSheet.kt
│   │   └── AddTransactionViewModel.kt
│   ├── history
│   │   ├── HistoryScreen.kt
│   │   └── HistoryViewModel.kt
│   ├── search
│   │   ├── SearchScreen.kt
│   │   └── SearchViewModel.kt
│   ├── analytics
│   │   ├── AnalyticsScreen.kt
│   │   └── AnalyticsViewModel.kt
│   ├── categories
│   │   ├── CategoriesScreen.kt
│   │   └── CategoriesViewModel.kt
│   ├── budget
│   │   ├── BudgetScreen.kt
│   │   └── BudgetViewModel.kt
│   ├── calendar
│   │   ├── CalendarScreen.kt
│   │   └── CalendarViewModel.kt
│   └── settings
│       ├── SettingsScreen.kt
│       └── SettingsViewModel.kt
├── di
│   └── AppModule.kt          (Hilt modules: Database, DAOs, Repositories, DataStore)
└── MainActivity.kt
```

### Data Model (Room Entities)

```kotlin
// TransactionEntity
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: UUID,
    val amount: BigDecimal,   // store as INTEGER (cents) or TEXT
    val note: String,
    val date: LocalDate,
    val categoryId: UUID,
    val createdAt: Instant
)

// CategoryEntity
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    val emoji: String,
    val colorHex: String?,
    val isDefault: Boolean,
    val sortOrder: Int
)

// BudgetEntity
@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: UUID,
    val categoryId: UUID?,      // null = total monthly budget
    val yearMonth: String,      // "2024-05"
    val limitAmount: BigDecimal
)
```

### Theming Strategy
- **Light / Dark / OLED**: Three `ColorScheme` objects in `Theme.kt`.
- **Accent Picker**: 6–8 pre-defined seed colors. On selection regenerate `ColorScheme` using Material3 `dynamicLightColorScheme` / `dynamicDarkColorScheme` or static palette derivation.
- **OLED mode**: `background = Color.Black`, `surface = Color.Black`, remove all surface-tint overlays.

## 6. Work Breakdown (Small Parts)

### Phase A: Foundation & Tooling
| Task | Files | Estimate |
|------|-------|----------|
| A1. Rename package to `com.financetracker` across manifest, source, build files | `build.gradle.kts`, `AndroidManifest.xml`, all `.kt` | 30 min |
| A2. Add dependencies: Room, Hilt, Navigation, DataStore, Compose BOM upgrade | `gradle/libs.versions.toml`, `app/build.gradle.kts` | 30 min |
| A3. Create Hilt `Application` class + `AppModule` | `FinanceApp.kt`, `di/AppModule.kt` | 20 min |
| A4. Define Room database, entities, DAOs, converters | `data/local/db/*`, `data/local/entity/*` | 1.5 h |
| A5. Set up DataStore for settings | `data/local/prefs/SettingsDataStore.kt` | 45 min |
| A6. Define domain models & repository interfaces | `domain/model/*`, `domain/repository/*` | 45 min |
| A7. Implement repository layer (Room + DataStore) | `data/repository/*` | 1.5 h |

### Phase B: Theme & Navigation Shell
| Task | Files | Estimate |
|------|-------|----------|
| B1. Build custom Material3 color tokens (teal seed + accent variants) | `ui/theme/Color.kt`, `Theme.kt` | 1 h |
| B2. Implement Light / Dark / OLED palette switching | `ui/theme/Theme.kt` | 45 min |
| B3. Create `Screen` sealed class + `AppNavHost` with bottom nav | `ui/navigation/*`, `MainActivity.kt` | 1 h |
| B4. Scaffold shared components: top app bar, bottom nav, FAB | `ui/components/AppScaffold.kt`, `BottomNavBar.kt`, `AppFAB.kt` | 1 h |

### Phase C: Core Screens (UI + ViewModel)
| Task | Files | Estimate |
|------|-------|----------|
| C1. **Home Screen** — donut chart (Canvas), recent list, budget summary card | `ui/home/*`, `ui/components/DonutChart.kt` | 2.5 h |
| C2. **Add Transaction Sheet** — category grid, amount input, note, date, save | `ui/addtransaction/*` | 2 h |
| C3. **History Screen** — month pager, sticky date headers, transaction list | `ui/history/*` | 1.5 h |
| C4. **Search Screen** — search bar, filter chips, result list, empty state | `ui/search/*` | 1.5 h |
| C5. **Analytics Screen** — period toggle, stat boxes, donut, bar chart | `ui/analytics/*`, `BarChart.kt` | 2.5 h |
| C6. **Categories Screen** — category cards with progress, FAB add | `ui/categories/*` | 1.5 h |
| C7. **Budget Screen** — total summary, per-category sliders, presets | `ui/budget/*` | 1.5 h |
| C8. **Calendar Screen** — month grid, heat map, day detail card | `ui/calendar/*`, `CalendarGrid.kt` | 2.5 h |
| C9. **Settings Screen** — theme chips, accent picker, export, reset | `ui/settings/*` | 1.5 h |

### Phase D: Logic, Polish, Testing
| Task | Files | Estimate |
|------|-------|----------|
| D1. Write use cases: monthly summary, search, budget calculations | `domain/usecase/*` | 1.5 h |
| D2. Implement CSV/JSON export in Settings | `data/export/CsvExporter.kt` | 1 h |
| D3. Add swipe-to-delete on History + undo snackbar | `ui/history/*` | 45 min |
| D4. Add default category seed data on first launch | `data/seed/CategorySeed.kt` | 30 min |
| D5. Unit tests: DAOs, repositories, use cases | `test/` | 2 h |
| D6. Compose UI tests: navigation, add transaction flow | `androidTest/` | 2 h |
| D7. QA pass: dark mode, OLED mode, dynamic font scale, RTL | all screens | 1.5 h |

### Total Estimated Effort
- **Phase A**: ~6 h
- **Phase B**: ~3.5 h
- **Phase C**: ~16 h
- **Phase D**: ~7.5 h
- **Buffer (20%)**: ~6.5 h
- **Grand Total**: ~40 hours of focused dev work

## 7. Risks, Tradeoffs, and Open Questions

| Risk | Mitigation |
|------|------------|
| Custom Canvas charts become hard to maintain | Start simple (Compose Canvas). If charts exceed ~200 LOC each, switch to `vico` Compose library. |
| Room schema changes after release | Version DB from day 1; write every migration in `AppDatabase` abstract class. |
| BigDecimal storage in SQLite | Store as `String` (TEXT) in Room to avoid precision loss; map to `BigDecimal` in entity. |
| Date/time handling across APIs | Use `java.time` (desugaring) or ` kotlinx.datetime`; keep everything in device local time. |
| Package rename breaks IDE caches | `./gradlew clean`, Invalidate Caches & Restart after A1. |

### Open Questions
1. Should the app support **income** transactions as well as expenses (bipolar amount sign), or only expenses?
   - *Proposal*: Store `amount` as positive = expense; allow negative for income. UI can toggle type in Add Transaction sheet.
2. Should there be a **recurring transaction** feature (subscriptions)?
   - *Proposal*: Defer to Phase 2 (post-MVP). Add `isRecurring` boolean flag to entity now so schema is forward-compatible.
3. Export format preference?
   - *Proposal*: CSV for spreadsheets, JSON for developer/debug use. Both written to `Downloads/ISpend/`.

## 8. Definition of Done (MVP)

- [ ] All 9 screens implemented in Compose with pixel-faithful layout to HTML prototype.
- [ ] Navigation between screens works via bottom nav and back buttons.
- [ ] User can add, view, edit, delete transactions.
- [ ] Categories are emoji-tagged and editable.
- [ ] Budgets can be set per month and per category; overspending is visually flagged.
- [ ] Analytics shows correct aggregated totals and charts.
- [ ] Calendar heat map reflects daily spend accurately.
- [ ] Settings persist theme/accent/export preferences across sessions.
- [ ] `./gradlew connectedCheck` passes (instrumented tests).
- [ ] `./gradlew test` passes (unit tests).
- [ ] APK size < 10 MB (no unnecessary heavy dependencies).

---
*Plan created: 2026-05-06*  
*Next step: Run `Phase A` tasks to set up project foundation before building screens.*