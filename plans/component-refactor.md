# Component Refactor Plan

**Goal:** Extract 23+ private composables into reusable `ui/components/` with `@Preview` and `PreviewData`.

## Files to create

| Tier | File | Origin | Purpose |
|------|------|--------|---------|
| Preview | `PreviewData.kt` | — | Dummy domain objects for every preview |
| 1 | `EmptyState.kt` | Home, Search, History | Icon + title + subtitle placeholder |
| 1 | `MonthNavigator.kt` | History, Calendar | Row with chevrons + month label |
| 1 | `FilterChipGroup.kt` | Search, Analytics, Settings | Generic selectable chip list |
| 1 | `AmountInput.kt` | AddTransaction, Budget | OutlinedTextField with `$` prefix + decimal keyboard |
| 1 | `CategoryChip.kt` | AddTransactionSheet | Selected/unselected category chip |
| 1 | `CircularProgressLabel.kt` | Home | Ring with center text (budget + category) |
| 1 | `DateSelectorRow.kt` | AddTransactionSheet | Tappable bordered date row with calendar icon |
| 2 | `BudgetSummaryCard.kt` | HomeScreen | Card wrapping CircularProgressLabel + remaining budget |
| 2 | `CategoryBudgetIndicatorRow.kt` | HomeScreen | LazyRow of category rings |
| 2 | `StatBox.kt` / `StatBoxRow.kt` | AnalyticsScreen | Summary stat cards row |
| 2 | `CategoryBreakdownCard.kt` | AnalyticsScreen | Card with DonutChart + DonutLegend |
| 2 | `WeekdayBarChartCard.kt` | AnalyticsScreen | Card with BarChart |
| 2 | `CategoryBudgetSliderCard.kt` | BudgetScreen | Expandable card: slider, amount input, presets, save button |
| 2 | `PresetAmountChips.kt` | BudgetScreen | Row of SuggestionChip for quick amounts |
| 2 | `DayCell.kt` | CalendarScreen | Heatmap calendar day cell |
| 2 | `DayDetailCard.kt` | CalendarScreen | Selected day summary + transaction list |
| 2 | `CategoryCard.kt` | CategoriesScreen | Card with linear progress + edit/delete actions |
| 2 | `CategoryDialog.kt` | CategoriesScreen | Unified add/edit AlertDialog for categories |
| 2 | `AccentColorPicker.kt` | SettingsScreen | Selectable accent color circles grid |
| 2 | `SettingsCard.kt` | SettingsScreen | Generic titled Card wrapper (reduces boilerplate) |
| 2 | `ResetDataDialog.kt` | SettingsScreen | Destructive confirmation dialog |
| 3 | `DateRangePicker.kt` | SearchScreen | Start+end DatePickerDialog orchestration |

## Screens to refactor

- `HomeScreen.kt`
- `AnalyticsScreen.kt`
- `SearchScreen.kt`
- `BudgetScreen.kt`
- `HistoryScreen.kt`
- `CalendarScreen.kt`
- `CategoriesScreen.kt`
- `SettingsScreen.kt`
- `AddTransactionSheet.kt`

## Execution order

1. Create `PreviewData.kt`.
2. Build Tier 1 components (7 files) with `@Preview`.
3. Build Tier 2 components (15 files) with `@Preview`.
4. Build `DateRangePicker`.
5. Refactor screens: delete private composables, import new components, wire callbacks.
6. Run `./gradlew ktlintFormat`.
7. Verify build compiles.

## Notes

- All components public, placed in `ui/components/`.
- `SettingsCard` signature: `title: String`, `content: @Composable ColumnScope.() -> Unit`.
- No tests (out of scope per user request).
- Caveman mode stays on for implementation replies.
