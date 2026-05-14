# Graph Report - .  (2026-05-14)

## Corpus Check
- Corpus is ~38,471 words - fits in a single context window. You may not need a graph.

## Summary
- 454 nodes · 550 edges · 49 communities (19 shown, 30 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 66 edges (avg confidence: 0.76)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_UI Shell & Navigation|UI Shell & Navigation]]
- [[_COMMUNITY_Feature Screens & ViewModels|Feature Screens & ViewModels]]
- [[_COMMUNITY_UI Composables|UI Composables]]
- [[_COMMUNITY_Design Mockups|Design Mockups]]
- [[_COMMUNITY_Analytics & Chart Components|Analytics & Chart Components]]
- [[_COMMUNITY_Data Layer Core|Data Layer Core]]
- [[_COMMUNITY_Category Management|Category Management]]
- [[_COMMUNITY_Transaction Database Queries|Transaction Database Queries]]
- [[_COMMUNITY_Transaction Repository Implementation|Transaction Repository Implementation]]
- [[_COMMUNITY_Search ViewModel Logic|Search ViewModel Logic]]
- [[_COMMUNITY_Transaction Repository Interface|Transaction Repository Interface]]
- [[_COMMUNITY_Add Transaction ViewModel|Add Transaction ViewModel]]
- [[_COMMUNITY_Navigation Routes|Navigation Routes]]
- [[_COMMUNITY_Category Database Queries|Category Database Queries]]
- [[_COMMUNITY_Budget Persistence|Budget Persistence]]
- [[_COMMUNITY_Room Type Converters|Room Type Converters]]
- [[_COMMUNITY_Dependency Injection|Dependency Injection]]
- [[_COMMUNITY_Budget Database Queries|Budget Database Queries]]
- [[_COMMUNITY_Calendar ViewModel|Calendar ViewModel]]
- [[_COMMUNITY_Budget ViewModel|Budget ViewModel]]
- [[_COMMUNITY_Category Repository Interface|Category Repository Interface]]
- [[_COMMUNITY_App Entry & Theming|App Entry & Theming]]
- [[_COMMUNITY_History ViewModel|History ViewModel]]
- [[_COMMUNITY_Budget Repository Interface|Budget Repository Interface]]
- [[_COMMUNITY_Settings ViewModel|Settings ViewModel]]
- [[_COMMUNITY_Room Database|Room Database]]
- [[_COMMUNITY_Settings Data Store|Settings Data Store]]
- [[_COMMUNITY_Settings Repository Impl|Settings Repository Impl]]
- [[_COMMUNITY_Settings Repository Interface|Settings Repository Interface]]
- [[_COMMUNITY_Budget Progress Calculation|Budget Progress Calculation]]
- [[_COMMUNITY_Unit Tests|Unit Tests]]
- [[_COMMUNITY_CSV Export|CSV Export]]
- [[_COMMUNITY_JSON Export|JSON Export]]
- [[_COMMUNITY_Instrumented Tests|Instrumented Tests]]
- [[_COMMUNITY_Home ViewModel|Home ViewModel]]
- [[_COMMUNITY_App Branding & Icons|App Branding & Icons]]
- [[_COMMUNITY_Daily Totals Calculation|Daily Totals Calculation]]
- [[_COMMUNITY_Data Exporters|Data Exporters]]
- [[_COMMUNITY_Transaction Search Result|Transaction Search Result]]
- [[_COMMUNITY_Period Domain Model|Period Domain Model]]
- [[_COMMUNITY_Accent Color Enum|Accent Color Enum]]
- [[_COMMUNITY_Finance Application Class|Finance Application Class]]
- [[_COMMUNITY_Budget Domain Model|Budget Domain Model]]
- [[_COMMUNITY_Gradle Wrapper|Gradle Wrapper]]
- [[_COMMUNITY_Settings Repository|Settings Repository]]

## God Nodes (most connected - your core abstractions)
1. `TransactionDao` - 16 edges
2. `TransactionRepositoryImpl` - 15 edges
3. `AppNavHost()` - 14 edges
4. `TransactionRepository` - 12 edges
5. `CategoryDao` - 11 edges
6. `Converters` - 9 edges
7. `CategoryRepositoryImpl` - 9 edges
8. `AppModule` - 9 edges
9. `AppDatabase (Room Database)` - 9 edges
10. `AppModule (Hilt DI Module)` - 9 edges

## Surprising Connections (you probably didn't know these)
- `ISpend Home Screen HTML Design` --conceptually_related_to--> `Budget Summary Card`  [INFERRED]
  design/01-home.html → app/src/main/java/com/financetracker/ui/home/HomeScreen.kt
