# Code Context

## Architecture vocabulary

- **NavigationCoordinator** — Hilt ViewModel that owns navigation chrome policy and emits typed navigation targets. Screens consume its `ChromeState`; `AppNavHost` adapts `NavigationTarget`s to the `NavController`.
- **Destination** — one data object describing a screen route, chrome visibility, bottom nav icons, and title resource.
- **NavigationTarget** — sealed class representing a navigation intent (`Back`, `AddTransaction`, `EditTransaction`, `Budget`, `History`). Screens emit these; the coordinator forwards them.

## Files Retrieved

### Screens audited (full files read)
1. `app/src/main/java/com/financetracker/ui/home/HomeScreen.kt` (lines 1‑149) — LazyColumn with budget, donut, and recent activity header + transaction list.
2. `app/src/main/java/com/financetracker/ui/analytics/AnalyticsScreen.kt` (lines 1‑67) — Almost entirely component calls; no significant inline blocks.
3. `app/src/main/java/com/financetracker/ui/search/SearchScreen.kt` (lines 1‑186) — Search text field, date/category filter rows, result list.
4. `app/src/main/java/com/financetracker/ui/budget/BudgetScreen.kt` (lines 1‑111) — Total budget editor inside `SectionCard`, category slider list, recalc FAB.
5. `app/src/main/java/com/financetracker/ui/history/HistoryScreen.kt` (lines 1‑93) — Month navigator + sticky date-group headers + transactions.
6. `app/src/main/java/com/financetracker/ui/calendar/CalendarScreen.kt` (lines 1‑118) — Weekday header, calendar grid with offset, heatmap legend, selected day detail.
7. `app/src/main/java/com/financetracker/ui/categories/CategoriesScreen.kt` (lines 1‑89) — `CategoryCard` list + dialogs/sheets.
8. `app/src/main/java/com/financetracker/ui/settings/SettingsScreen.kt` (lines 1‑144) — `SettingsCard` wrappers with inline buttons.
9. `app/src/main/java/com/financetracker/ui/addtransaction/AddTransactionSheet.kt` (lines 1‑158) — Bottom-sheet form with inline title, category FlowRow, note input, save button.

### Existing components referenced (all in `app/src/main/java/com/financetracker/ui/components/`)
- `AccentColorPicker.kt`
- `AmountInput.kt`
- `BarChart.kt`
- `BudgetSummaryCard.kt`
- `CategoryBreakdownCard.kt`
- `CategoryBudgetIndicatorRow.kt`
- `CategoryBudgetSliderCard.kt`
- `CategoryCard.kt`
- `CategoryChip.kt`
- `CategoryDialog.kt`
- `CategoryIcon.kt`
- `CategoryIconPickerSheet.kt`
- `CircularProgressLabel.kt`
- `DateRangePicker.kt`
- `DateSelectorRow.kt`
- `DayCell.kt`
- `DayDetailCard.kt`
- `DonutChart.kt`
- `EmptyState.kt`
- `FilterChipGroup.kt`
- `MonthNavigator.kt`
- `PresetAmountChips.kt`
- `RememberIconStyle.kt`
- `ResetDataDialog.kt`
- `SectionCard.kt`
- `SettingsCard.kt`
- `StatBox.kt` (contains `StatBox` + `StatBoxRow`)
- `TransactionCard.kt`
- `WeekdayBarChartCard.kt`

---

## Key Code

### HomeScreen — inline "Recent Activity" header block (candidate)
```kotlin
// HomeScreen.kt lines 96-120
item(key = "recent_header") {
    val headerShape = if (uiState.recentTransactions.isEmpty()) {
        RoundedCornerShape(12.dp)
    } else {
        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(recentColor, shape = headerShape)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Recent Activity", style = MaterialTheme.typography.titleSmall)
        Text("History", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
    }
}
```
- ~24 lines. Only used in Home. Could be `RecentActivityHeader`.

