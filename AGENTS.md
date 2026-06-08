# Agent Guide — FinanceTrackingApp

Use this guide before planning or implementing any change. It covers architecture, conventions, and the end-to-end pattern for adding features.

## 1. Project Overview

- **Type:** Android application (single `:app` module)
- **Package:** `com.financetracker`
- **Language:** Kotlin 2.2.10 (Java 11 target)
- **Min SDK:** 24 · **Compile SDK:** 36
- **Build:** Gradle with version catalogs (`gradle/libs.versions.toml`)

## 2. Tech Stack

| Layer | Technology | Version (key) |
|-------|-----------|---------------|
| UI | Jetpack Compose + Material3 | BOM 2026.02.01 |
| Navigation | Navigation Compose | 2.9.0 |
| DI | Dagger Hilt | 2.59.2 |
| Database | Room (KSP compiler) | 2.7.1 |
| Preferences | DataStore Preferences | 1.1.6 |
| Serialization | Kotlinx Serialization JSON | 1.8.1 |
| Activity | `androidx.activity:activity-compose` | — |

**Plugins in `:app`:** `android.application`, `kotlin.compose`, `kotlin.serialization`, `com.google.devtools.ksp`, `dagger.hilt.android.plugin`

## 3. Architecture

Clean Architecture-lite with MVVM:

```
domain/      → pure Kotlin models, repository interfaces, use cases
data/        → Room entities, DAOs, repository impls, local prefs, exporters
ui/          → feature packages with Screen + ViewModel + UiState
di/          → Hilt modules
```

### Layer Rules
- **Domain** has no Android/framework dependencies. Models use `UUID`, `BigDecimal`, `LocalDate`.
- **Data** maps `Entity ↔ Domain` in repository implementations.
- **UI** is Compose-only. No Fragments. ViewModels are `HiltViewModel`.

## 4. Directory Structure

```
app/src/main/java/com/financetracker/
├── di/
│   └── AppModule.kt                    # Single Hilt module (@InstallIn(SingletonComponent))
├── domain/
│   ├── model/
│   │   ├── Transaction.kt
│   │   ├── Category.kt
│   │   └── Budget.kt
│   ├── repository/
│   │   ├── TransactionRepository.kt
│   │   ├── CategoryRepository.kt
│   │   └── BudgetRepository.kt
│   └── usecase/
│       └── *UseCase.kt                 # operator fun invoke()
├── data/
│   ├── local/
│   │   ├── db/
│   │   │   ├── AppDatabase.kt
│   │   │   ├── Converters.kt
│   │   │   ├── TransactionDao.kt
│   │   │   ├── CategoryDao.kt
│   │   │   └── BudgetDao.kt
│   │   ├── entity/
│   │   │   ├── TransactionEntity.kt
│   │   │   ├── CategoryEntity.kt
│   │   │   └── BudgetEntity.kt
│   │   └── prefs/
│   │       └── SettingsDataStore.kt
│   └── repository/
│       ├── TransactionRepositoryImpl.kt
│       ├── CategoryRepositoryImpl.kt
│       └── BudgetRepositoryImpl.kt
├── ui/
│   ├── navigation/
│   │   ├── AppNavHost.kt               # Central NavHost + Scaffold
│   │   └── Screen.kt                   # Sealed routes + BottomNavItem
│   ├── theme/
│   │   ├── Theme.kt                    # Light/Dark/OLED + accent colors
│   │   └── Color.kt / Type.kt
│   ├── components/
│   │   ├── TransactionCard.kt
│   │   ├── BarChart.kt / DonutChart.kt
│   │   └── ...
│   └── <feature>/                      # One package per screen
│       ├── *Screen.kt
│       ├── *ViewModel.kt
│       └── *UiState.kt
├── widget/
│   └── QuickAddTransactionActivity.kt  # Widget quick-add (manual DI, no VM)
└── MainActivity.kt                     # @AndroidEntryPoint, seeds categories, edge-to-edge
```

## 5. Naming Conventions

| Concept | Pattern | Example |
|---------|---------|---------|
| Domain model | plain data class | `Transaction` |
| Room entity | `*Entity` | `TransactionEntity` |
| DAO | `*Dao` interface | `TransactionDao` |
| Repository interface | `*Repository` | `TransactionRepository` |
| Repository impl | `*RepositoryImpl` | `TransactionRepositoryImpl` |
| Use case | `*UseCase` | `GetMonthlySummaryUseCase` |
| Screen composable | `*Screen` or `*Sheet` | `HomeScreen`, `AddTransactionSheet` |
| ViewModel | `*ViewModel` | `HomeViewModel` |
| UI state | `*UiState` data class | `HomeUiState` |
| Navigation route | `Screen.*` sealed object | `Screen.Home` |

## 6. Database

