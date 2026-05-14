# Graph Report - FinanceTrackingApp  (2026-05-14)

## Corpus Check
- 66 files · ~38,520 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 457 nodes · 554 edges · 50 communities (20 shown, 30 thin omitted)
- Extraction: 87% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 69 edges (avg confidence: 0.76)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `28163f04`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 19|Community 19]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]
- [[_COMMUNITY_Community 24|Community 24]]
- [[_COMMUNITY_Community 25|Community 25]]
- [[_COMMUNITY_Community 26|Community 26]]
- [[_COMMUNITY_Community 27|Community 27]]
- [[_COMMUNITY_Community 28|Community 28]]
- [[_COMMUNITY_Community 29|Community 29]]
- [[_COMMUNITY_Community 30|Community 30]]
- [[_COMMUNITY_Community 31|Community 31]]
- [[_COMMUNITY_Community 32|Community 32]]
- [[_COMMUNITY_Community 33|Community 33]]
- [[_COMMUNITY_Community 34|Community 34]]
- [[_COMMUNITY_Community 35|Community 35]]
- [[_COMMUNITY_Community 36|Community 36]]
- [[_COMMUNITY_Community 37|Community 37]]
- [[_COMMUNITY_Community 38|Community 38]]
- [[_COMMUNITY_Community 39|Community 39]]
- [[_COMMUNITY_Community 40|Community 40]]
- [[_COMMUNITY_Community 41|Community 41]]
- [[_COMMUNITY_Community 42|Community 42]]
- [[_COMMUNITY_Community 47|Community 47]]
- [[_COMMUNITY_Community 48|Community 48]]

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

## Communities (50 total, 30 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.07
Nodes (44): Accent Color Options, Application FAB, App Navigation Host, Application Top Bar, Bottom Navigation Bar, Bottom Navigation Item Model, Budget Repository, Budget Summary Card (+36 more)

### Community 1 - "Community 1"
Cohesion: 0.1
Nodes (35): AddTransactionSheet, AddTransactionUiState, AddTransactionViewModel, AnalyticsScreen, AnalyticsUiState, AnalyticsViewModel, BarChart, BarData (+27 more)

### Community 2 - "Community 2"
Cohesion: 0.08
Nodes (19): AddTransactionSheet(), CategoryChip(), BudgetScreen(), CalendarScreen(), DayCell(), AddCategoryDialog(), CategoriesScreen(), CategoryCard() (+11 more)

### Community 3 - "Community 3"
Cohesion: 0.12
Nodes (26): Add Transaction Screen (Bottom Sheet), Transaction History Screen, Search Transactions Screen, Analytics Dashboard Screen, Categories Management Screen, Monthly Budget Configuration Screen, Spending Calendar with Heat Map Screen, App Settings Screen (+18 more)

### Community 4 - "Community 4"
Cohesion: 0.12
Nodes (16): AnalyticsScreen(), StatBox(), AnalyticsUiState, AnalyticsViewModel, WeekdayBar, BarChart(), BarData, DonutChart() (+8 more)

### Community 5 - "Community 5"
Cohesion: 0.21
Nodes (22): AppDatabase (Room Database), AppModule (Hilt DI Module), BudgetDao, BudgetEntity (Room Entity), BudgetRepositoryImpl, CategoryDao, CategoryEntity (Room Entity), CategoryRepositoryImpl (+14 more)

### Community 6 - "Community 6"
Cohesion: 0.12
Nodes (6): CategoriesUiState, CategoriesViewModel, CategoryWithProgress, CategoryEntity, Category, CategoryRepositoryImpl

### Community 9 - "Community 9"
Cohesion: 0.19
Nodes (7): Custom, DateFilter, None, Quick, QuickChip, SearchUiState, SearchViewModel

### Community 11 - "Community 11"
Cohesion: 0.18
Nodes (3): AddTransactionUiState, AddTransactionViewModel, Transaction

### Community 12 - "Community 12"
Cohesion: 0.17
Nodes (11): AddTransaction, Analytics, BottomNavItem, Budget, Calendar, Categories, History, Home (+3 more)

### Community 18 - "Community 18"
Cohesion: 0.39
Nodes (3): CalendarDay, CalendarUiState, CalendarViewModel

### Community 19 - "Community 19"
Cohesion: 0.36
Nodes (3): BudgetUiState, BudgetViewModel, CategoryBudgetSliders

### Community 21 - "Community 21"
Cohesion: 0.39
Nodes (5): MainActivity, darkColorScheme(), FinanceTrackingAppTheme(), lightColorScheme(), oledColorScheme()

### Community 22 - "Community 22"
Cohesion: 0.29
Nodes (3): DateGroup, HistoryUiState, HistoryViewModel

### Community 35 - "Community 35"
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
- **Why does `AppNavHost()` connect `Community 2` to `Community 4`, `Community 21`?**
  _High betweenness centrality (0.015) - this node is a cross-community bridge._
- **Why does `Category` connect `Community 6` to `Community 8`?**
  _High betweenness centrality (0.007) - this node is a cross-community bridge._
- **Why does `HomeScreen()` connect `Community 4` to `Community 2`?**
  _High betweenness centrality (0.005) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `AppNavHost()` (e.g. with `.onCreate()` and `AppTopBar()`) actually correct?**
  _`AppNavHost()` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `FinanceApp`, `DailyTotal`, `TransactionSearchResult` to the rest of the system?**
  _69 weakly-connected nodes found - possible documentation gaps or missing edges._