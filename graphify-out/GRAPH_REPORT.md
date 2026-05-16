# Graph Report - FinanceTrackingApp  (2026-05-16)

## Corpus Check
- 70 files · ~39,704 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 336 nodes · 362 edges · 44 communities (17 shown, 27 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 41 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6bd5d68e`
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

## God Nodes (most connected - your core abstractions)
1. `TransactionDao` - 16 edges
2. `TransactionRepositoryImpl` - 15 edges
3. `AppNavHost()` - 14 edges
4. `TransactionRepository` - 12 edges
5. `CategoryDao` - 11 edges
6. `CategoryRepositoryImpl` - 10 edges
7. `Converters` - 9 edges
8. `AppModule` - 9 edges
9. `BudgetDao` - 8 edges
10. `BudgetRepositoryImpl` - 8 edges

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

## Communities (44 total, 27 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.08
Nodes (17): AddTransactionSheet(), CategoryChip(), BudgetScreen(), CalendarScreen(), DayCell(), AddCategoryDialog(), CategoriesScreen(), CategoryCard() (+9 more)

### Community 1 - "Community 1"
Cohesion: 0.1
Nodes (7): AddTransactionUiState, AddTransactionViewModel, Transaction, SettingsDataStore, UserPreferences, QuickAddContent(), QuickAddTransactionActivity

### Community 2 - "Community 2"
Cohesion: 0.16
Nodes (12): AnalyticsScreen(), StatBox(), BarChart(), BarData, DonutChart(), DonutLegend(), DonutSegment, BudgetSummaryCard() (+4 more)

### Community 5 - "Community 5"
Cohesion: 0.19
Nodes (7): Custom, DateFilter, None, Quick, QuickChip, SearchUiState, SearchViewModel

### Community 8 - "Community 8"
Cohesion: 0.17
Nodes (11): AddTransaction, Analytics, BottomNavItem, Budget, Calendar, Categories, History, Home (+3 more)

### Community 13 - "Community 13"
Cohesion: 0.22
Nodes (3): TransactionWidgetReceiver, WidgetCategoryStore, WidgetCategory

### Community 14 - "Community 14"
Cohesion: 0.25
Nodes (4): CategoriesUiState, CategoriesViewModel, CategoryWithProgress, Category

### Community 16 - "Community 16"
Cohesion: 0.36
Nodes (3): BudgetUiState, BudgetViewModel, CategoryBudgetSliders

### Community 17 - "Community 17"
Cohesion: 0.39
Nodes (3): CalendarDay, CalendarUiState, CalendarViewModel

### Community 18 - "Community 18"
Cohesion: 0.39
Nodes (5): MainActivity, darkColorScheme(), FinanceTrackingAppTheme(), lightColorScheme(), oledColorScheme()

### Community 20 - "Community 20"
Cohesion: 0.29
Nodes (3): DateGroup, HistoryUiState, HistoryViewModel

### Community 22 - "Community 22"
Cohesion: 0.53
Nodes (3): AnalyticsUiState, AnalyticsViewModel, WeekdayBar

## Knowledge Gaps
- **27 isolated node(s):** `FinanceApp`, `DailyTotal`, `TransactionSearchResult`, `Budget`, `Period` (+22 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **27 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `AppNavHost()` connect `Community 0` to `Community 18`, `Community 2`?**
  _High betweenness centrality (0.082) - this node is a cross-community bridge._
- **Why does `FinanceTrackingAppTheme()` connect `Community 18` to `Community 1`?**
  _High betweenness centrality (0.071) - this node is a cross-community bridge._
- **Why does `Category` connect `Community 14` to `Community 1`, `Community 4`, `Community 6`?**
  _High betweenness centrality (0.051) - this node is a cross-community bridge._
- **Are the 13 inferred relationships involving `AppNavHost()` (e.g. with `.onCreate()` and `AppTopBar()`) actually correct?**
  _`AppNavHost()` has 13 INFERRED edges - model-reasoned connections that need verification._
- **What connects `FinanceApp`, `DailyTotal`, `TransactionSearchResult` to the rest of the system?**
  _27 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.08 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.1 - nodes in this community are weakly interconnected._