### SearchScreen — search OutlinedTextField block (candidate)
```kotlin
// SearchScreen.kt lines 59-74
OutlinedTextField(
    value = uiState.query,
    onValueChange = viewModel::onQueryChanged,
    modifier = Modifier.fillMaxWidth().focusRequester(searchFocusRequester),
    placeholder = { Text("Search expenses...") },
    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    trailingIcon = {
        if (uiState.query.isNotEmpty()) {
            IconButton(onClick = viewModel::clearSearch) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
        }
    },
    singleLine = true,
    shape = RoundedCornerShape(16.dp)
)
```
- ~16 lines. Only used in SearchScreen. Single-use but dense.

### SearchScreen — date range filter row (candidate)
```kotlin
// SearchScreen.kt lines 85-132
Row(...) {
    val allChips = QuickChip.entries.toList() + "Custom"
    FilterChipGroup(...)
    if (uiState.dateFilter != DateFilter.None) {
        IconButton(onClick = viewModel::clearDateFilter) { ... }
    }
}
```
- ~48 lines. Contains logic for mixed-type chip list + clear action. Only used here.

### BudgetScreen — total budget editor block inside SectionCard (candidate)
```kotlin
// BudgetScreen.kt lines 66-81
AmountInput(...)
Spacer(modifier = Modifier.height(8.dp))
Button(
    onClick = { totalInput.toBigDecimalOrNull()?.let { viewModel.setTotalBudget(it) } },
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp)
) { Text("Save Total Budget") }
```
- ~16 lines. Only used in BudgetScreen.

### HistoryScreen — sticky date-group header (candidate)
```kotlin
// HistoryScreen.kt lines 56-67
stickyHeader(key = group.date.toString()) {
    Text(
        text = group.label,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    )
}
```
- ~12 lines. Repeated per group inside LazyColumn. Only used in HistoryScreen.

### CalendarScreen — weekday header row (candidate)
```kotlin
// CalendarScreen.kt lines 58-68
Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
    listOf("S","M","T","W","T","F","S").forEach {
        Text(it, style = MaterialTheme.typography.labelSmall, ...)
    }
}
```
- ~11 lines. Only used in CalendarScreen.

### CalendarScreen — heatmap legend row (candidate)
```kotlin
// CalendarScreen.kt lines 103-109
Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
    Text("Less", style = MaterialTheme.typography.labelSmall)
    HEAT_COLORS.forEach { color ->
        Box(modifier = Modifier.size(16.dp).padding(2.dp).background(color, RoundedCornerShape(2.dp)))
    }
    Text("More", style = MaterialTheme.typography.labelSmall)
}
```
- ~7 lines. Only used in CalendarScreen.

### SettingsScreen — inline navigation/export buttons inside SettingsCards (candidates)
```kotlin
// SettingsScreen.kt lines 78-124
// History button (5 lines)
// Budget button (5 lines)
// Export CSV/JSON buttons (12 lines)
// Reset button (4 lines)
```
- Each block is trivial (<6 lines) and only used here.

### AddTransactionSheet — inline title + category selector + note + save button (candidates)
```kotlin
// AddTransactionSheet.kt lines 85-155
// Title Text (5 lines)
// "Category" label + FlowRow of CategoryChip (18 lines)
// OutlinedTextField for note (7 lines)
// Error Text (7 lines)
// Save Button (15 lines)
```
- Title and save button are single-use but styled. Category selector could be `CategoryChipGroup`.

---

## Architecture

- **UI layer:** Each screen is a single `@Composable` in its own package. Screens hold `LazyColumn`/`Column` scaffolding and wire components to ViewModel events.
- **Reusable components live in `ui/components/`**. They are imported directly; no wrapper module.
- **State flow:** `ViewModel` exposes `StateFlow<*UiState>` → screen collects via `collectAsState()` → passes lambdas down to components.
- **Navigation:** Screens receive `onNavigate*` or `onEditTransaction` lambdas from `AppNavHost`. Components never touch `NavController`.
- **Theming:** `MaterialTheme` + custom `ChartColors`. Inline styles reference theme directly.

---

## Per-Screen Audit