- **Room version:** 1 (`exportSchema = false`)
- **Entities:** `TransactionEntity`, `CategoryEntity`, `BudgetEntity`
- **Converters:** `UUID↔String`, `LocalDate↔String`, `BigDecimal↔String`, `Instant↔Long`
- **Schema:**
  - `transactions`: id (UUID PK), amount (BigDecimal), note, date (LocalDate), categoryId (UUID), createdAt (Instant)
  - `categories`: id (UUID PK), name, emoji, colorHex, isDefault, sortOrder
  - `budgets`: id (UUID PK), categoryId (UUID? null = total), yearMonth (String), limitAmount (BigDecimal)
- **Query DTOs:** `TransactionSearchResult` (JOIN result), `DailyTotal` (aggregation)

⚠️ Migrations: Currently no migrations exist. `exportSchema = false`. Ask the user whether to write `Migration` objects or use destructive migration during development.

## 7. Dependency Injection (Hilt)

Single module pattern in `di/AppModule.kt`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase
    @Provides fun provideTransactionDao(db: AppDatabase): TransactionDao
    @Provides @Singleton fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository = impl
    // Repeat for Category, Budget, Settings
}
```

ViewModels:
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    ...
) : ViewModel() { ... }
```

Screens:
```kotlin
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) { ... }
```

## 8. UI Patterns

- Each screen has a `*UiState` data class and `StateFlow<*UiState>` in the ViewModel.
- Collect in screen: `val uiState by viewModel.uiState.collectAsState()`
- Mutate state: `_uiState.value = _uiState.value.copy(...)`
- No shared MVI / event bus. Keep it simple.
- `AppNavHost` owns the `Scaffold`, conditional `TopAppBar`/`BottomBar`/`FAB`, and `NavHost`.
- Bottom nav: Home, Analytics, Search, Settings
- Sub-screens (top bar + back): History, Categories, Budget, Calendar
- Overlay sheet: AddTransaction
- Edge-to-edge enabled in `MainActivity` via `enableEdgeToEdge()`
- Theme supports Light, Dark, OLED, and 6 accent colors via `SettingsDataStore`

## 9. Navigation

All routes defined in `ui/navigation/Screen.kt`:

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction")
    object AddTransactionWithId : Screen("add_transaction/{transactionId}")
    // ...
}
```

Register in `AppNavHost.kt`:
```kotlin
NavHost(navController, startDestination = Screen.Home.route) {
    composable(Screen.Home.route) { HomeScreen(navController) }
    composable(Screen.AddTransaction.route) { AddTransactionSheet(navController) }
    composable(
        Screen.AddTransactionWithId.route,
        arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
    ) { ... }
}
```

## 10. How to Add a New Feature (End-to-End)

Follow this order for every new feature:

1. **Domain:** Add model in `domain/model/` and repository interface method in `domain/repository/`
2. **Data:** Add `*Entity` in `data/local/entity/`, add DAO methods in `data/local/db/`, add `*RepositoryImpl` in `data/repository/` (or extend existing)
3. **DI:** Add `@Provides` in `di/AppModule.kt` for new DAOs / repositories
4. **UI:** Create package `ui/<feature>/` with `*Screen.kt`, `*ViewModel.kt`, `*UiState.kt`
5. **Navigation:** Add route in `Screen.kt`, register `composable` in `AppNavHost.kt`
6. **Theme/Components:** Add reusable UI components to `ui/components/` if needed
7. **Verify:** Run `./gradlew check` and fix any lint or ktlint issues before finishing

Example existing feature: `AddTransactionSheet` → `AddTransactionViewModel` → `AddTransactionUiState`

## 11. Testing

- **Current state:** Minimal. Only default `ExampleUnitTest` and `ExampleInstrumentedTest`.
- **Dependencies:** JUnit 4, Espresso, Compose UI test deps declared but unused.
- **No:** Room in-memory tests, Hilt test rules, coroutine test utilities, Turbine.
- **Ask the user** whether new features should include tests or if testing is out of scope.

## 12. Key Files to Read First

When modifying a feature, read these in order:
1. `ui/navigation/Screen.kt` — understand routes
2. `ui/navigation/AppNavHost.kt` — understand scaffold and nav structure
3. `di/AppModule.kt` — understand DI graph
4. `data/local/db/AppDatabase.kt` — understand entities and schema
5. The feature's `*Screen.kt`, `*ViewModel.kt`, `*UiState.kt`
6. Relevant `*Repository.kt` and `*RepositoryImpl.kt`
7. Relevant `*Dao.kt`

## 13. Custom Abstractions / Utils

- **`TransactionSearchResult`** — Room JOIN DTO for search queries
- **`DailyTotal`** — aggregation DTO (date + total amount)
- **`Converters`** — Room type converters for `UUID`, `LocalDate`, `BigDecimal`, `Instant`
- **`DonutSegment`** — chart model used by `DonutChart`
- **`CsvExporter` / `JsonExporter`** — write to `context.getExternalFilesDir(null)/ISpend/`
- **`SettingsDataStore`** — typed DataStore wrapper for theme prefs
- **`WidgetCategoryStore`** — widget category cache synced on category writes
- **No base ViewModel, no base Activity, no Result/Outcome sealed class**

## 14. Widget

- `QuickAddTransactionActivity` — Compose-based activity for app-widget quick-add
- Manually injects repositories (no HiltViewModel)
- `TransactionWidgetReceiver` + XML widget provider info
- Widget support is intentionally limited to quick-add expenses

## 15. Code Style / Lint

- **ktlint** — `org.jlleitschuh.gradle.ktlint` 14.2.0, configured in `app/build.gradle.kts`
- **`.editorconfig`** — project root, shared by ktlint and IDE
- Run `./gradlew ktlintCheck` to verify formatting
- Run `./gradlew ktlintFormat` to auto-fix formatting
- Run `./gradlew check` after any code change to verify lint, ktlint, and compilation pass
- **CI** — `.github/workflows/ci.yml` runs `./gradlew check` on every PR and push to `main`
- **detekt** — deferred until stable 2.0 supports Kotlin 2.2.10 + AGP 9.2.1
- Use standard Kotlin/Android formatting
- Follow existing patterns in the file you're editing

## 16. Common Patterns (Copy-Paste Ready)

### ViewModel Template
```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeatureUiState())
    val uiState: StateFlow<FeatureUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            transactionRepository.getAllTransactions()
                .collect { transactions ->
                    _uiState.value = _uiState.value.copy(transactions = transactions)
                }
        }
    }
}

