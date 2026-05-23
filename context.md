# Code Context — FinanceTrackingApp

## Files Retrieved
1. `app/build.gradle.kts` (full) — single `:app` module; defines tech stack (Compose BOM, Room KSP, Hilt, Navigation, DataStore, Serialization), compileSdk 36, minSdk 24, Java 11.
2. `gradle/libs.versions.toml` (full) — version catalog. Key versions: AGP 9.2.1, Kotlin 2.2.10, Room 2.7.1, Hilt 2.59.2, Navigation 2.9.0, Compose BOM 2026.02.01.
3. `app/src/main/java/com/financetracker/di/AppModule.kt` (full) — single Hilt module `@InstallIn(SingletonComponent)`. Provides DB, DAOs, and binds repository interfaces → impls.
4. `app/src/main/java/com/financetracker/data/local/db/AppDatabase.kt` (full) — Room DB v1, exportSchema false, 3 entities, `TypeConverters(Converters::class)`.
5. `app/src/main/java/com/financetracker/data/local/db/Converters.kt` (full) — converters for `UUID`, `LocalDate`, `BigDecimal`, `Instant`.
6. `app/src/main/java/com/financetracker/data/local/db/TransactionDao.kt` (full) — complex search queries returning `Flow<List<TransactionSearchResult>>` with JOINs; `DailyTotal` aggregation DTO.
7. `app/src/main/java/com/financetracker/domain/repository/TransactionRepository.kt` (full) — repository interface contract.
8. `app/src/main/java/com/financetracker/data/repository/TransactionRepositoryImpl.kt` (full) — maps `TransactionEntity` ↔ `Transaction`, uses `CategoryRepository` to hydrate category data.
9. `app/src/main/java/com/financetracker/ui/navigation/AppNavHost.kt` (full) — central navigation graph with `Scaffold`, conditional `TopBar`/`BottomBar`/`FAB`, `NavHost`, routes for all screens.
10. `app/src/main/java/com/financetracker/ui/navigation/Screen.kt` (full) — sealed class routes + `BottomNavItem` list.
11. `app/src/main/java/com/financetracker/ui/home/HomeScreen.kt` (full) — representative Screen composable collecting `uiState` via `collectAsState()`.
12. `app/src/main/java/com/financetracker/ui/home/HomeViewModel.kt` (full) — `HiltViewModel`, exposes `StateFlow<HomeUiState>`, combines repository flows in `init`.
13. `app/src/main/java/com/financetracker/ui/addtransaction/AddTransactionViewModel.kt` (full) — `HiltViewModel` pattern with mutable state updates.
14. `app/src/main/java/com/financetracker/ui/theme/Theme.kt` (lines 1–120) — supports Light, Dark, OLED themes with accent color picker; edge-to-edge via `enableEdgeToEdge()`.
15. `app/src/main/java/com/financetracker/MainActivity.kt` (full) — `AndroidEntryPoint`, seeds default categories on first launch, applies dynamic theme from `SettingsRepository`.
16. `app/src/main/java/com/financetracker/domain/model/Transaction.kt`, `Category.kt`, `Budget.kt` (full) — plain domain data classes using `UUID`, `BigDecimal`, `LocalDate`.
17. `app/src/main/java/com/financetracker/data/local/entity/TransactionEntity.kt`, `CategoryEntity.kt`, `BudgetEntity.kt` (full) — Room entities with `UUID` primary keys.
18. `app/src/main/java/com/financetracker/data/local/prefs/SettingsDataStore.kt` (full) — DataStore preferences for theme mode and accent color.
19. `app/src/main/java/com/financetracker/ui/components/TransactionCard.kt` (lines 1–60) — reusable component with many customization params (`useCard`, `showDate`, `onDelete`, etc.).
20. `app/src/main/java/com/financetracker/ui/search/SearchViewModel.kt` (full) — demonstrates `Flow` debounce, `flatMapLatest`, and `combine` patterns.

## Key Code