### HomeScreen
**Already extracted:** `EmptyState`, `BudgetSummaryCard`, `CategoryBudgetIndicatorRow`, `CategoryBreakdownCard`, `TransactionCard`.
**Inline blocks worth noting:**
- `Recent Activity` header Row (lines 96‑120, ~24 lines). Could become `RecentActivityHeader`.
- Transaction item shape logic (lines 126‑142) is just a `RoundedCornerShape` wrapper around `TransactionCard`. Not worth extracting.
**Already-exists match:** The header+list pattern is similar to a generic `ListSectionCard`, but `SectionCard` already exists and is used in Budget/Settings.

### AnalyticsScreen
**Already extracted:** `FilterChipGroup`, `StatBoxRow`, `CategoryBreakdownCard`, `WeekdayBarChartCard`.
**Inline blocks:** None significant; screen is pure glue.

### SearchScreen
**Already extracted:** `DateRangePicker`, `EmptyState`, `FilterChipGroup`, `TransactionCard`.
**Inline blocks worth noting:**
- Search `OutlinedTextField` with focus requester + clear icon (lines 59‑74, ~16 lines). Could be `SearchTextField`.
- Date-range filter Row (lines 85‑132, ~48 lines). Logic-heavy (mixed `QuickChip` + `"Custom"` string). Could be `DateFilterChipRow`, but coupling to `SearchViewModel` is tight.
- Category `FilterChipGroup` block (lines 136‑142) is trivial.
**Already-exists match:** `FilterChipGroup` already covers the chip rendering; the surrounding Row and clear button are extra.

### BudgetScreen
**Already extracted:** `AmountInput`, `CategoryBudgetSliderCard`, `SectionCard`.
**Inline blocks worth noting:**
- Total budget editor inside `SectionCard` (lines 66‑81, ~16 lines). Could be `TotalBudgetEditor`.
- `FloatingActionButton` for recalc (lines 100‑109) is trivial.
**Already-exists match:** `SectionCard` already provides the card wrapper.

### HistoryScreen
**Already extracted:** `MonthNavigator`, `TransactionCard`, `EmptyState`.
**Inline blocks worth noting:**
- Sticky date-group header (lines 56‑67, ~12 lines). Could become `DateGroupHeader`.
- `LazyColumn` grouping logic (lines 55‑80) is screen-level; extracting would require passing `Map` of groups.

### CalendarScreen
**Already extracted:** `MonthNavigator`, `DayCell`, `DayDetailCard`.
**Inline blocks worth noting:**
- Weekday header Row (lines 58‑68, ~11 lines). Could become `WeekdayHeader`.
- Calendar grid offset + DayCell items (lines 76‑98, ~22 lines). Could become `CalendarGrid`, but highly specialized.
- Heatmap legend Row (lines 103‑109, ~7 lines). Could become `HeatmapLegend`.
- Selected day detail block (lines 113‑116) is just a conditional `DayDetailCard`.

### CategoriesScreen
**Already extracted:** `CategoryCard`, `CategoryDialog`, `CategoryIconPickerSheet`.
**Inline blocks:** None; screen is list + dialogs.

### SettingsScreen
**Already extracted:** `AccentColorPicker`, `FilterChipGroup`, `ResetDataDialog`, `SettingsCard`.
**Inline blocks worth noting:**
- Navigation/export buttons inside `SettingsCard` (lines 78‑124). Each is 4‑6 lines, single-use, trivial.
- Message `Text` (lines 126‑132, ~7 lines). Could be inline snackbar text.
**Already-exists match:** `SettingsCard` already wraps each section.

### AddTransactionSheet
**Already extracted:** `AmountInput`, `CategoryChip`, `DateRangePicker`, `DateSelectorRow`.
**Inline blocks worth noting:**
- Title `Text` (lines 85‑89, ~5 lines).
- Category label + `FlowRow` of `CategoryChip`s (lines 98‑116, ~18 lines). Could become `CategoryChipGroup`.
- Note `OutlinedTextField` (lines 118‑124, ~7 lines).
- Error `Text` (lines 132‑138, ~7 lines).
- Save `Button` (lines 140‑155, ~15 lines). Could become `PrimaryActionButton` or `SaveButton`.

---

## Summary