- `ISpend Home Screen HTML Design` --conceptually_related_to--> `Donut Chart Component`  [INFERRED]
  design/01-home.html → app/src/main/java/com/financetracker/ui/components/DonutChart.kt
- `Settings ViewModel` --conceptually_related_to--> `Design Progress Checklist`  [AMBIGUOUS]
  app/src/main/java/com/financetracker/ui/settings/SettingsViewModel.kt → design/progress.md
- `History Screen` --conceptually_related_to--> `ISpend Home Screen HTML Design`  [INFERRED]
  app/src/main/java/com/financetracker/ui/history/HistoryScreen.kt → design/01-home.html
- `Home Screen` --conceptually_related_to--> `Emoji-Tagged Category System`  [INFERRED]
  app/src/main/java/com/financetracker/ui/home/HomeScreen.kt → design/index.html

## Hyperedges (group relationships)
- **Repository Pattern Implementation** — TransactionRepositoryImpl, CategoryRepositoryImpl, BudgetRepositoryImpl, SettingsRepositoryImpl [EXTRACTED 0.90]
- **Room Persistence Layer** — AppDatabase, TransactionDao, CategoryDao, BudgetDao, TransactionEntity, CategoryEntity, BudgetEntity, Converters, TransactionSearchResult, DailyTotal [EXTRACTED 0.95]
- **Hilt Dependency Injection Wiring** — AppModule, FinanceApp, MainActivity [EXTRACTED 0.90]
- **Budget Management System** — BudgetViewModel_BudgetViewModel, BudgetRepository_BudgetRepository, CalculateBudgetProgressUseCase_CalculateBudgetProgressUseCase [INFERRED 0.75]
- **Financial Visualization Pipeline** — AnalyticsScreen_AnalyticsScreen, DonutChart_DonutChart, BarChart_BarChart [INFERRED 0.80]
- **MVVM Architecture Pattern: Screen-ViewModel-Repository-Data flow** — HomeScreen_screen, HomeViewModel_viewmodel, HistoryScreen_screen, HistoryViewModel_viewmodel, SearchScreen_screen, SearchViewModel_viewmodel, SettingsScreen_screen, SettingsViewModel_viewmodel [EXTRACTED 1.00]
- **Multi-Theme System: Light + Dark + OLED with accent color picker** — FinanceTrackingAppTheme_composable, ColorTokens_theme, AccentColor_enum, UserPreferences_config, MultiThemeArchitecture_rationale [EXTRACTED 1.00]
- **HTML Design Prototype Covering All 9 Screens with Android Frame Gallery** — ISpendHomeDesign_design, ISpendGalleryDesign_design, ISpendDesignProgress_doc, ISpendPDFPrototype_doc, MaterialYou_rationale, OfflineFirst_rationale, EmojiCategories_rationale [EXTRACTED 1.00]
- **ISpend App Complete UI Screen Suite (7 Screens + Home)** — 02_add_transaction_screen, 03_history_screen, 04_search_screen, 05_dashboard_screen, 06_categories_screen, 07_budget_screen, 08_calendar_screen, 09_settings_screen [EXTRACTED 1.00]
- **Shared Bottom Navigation Bar Framework Across Screens** — bottom_navigation, 03_history_screen, 04_search_screen, 05_dashboard_screen, 06_categories_screen, 07_budget_screen, 08_calendar_screen, 09_settings_screen [EXTRACTED 1.00]
- **Shared Material Design 3 (OKLCH) Design Language Across All Screens** — design_system, 02_add_transaction_screen, 03_history_screen, 04_search_screen, 05_dashboard_screen, 06_categories_screen, 07_budget_screen, 08_calendar_screen, 09_settings_screen [EXTRACTED 1.00]

## Communities (49 total, 30 thin omitted)

### Community 0 - "UI Shell & Navigation"
Cohesion: 0.07
Nodes (44): Accent Color Options, Application FAB, App Navigation Host, Application Top Bar, Bottom Navigation Bar, Bottom Navigation Item Model, Budget Repository, Budget Summary Card (+36 more)

### Community 1 - "Feature Screens & ViewModels"
Cohesion: 0.1
Nodes (35): AddTransactionSheet, AddTransactionUiState, AddTransactionViewModel, AnalyticsScreen, AnalyticsUiState, AnalyticsViewModel, BarChart, BarData (+27 more)

### Community 2 - "UI Composables"
Cohesion: 0.09
Nodes (18): AddTransactionSheet(), CategoryChip(), BudgetScreen(), CalendarScreen(), DayCell(), AddCategoryDialog(), CategoriesScreen(), CategoryCard() (+10 more)