### Tech Stack
- **Language:** Kotlin 2.2.10 (Java 11 target)
- **UI:** Jetpack Compose (BOM 2026.02.01), Material3, `androidx.activity:activity-compose`, `androidx.hilt:hilt-navigation-compose`
- **Database:** Room 2.7.1 with KSP compiler, `room-ktx`
- **DI:** Dagger Hilt 2.59.2 (`@HiltAndroidApp`, `@HiltViewModel`, `hiltViewModel()`)
- **Navigation:** Navigation Compose 2.9.0
- **Preferences:** DataStore Preferences 1.1.6
- **Serialization:** Kotlinx Serialization JSON 1.8.1
- **Build:** Gradle with version catalogs, AGP 9.2.1, single `:app` module

### Architecture Pattern
Clean Architecture-lite with MVVM:
- `domain/` — pure Kotlin models (`Transaction`, `Category`, `Budget`), repository interfaces, use cases (`*UseCase` with `operator fun invoke()`)
  - `SearchTransactionsUseCase` — orchestrates search query, date filter, category filter via reactive `Flow`
  - `GetMonthlySummaryUseCase` — returns `MonthlySummary` with pure `CategoryBreakdown` list (no Compose models)
  - `CalculateBudgetProgressUseCase` — returns `Flow<List<BudgetProgress>>` with enriched category metadata
- `data/` — Room entities (`*Entity`), DAOs (`*Dao`), repository implementations (`*RepositoryImpl`), local prefs, exporters
- `ui/` — feature packages (`ui/<feature>/`) containing `*Screen.kt`, `*ViewModel.kt`, and `*UiState` data class; shared components in `ui/components/`
- `di/` — single `AppModule.kt`

### Naming Conventions
| Layer | Pattern | Example |
|-------|---------|---------|
| Domain model | plain data class | `Transaction` |
| Entity | `*Entity` | `TransactionEntity` |
| DAO | `*Dao` interface | `TransactionDao` |
| Repository interface | `*Repository` | `TransactionRepository` |
| Repository impl | `*RepositoryImpl` | `TransactionRepositoryImpl` |
| Use case | `*UseCase` | `GetMonthlySummaryUseCase` |
| Screen | `*Screen` or `*Sheet` composable | `HomeScreen`, `AddTransactionSheet` |
| ViewModel | `*ViewModel` | `HomeViewModel` |
| UI State | `*UiState` data class | `HomeUiState` |
| Route | `Screen.*` sealed object | `Screen.Home` |

### Dependency Injection
Single module:
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun provideDatabase(...): AppDatabase
    @Provides fun provideTransactionDao(db: AppDatabase): TransactionDao
    @Provides @Singleton fun provideTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository = impl
    // ... same for Category, Budget, Settings
}
```
ViewModels:
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() { ... }
```
Screens inject ViewModels:
```kotlin
@Composable
fun HomeScreen(..., viewModel: HomeViewModel = hiltViewModel()) { ... }
```

### Database Schema
- **transactions** — `id: UUID` PK, `amount: BigDecimal`, `note: String`, `date: LocalDate`, `categoryId: UUID`, `createdAt: Instant`
- **categories** — `id: UUID` PK, `name`, `emoji`, `colorHex`, `isDefault`, `sortOrder`
- **budgets** — `id: UUID` PK, `categoryId: UUID?` (null = total budget), `yearMonth: String`, `limitAmount: BigDecimal`
- **Converters** — `UUID↔String`, `LocalDate↔String`, `BigDecimal↔String`, `Instant↔Long`
- **Search DTO** — `TransactionSearchResult` joins `transactions` + `categories` for query results
- **Aggregation DTO** — `DailyTotal(date, total)`

### UI Patterns
- Each screen owns a `*UiState` data class and a `StateFlow<*UiState>` in the ViewModel.
- Screens collect with `val uiState by viewModel.uiState.collectAsState()`.
- State mutations happen in ViewModel via `_uiState.value = _uiState.value.copy(...)`. No shared MVI or event bus.
- `AppNavHost` hosts `Scaffold`, `NavHost`, conditional top/bottom bars, and FAB. All navigation routes defined in `Screen.kt`.
- Add transaction is a bottom sheet overlay (`AddTransactionSheet`) launched as a destination.
- Edge-to-edge enabled in `MainActivity`.
- Custom reusable components: `TransactionCard`, `DonutChart`, `DonutLegend`, `BarChart`.