data class FeatureUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
```

### Screen Template
```kotlin
@Composable
fun FeatureScreen(
    navController: NavHostController,
    viewModel: FeatureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // UI here
    }
}
```

### Repository Implementation Template
```kotlin
class FeatureRepositoryImpl @Inject constructor(
    private val featureDao: FeatureDao,
    private val categoryRepository: CategoryRepository
) : FeatureRepository {
    override fun getAll(): Flow<List<Feature>> =
        featureDao.getAll().map { list -> list.map { it.toDomain() } }
}
```

## 17. Open Questions to Ask the User

Before implementing, clarify these if they affect the task:

1. **Database migrations:** Should new schema changes use `Migration` objects or destructive migration?
2. **Error/loading states:** Keep boolean `isLoading` + `errorMessage: String?`, or introduce a sealed `Result`/`Outcome` class?
3. **Testing:** Should new features include ViewModel/repository tests (Turbine, coroutine-test), or is testing out of scope?
4. **Module split:** Should `:app` stay monolithic, or should large features move to Gradle feature modules?
5. **Widget parity:** When adding new entity types, should widget quick-add support be considered?

---

<!-- code-review-graph MCP tools -->
## MCP Tools: code-review-graph

**IMPORTANT: This project has a knowledge graph. ALWAYS use the
code-review-graph MCP tools BEFORE using Grep/Glob/Read to explore
the codebase.** The graph is faster, cheaper (fewer tokens), and gives
you structural context (callers, dependents, test coverage) that file
scanning cannot.

### When to use graph tools FIRST

- **Exploring code**: `semantic_search_nodes` or `query_graph` instead of Grep
- **Understanding impact**: `get_impact_radius` instead of manually tracing imports
- **Code review**: `detect_changes` + `get_review_context` instead of reading entire files
- **Finding relationships**: `query_graph` with callers_of/callees_of/imports_of/tests_for
- **Architecture questions**: `get_architecture_overview` + `list_communities`

Fall back to Grep/Glob/Read **only** when the graph doesn't cover what you need.

### Key Tools

| Tool | Use when |
| ------ | ---------- |
| `detect_changes` | Reviewing code changes — gives risk-scored analysis |
| `get_review_context` | Need source snippets for review — token-efficient |
| `get_impact_radius` | Understanding blast radius of a change |
| `get_affected_flows` | Finding which execution paths are impacted |
| `query_graph` | Tracing callers, callees, imports, tests, dependencies |
| `semantic_search_nodes` | Finding functions/classes by name or keyword |
| `get_architecture_overview` | Understanding high-level codebase structure |
| `refactor_tool` | Planning renames, finding dead code |

### Workflow

1. The graph auto-updates on file changes (via hooks).
2. Use `detect_changes` for code review.
3. Use `get_affected_flows` to understand impact.
4. Use `query_graph` pattern="tests_for" to check coverage.

<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan:
`specs/002-liquid-nav-redesign/plan.md`
<!-- SPECKIT END -->