### Community 3 - "Design Mockups"
Cohesion: 0.12
Nodes (26): Add Transaction Screen (Bottom Sheet), Transaction History Screen, Search Transactions Screen, Analytics Dashboard Screen, Categories Management Screen, Monthly Budget Configuration Screen, Spending Calendar with Heat Map Screen, App Settings Screen (+18 more)

### Community 4 - "Analytics & Chart Components"
Cohesion: 0.12
Nodes (16): AnalyticsScreen(), StatBox(), AnalyticsUiState, AnalyticsViewModel, WeekdayBar, BarChart(), BarData, DonutChart() (+8 more)

### Community 5 - "Data Layer Core"
Cohesion: 0.21
Nodes (22): AppDatabase (Room Database), AppModule (Hilt DI Module), BudgetDao, BudgetEntity (Room Entity), BudgetRepositoryImpl, CategoryDao, CategoryEntity (Room Entity), CategoryRepositoryImpl (+14 more)

### Community 6 - "Category Management"
Cohesion: 0.12
Nodes (6): CategoriesUiState, CategoriesViewModel, CategoryWithProgress, CategoryEntity, Category, CategoryRepositoryImpl

### Community 9 - "Search ViewModel Logic"
Cohesion: 0.19
Nodes (7): Custom, DateFilter, None, Quick, QuickChip, SearchUiState, SearchViewModel

### Community 11 - "Add Transaction ViewModel"
Cohesion: 0.18
Nodes (3): AddTransactionUiState, AddTransactionViewModel, Transaction

### Community 12 - "Navigation Routes"
Cohesion: 0.17
Nodes (11): AddTransaction, Analytics, BottomNavItem, Budget, Calendar, Categories, History, Home (+3 more)

### Community 18 - "Calendar ViewModel"
Cohesion: 0.39
Nodes (3): CalendarDay, CalendarUiState, CalendarViewModel

### Community 19 - "Budget ViewModel"
Cohesion: 0.36
Nodes (3): BudgetUiState, BudgetViewModel, CategoryBudgetSliders

### Community 21 - "App Entry & Theming"
Cohesion: 0.39
Nodes (5): MainActivity, darkColorScheme(), FinanceTrackingAppTheme(), lightColorScheme(), oledColorScheme()

### Community 22 - "History ViewModel"
Cohesion: 0.29
Nodes (3): DateGroup, HistoryUiState, HistoryViewModel

### Community 35 - "App Branding & Icons"
Cohesion: 0.67
Nodes (3): ISpend Personal Finance Tracking Android App, ISpend App Android Launcher Icons (Adaptive Icon: foreground layer at multiple densities), ISpend App Play Store Listing Icon

## Ambiguous Edges - Review These
- `History ViewModel` → `Placeholder Unit and Instrumentation Tests`  [AMBIGUOUS]
  app/src/test/java/com/financetracker/ExampleUnitTest.kt · relation: semantically_similar_to
- `Settings ViewModel` → `Design Progress Checklist`  [AMBIGUOUS]
  app/src/main/java/com/financetracker/ui/settings/SettingsViewModel.kt · relation: conceptually_related_to

## Knowledge Gaps
- **69 isolated node(s):** `FinanceApp`, `DailyTotal`, `TransactionSearchResult`, `UserPreferences`, `Budget` (+64 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **30 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **What is the exact relationship between `History ViewModel` and `Placeholder Unit and Instrumentation Tests`?**
  _Edge tagged AMBIGUOUS (relation: semantically_similar_to) - confidence is low._
- **What is the exact relationship between `Settings ViewModel` and `Design Progress Checklist`?**
  _Edge tagged AMBIGUOUS (relation: conceptually_related_to) - confidence is low._
- **Why does `AppNavHost()` connect `UI Composables` to `Analytics & Chart Components`, `App Entry & Theming`?**
  _High betweenness centrality (0.015) - this node is a cross-community bridge._
- **Why does `Category` connect `Category Management` to `Transaction Repository Implementation`?**
  _High betweenness centrality (0.007) - this node is a cross-community bridge._
- **Why does `AnalyticsScreen()` connect `Analytics & Chart Components` to `UI Composables`?**
  _High betweenness centrality (0.005) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `AppNavHost()` (e.g. with `.onCreate()` and `AppTopBar()`) actually correct?**
  _`AppNavHost()` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `FinanceApp`, `DailyTotal`, `TransactionSearchResult` to the rest of the system?**
  _69 weakly-connected nodes found - possible documentation gaps or missing edges._