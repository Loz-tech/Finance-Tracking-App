# Graph Report - FinanceTrackingApp  (2026-05-16)

## Corpus Check
- 73 files · ~43,643 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 429 nodes · 456 edges · 45 communities (18 shown, 27 thin omitted)
- Extraction: 91% EXTRACTED · 9% INFERRED · 0% AMBIGUOUS · INFERRED: 43 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d77dad22`
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
- [[_COMMUNITY_Community 44|Community 44]]

## God Nodes (most connected - your core abstractions)
1. `Agent Guide — FinanceTrackingApp` - 19 edges
2. `TransactionDao` - 16 edges
3. `TransactionRepositoryImpl` - 15 edges
4. `AppNavHost()` - 14 edges
5. `Key Code` - 14 edges
6. `TransactionRepository` - 12 edges
7. `CategoryDao` - 11 edges
8. `CategoryRepositoryImpl` - 10 edges
9. `Plan: ktlint + `.editorconfig` Integration (Option B)` - 10 edges
10. `Execution Steps` - 10 edges

## Surprising Connections (you probably didn't know these)
- `QuickAddContent()` --calls--> `Category`  [INFERRED]
  app/src/main/java/com/financetracker/widget/QuickAddTransactionActivity.kt → app/src/main/java/com/financetracker/domain/model/Category.kt
- `AnalyticsScreen()` --calls--> `BarChart()`  [INFERRED]
  app/src/main/java/com/financetracker/ui/analytics/AnalyticsScreen.kt → app/src/main/java/com/financetracker/ui/components/BarChart.kt
- `AnalyticsScreen()` --calls--> `BarData`  [INFERRED]
  app/src/main/java/com/financetracker/ui/analytics/AnalyticsScreen.kt → app/src/main/java/com/financetracker/ui/components/BarChart.kt
- `AppNavHost()` --calls--> `AnalyticsScreen()`  [INFERRED]
  app/src/main/java/com/financetracker/ui/navigation/AppNavHost.kt → app/src/main/java/com/financetracker/ui/analytics/AnalyticsScreen.kt
- `AppNavHost()` --calls--> `BudgetScreen()`  [INFERRED]
  app/src/main/java/com/financetracker/ui/navigation/AppNavHost.kt → app/src/main/java/com/financetracker/ui/budget/BudgetScreen.kt

## Communities (45 total, 27 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (17): AddTransactionSheet(), CategoryChip(), BudgetScreen(), CalendarScreen(), DayCell(), AddCategoryDialog(), CategoriesScreen(), CategoryCard() (+9 more)

### Community 1 - "Community 1"
Cohesion: 0.12
Nodes (10): MainActivity, Transaction, SettingsDataStore, UserPreferences, darkColorScheme(), FinanceTrackingAppTheme(), lightColorScheme(), oledColorScheme() (+2 more)

### Community 2 - "Community 2"
Cohesion: 0.12
Nodes (15): AnalyticsScreen(), StatBox(), AnalyticsUiState, AnalyticsViewModel, WeekdayBar, BarChart(), BarData, DonutChart() (+7 more)

### Community 5 - "Community 5"
Cohesion: 0.19
Nodes (7): Custom, DateFilter, None, Quick, QuickChip, SearchUiState, SearchViewModel

### Community 6 - "Community 6"
Cohesion: 0.13
Nodes (6): CategoriesUiState, CategoriesViewModel, CategoryWithProgress, CategoryEntity, Category, CategoryRepositoryImpl

### Community 8 - "Community 8"
Cohesion: 0.17
Nodes (11): AddTransaction, Analytics, BottomNavItem, Budget, Calendar, Categories, History, Home (+3 more)

### Community 13 - "Community 13"
Cohesion: 0.17
Nodes (4): TransactionWidgetReceiver, WidgetCategory, WidgetCategoryStore, WidgetCategory

### Community 14 - "Community 14"
Cohesion: 0.06
Nodes (31): code:toml (ktlintGradle = "14.2.0"), code:block10 (- No detekt, ktlint, or `.editorconfig`), code:block11 (- **ktlint** — `org.jlleitschuh.gradle.ktlint` 14.2.0, confi), code:bash (./gradlew ktlintCheck), code:toml (ktlint = { id = "org.jlleitschuh.gradle.ktlint", version.ref), code:kotlin (alias(libs.plugins.ktlint) apply false), code:kotlin (ktlint {), code:editorconfig (root = true) (+23 more)

### Community 16 - "Community 16"
Cohesion: 0.36
Nodes (3): BudgetUiState, BudgetViewModel, CategoryBudgetSliders

### Community 17 - "Community 17"
Cohesion: 0.39
Nodes (3): CalendarDay, CalendarUiState, CalendarViewModel

### Community 18 - "Community 18"
Cohesion: 0.1
Nodes (20): Architecture Pattern, Build System, Code Context — FinanceTrackingApp, Code Style / Lint, code:kotlin (@Module), code:kotlin (@HiltViewModel), code:kotlin (@Composable), Custom Abstractions / Utils (+12 more)

### Community 20 - "Community 20"
Cohesion: 0.29
Nodes (3): DateGroup, HistoryUiState, HistoryViewModel

### Community 44 - "Community 44"
Cohesion: 0.06
Nodes (33): 10. How to Add a New Feature (End-to-End), 11. Testing, 12. Key Files to Read First, 13. Custom Abstractions / Utils, 14. Widget, 15. Code Style / Lint, 16. Common Patterns (Copy-Paste Ready), 17. Open Questions to Ask the User (+25 more)

## Knowledge Gaps
- **86 isolated node(s):** `FinanceApp`, `DailyTotal`, `TransactionSearchResult`, `Budget`, `Period` (+81 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **27 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AppNavHost()` connect `Community 0` to `Community 1`, `Community 2`?**
  _High betweenness centrality (0.050) - this node is a cross-community bridge._
- **Why does `Category` connect `Community 6` to `Community 1`, `Community 4`?**
  _High betweenness centrality (0.031) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `AppNavHost()` (e.g. with `.onCreate()` and `AppTopBar()`) actually correct?**
  _`AppNavHost()` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `FinanceApp`, `DailyTotal`, `TransactionSearchResult` to the rest of the system?**
  _86 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.08 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.12 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.12 - nodes in this community are weakly interconnected._