### Navigation Structure
Bottom nav items: Home, Analytics, Search, Settings.
Sub-screens (top bar + back): History, Categories, Budget, Calendar.
Overlay: AddTransaction (`add_transaction` / `add_transaction/{transactionId}`).
All navigation is string-based via `Screen.route` with `navArgument` for UUID editing.

### Testing Setup
Minimal. Only default example tests exist:
- `app/src/test/java/.../ExampleUnitTest.kt` (JUnit 4)
- `app/src/androidTest/java/.../ExampleInstrumentedTest.kt` (Espresso + Compose UI test deps declared but unused)
No Room in-memory tests, no Hilt test rules, no coroutine test utilities.

### Build System
- Gradle with `gradle/libs.versions.toml` version catalog.
- Plugins applied: `android.application`, `kotlin.compose`, `kotlin.serialization`, `ksp`, `hilt`.
- No custom ProGuard rules; `isMinifyEnabled = false`.

### Custom Abstractions / Utils
- **`TransactionSearchResult`** — Room query DTO for search JOINs.
- **`DailyTotal`** — aggregation query result.
- **`Converters`** — Room type converters for `UUID`, `LocalDate`, `BigDecimal`, `Instant`.
- **`DonutSegment`** — chart model shared between domain/UI layers.
- **`CsvExporter` / `JsonExporter`** — injected singletons writing to `context.getExternalFilesDir(null)/ISpend/`.
- **`SettingsDataStore`** — typed DataStore wrapper for theme prefs.
- **`WidgetCategoryStore`** — widget category cache (used by `CategoryRepositoryImpl` to sync on write).
- **`SearchCriteria`** — value object bundling search query, selected category IDs, and date filter.
- **`DateFilter`** — sealed class for date filtering: `None`, `Quick` (chip-based), or `Custom` range.
- **`QuickChip`** — enum of preset date ranges (`TODAY`, `LAST_7_DAYS`, etc.) with `calculateRange(today)`.
- **`CategoryBreakdown`** — pure domain model for category spending analysis (name, emoji, amount, colorHex).
- **`TimeProvider`** — seam for time; `SystemTimeProvider` production impl, `FakeTimeProvider` in tests.
- No base ViewModel, no base Activity/Fragment, no Result/Outcome sealed class.

### Code Style / Lint
- No detekt, ktlint, or `.editorconfig` found.
- No custom lint configuration.
- Standard Kotlin/Android formatting assumed.

### Feature Structure (end-to-end example: Add Transaction)
1. **Domain:** `domain/model/Transaction.kt` + `domain/repository/TransactionRepository.kt`
2. **Data:** `data/local/entity/TransactionEntity.kt` + `data/local/db/TransactionDao.kt` + `data/repository/TransactionRepositoryImpl.kt`
3. **UI:** `ui/addtransaction/AddTransactionSheet.kt` + `ui/addtransaction/AddTransactionViewModel.kt` + `AddTransactionUiState`
4. **Navigation:** Add route in `Screen.kt`, register `composable(...)` in `AppNavHost.kt`
5. **DI:** Already covered by `AppModule` (generic DAO/repository binds); nothing new needed per feature unless new interfaces.

### Widget / Extra
- `QuickAddTransactionActivity` — Compose-based activity for widget quick-add, manually injecting repos (no ViewModel).
- `TransactionWidgetReceiver` + XML widget info.
- Theme supports OLED black mode and 6 accent colors.

---

## Remaining Questions (3–5)
1. **Database migrations:** Room schema is v1 with `exportSchema = false`. Is the expectation to bump version and write `Migration` objects, or rely on destructive migration during development?
2. **Error / loading state pattern:** Most ViewModels set `isLoading = false` on success. Is there a standard sealed class (`Result`, `Outcome`) planned, or should new features keep the boolean-flag + optional `errorMessage: String?` pattern?
3. **Testing baseline:** The project currently has no real tests. Should new features include ViewModel tests (e.g., Turbine + coroutine-test), or is testing out of scope for now?
4. **Module split:** Is the `:app` module expected to remain monolithic, or should new large features eventually move to their own Gradle feature modules?
5. **Widget parity:** When adding a new entity type (e.g., recurring transactions), should widget integration be considered by default, or is widget support intentionally limited to quick-add expenses only?