### Already done (from existing components)
- Most heavy UI already extracted: `TransactionCard`, `CategoryBreakdownCard`, `BudgetSummaryCard`, `CategoryBudgetSliderCard`, `StatBoxRow`, `WeekdayBarChartCard`, `MonthNavigator`, `DayCell`, `DayDetailCard`, `EmptyState`, `AmountInput`, `DateSelectorRow`, `FilterChipGroup`, `CategoryChip`, `CategoryCard`, `CategoryDialog`, `SettingsCard`, `SectionCard`, `AccentColorPicker`, `ResetDataDialog`, `DateRangePicker`.

### Still inline and worth extracting
| Candidate | Location | Approx Lines | Rationale |
|-----------|----------|--------------|-----------|
| `RecentActivityHeader` | `HomeScreen.kt` 96‑120 | 24 | Distinct visual block; shape logic depends on empty state but could be parameterized. |
| `SearchTextField` | `SearchScreen.kt` 59‑74 | 16 | Search field with clear icon and focus logic. Could be reused if search expands. |
| `DateFilterChipRow` | `SearchScreen.kt` 85‑132 | 48 | Complex Row wrapping `FilterChipGroup` + clear button + mixed type chips. Tight to Search but reduces screen size. |
| `TotalBudgetEditor` | `BudgetScreen.kt` 66‑81 | 16 | `AmountInput` + save button inside a section. Could encapsulate local input state. |
| `DateGroupHeader` | `HistoryScreen.kt` 56‑67 | 12 | Reused for every sticky header in History. |
| `WeekdayHeader` | `CalendarScreen.kt` 58‑68 | 11 | Static letters row. Reusable for any calendar view. |
| `CalendarGrid` | `CalendarScreen.kt` 76‑98 | 22 | Offset empty cells + DayCell items. High cohesion. |
| `HeatmapLegend` | `CalendarScreen.kt` 103‑109 | 7 | Small but visually distinct. |
| `CategoryChipGroup` | `AddTransactionSheet.kt` 98‑116 | 18 | Label + FlowRow of `CategoryChip`s. Used only here but could be reused for filters. |
| `ErrorText` | `AddTransactionSheet.kt` 132‑138 | 7 | Conditional error text with theme color. Very small; marginal value. |
| `PrimarySaveButton` | `AddTransactionSheet.kt` 140‑155 | 15 | Styled save button with conditional label. Could be generic `PrimaryButton` if shape/height match app-wide. |

### NOT worth extracting (single-use / trivial)
- Transaction shape wrapper in HomeScreen (2‑3 lines of shape logic).
- FAB recalc button in BudgetScreen (standard `FloatingActionButton`).
- Navigation/export buttons in SettingsScreen (4‑6 lines each, just `Button` + `Text`).
- Title Text in AddTransactionSheet (5 lines, single-use).
- Note `OutlinedTextField` in AddTransactionSheet (7 lines, single-use).
- Message Text in SettingsScreen (single-use).

### Recommended next steps with file names
1. **HomeScreen** → extract `ui/components/RecentActivityHeader.kt` (parameterize `onHistoryClick`, `isEmpty`, `backgroundColor`).
2. **SearchScreen** → extract `ui/components/SearchTextField.kt` (wrap `OutlinedTextField` + clear + focus). Then optionally `ui/components/DateFilterChipRow.kt`.
3. **BudgetScreen** → extract `ui/components/TotalBudgetEditor.kt` (hold local `totalInput` state inside component).
4. **HistoryScreen** → extract `ui/components/DateGroupHeader.kt` (parameterize `label`).
5. **CalendarScreen** → extract `ui/components/WeekdayHeader.kt` and `ui/components/HeatmapLegend.kt`. Optionally `ui/components/CalendarGrid.kt` if calendar is reused elsewhere.
6. **AddTransactionSheet** → extract `ui/components/CategoryChipGroup.kt` (label + FlowRow). Optionally `ui/components/PrimarySaveButton.kt` if same style used on other screens (e.g., login/sign-up later).

---

## Start Here

Open **`app/src/main/java/com/financetracker/ui/home/HomeScreen.kt`** first because:
- It contains the largest remaining inline visual block (the `Recent Activity` header).
- It also shows how `TransactionCard` is already parameterized (`useCard`, `iconSize`, `showDate`, etc.), giving a pattern for further extraction.
