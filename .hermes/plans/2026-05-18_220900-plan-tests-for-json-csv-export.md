# Plan: Add Unit Tests for JSON and CSV Export Serialization

## Goal
Add JVM unit tests for `JsonExporter` and `CsvExporter` that verify:
- Correct content structure (JSON array/objects, CSV header/rows).
- Proper escaping of special characters (quotes, backslashes, newlines, commas).
- Edge cases (empty transaction list, single item, multiple items, negative amounts, empty notes, unicode).

## Current Context / Assumptions
- `JsonExporter` and `CsvExporter` live in `app/src/main/java/com/financetracker/data/export/`.
- Both depend on `android.content.Context` (via `@ApplicationContext`) to resolve the output directory (`getExternalFilesDir(null)/ISpend/`).
- Both build the output format manually (`StringBuilder` for JSON, `FileWriter` for CSV).
- `Transaction` and `Category` are plain Kotlin data classes with `UUID`, `BigDecimal`, `LocalDate`.
- The project currently has **only JUnit 4** in `testImplementation`. No mocking or Android test utilities are present.
- **User explicitly requested tests; testing is in scope for this work** (AGENTS.md §11).
- Transaction fields not exported: id, createdAt. Category fields not exported: id, colorHex, isDefault, sortOrder. These are intentional omissions.

## Proposed Approach
Use **Option B** — extract internal pure `String`-building functions from `JsonExporter` and `CsvExporter`, then test those with plain JUnit 4. The existing `export()` methods delegate to the new internal functions and handle file I/O / `Context` interaction.

**Why pure-function extraction over Robolectric?**
- **No new dependencies.** Plain JUnit 4 is already available; no test-only dependency downloads.
- **Fast and deterministic.** String assertions run in milliseconds without a shadow Android framework.
- **Scope-minimal.** Each exporter gets one new `internal fun` that the existing `export()` calls. No behavior change.
- **Tests exercise real logic.** The string-building and escaping is what matters; the `File.writeText()` / `FileWriter` shell is trivial Java I/O.

**Refactor footprint per exporter:**
- `JsonExporter.kt`: extract `internal fun buildJsonString(transactions: List<Transaction>): String` inside `companion object`
- `CsvExporter.kt`: extract `internal fun buildCsvContent(transactions: List<Transaction>): String` inside `companion object`

## Production Code Changes Required
Both exporters have escaping bugs that will produce invalid JSON or corrupt CSV. These must be fixed before tests pass.

| Bug | Current behavior | Required fix |
|-----|-----------------|--------------|
| **JSON control-character escaping** | `note.replace("\"", "\\\")` only escapes `"` | Also escape `\n` → `\\n`, `\\` → `\\\\`, `\r` → `\\r`, `\t` → `\\t`. Apply to `category.name`, `category.emoji`, and `note`. |
| **CSV category field escaping** | Only `note` is wrapped in quotes; `category.name` and `category.emoji` are raw CSV values | Wrap `category.name`, `category.emoji`, and `note` in quotes with inner quotes doubled, unconditionally (same rule as current note handling). |

## Step-by-step Plan

### 1. Extract pure functions in JsonExporter.kt
File: `app/src/main/java/com/financetracker/data/export/JsonExporter.kt`

- Extract `StringBuilder` logic into `companion object`:
  ```kotlin
  companion object {
      internal fun buildJsonString(transactions: List<Transaction>): String {
          val json = StringBuilder()
          json.appendLine("[")
          transactions.forEachIndexed { i, txn ->
              json.append("  {")
              json.append("\"date\": \"${txn.date}\", ")
              json.append("\"category\": \"${escapeJson(txn.category.name)}\", ")
              json.append("\"emoji\": \"${escapeJson(txn.category.emoji)}\", ")
              json.append("\"amount\": ${txn.amount}, ")
              json.append("\"note\": \"${escapeJson(txn.note)}\"")
              json.append("}")
              if (i < transactions.size - 1) json.appendLine(",") else json.appendLine()
          }
          json.appendLine("]")
          return json.toString()
      }

      private fun escapeJson(value: String): String =
          value
              .replace("\\", "\\\\")
              .replace("\"", "\\\"")
              .replace("\n", "\\n")
              .replace("\r", "\\r")
              .replace("\t", "\\t")
  }
  ```
- Make `export()` call `buildJsonString()` and write the returned string to the file.

### 2. Extract pure functions in CsvExporter.kt
File: `app/src/main/java/com/financetracker/data/export/CsvExporter.kt`

- Extract file-writing logic into `companion object`:
  ```kotlin
  companion object {
      internal fun buildCsvContent(transactions: List<Transaction>): String {
          val csv = StringBuilder()
          csv.appendLine("Date,Category,Emoji,Amount,Note")
          transactions.forEach { txn ->
              csv.appendLine(
                  "${txn.date},${csvField(txn.category.name)},${csvField(txn.category.emoji)},${txn.amount},${csvField(txn.note)}"
              )
          }
          return csv.toString()
      }

      private fun csvField(value: String): String =
          "\"${value.replace("\"", "\"\"")}\""
  }
  ```
- Make `export()` call `buildCsvContent()` and write the returned string to the file.

### 3. Prepare test source directories
- Ensure `app/src/test/java/com/financetracker/data/export/` exists.

### 4. Write shared fixture file
File: `app/src/test/java/com/financetracker/data/export/TransactionFixtures.kt`

```kotlin
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

object TransactionFixtures {
    fun cat(
        name: String = "Food",
        emoji: String = "🍔"
    ) = Category(
        id = UUID.fromString("00000000-0000-0000-0000-000000000001"),
        name = name,
        emoji = emoji,
        colorHex = "#FF5722",
        isDefault = false,
        sortOrder = 0
    )

    fun txn(
        amount: String,
        note: String = "",
        date: LocalDate = LocalDate.of(2026, 5, 18),
        category: Category = cat()
    ) = Transaction(
        id = UUID.fromString("00000000-0000-0000-0000-000000000000"),
        amount = BigDecimal(amount),
        note = note,
        date = date,
        category = category,
        createdAt = 0L
    )
}
```

### 5. Write `JsonExporterTest.kt`
File: `app/src/test/java/com/financetracker/data/export/JsonExporterTest.kt`

All tests call `JsonExporter.Companion.buildJsonString(...)` directly (no `Context`, no file I/O).
Tests verify content via string assertions (`contains`, `startsWith`, etc.) on the pure function output. JSON parseability is implicitly validated by verifying expected field patterns.

Import from shared fixture:
```kotlin
import com.financetracker.data.export.TransactionFixtures.cat
import com.financetracker.data.export.TransactionFixtures.txn
```

Tests to include:

| Test name | Scenario |
|---|---|
| `export empty list creates valid JSON array` | Empty list → parseable JSON array `[]` |
| `export single transaction outputs correct fields` | One transaction → JSON object contains `date`, `category`, `emoji`, `amount`, `note` |
| `export multiple transactions has comma between objects` | Two transactions → objects separated by `,` and newline |
| `export category name with quote is escaped` | Category name `Salary "Net"` → `category` field reads `Salary \"Net\"` |
| `export note with double quotes escapes quotes` | Note contains `"` → escaped as `\"` in JSON |
| `export note with backslash escapes backslash` | Note contains `\` → escaped as `\\` |
| `export note with newline escapes newline` | Note contains `\n` → escaped as `\\n` |
| `export note with carriage return escapes carriage return` | Note contains `\r` → escaped as `\\r` |
| `export note with tab escapes tab` | Note contains `\t` → escaped as `\\t` |
| `export amount preserves decimal scale` | Amount `10.50` → appears as `10.50` (not `10.5`) |
| `export negative amount preserves negative sign` | Amount `-50.00` → appears as `-50.00` |
| `export empty note outputs empty string` | Note `""` → `"note": ""` in JSON |
| `export note with unicode preserves characters` | Note contains emoji/unicode → preserved exactly |

### 6. Write `CsvExporterTest.kt`
File: `app/src/test/java/com/financetracker/data/export/CsvExporterTest.kt`

All tests call `CsvExporter.Companion.buildCsvContent(...)` directly.

Import from shared fixture:
```kotlin
import com.financetracker.data.export.TransactionFixtures.cat
import com.financetracker.data.export.TransactionFixtures.txn
```

Tests to include:

| Test name | Scenario |
|---|---|
| `export empty list outputs header only` | Empty list → single line `Date,Category,Emoji,Amount,Note` |
| `export single transaction outputs correct row` | One transaction → header + 1 row with correct columns |
| `export note with quotes escapes quotes` | Note contains `"` → CSV wraps in quotes and doubles inner quotes (`""`) |
| `export note with commas preserved inside quotes` | Note contains `,` → wrapped in `"..."` and comma preserved |
| `export note with newline is quoted` | Note contains `\n` → wrapped in `"..."` in CSV row |
| `export category name with comma is quoted` | Category name `"Food, Drinks"` → quoted in CSV column |
| `export category name with quote is escaped` | Category name `"Salary \"Net\""` → quotes doubled in quoted field |
| `export category emoji with comma is quoted` | Emoji containing comma (defensive) → quoted in CSV column |
| `export multiple transactions has multiple rows` | Two transactions → header + 2 data rows |
| `export amount uses plain number` | Amount `10.50` → plain text `10.50` in CSV |
| `export negative amount preserves negative sign` | Amount `-50.00` → plain text `-50.00` in CSV |
| `export empty note outputs quoted empty` | Note `""` → `,""` in CSV row |
| `export note with unicode preserves characters` | Emoji/unicode in note → preserved exactly |

### 7. Verify formatting
- Run `./gradlew ktlintCheck` to ensure new test files and modified exporter files follow project style.
- Fix any formatting issues with `./gradlew ktlintFormat`.

### 8. Run tests
- Run `./gradlew test` (or `./gradlew testDebugUnitTest`).
- All 26 tests should pass.

## Files Likely to Change
| File | Action |
|---|---|
| `app/src/main/java/com/financetracker/data/export/JsonExporter.kt` | Extract `buildJsonString()` in companion object, fix JSON escaping |
| `app/src/main/java/com/financetracker/data/export/CsvExporter.kt` | Extract `buildCsvContent()` in companion object, fix CSV escaping |
| `app/src/test/java/com/financetracker/data/export/TransactionFixtures.kt` | **New** — shared `txn()` and `cat()` helpers |
| `app/src/test/java/com/financetracker/data/export/JsonExporterTest.kt` | **New** — 13 tests |
| `app/src/test/java/com/financetracker/data/export/CsvExporterTest.kt` | **New** — 13 tests |

## Tests / Validation
- `./gradlew testDebugUnitTest` must pass.
- `./gradlew ktlintCheck` must pass.

## Risks, Tradeoffs, and Open Questions
1. **Escaping changes are real fixes, not test-only.** The `JsonExporter` and `CsvExporter` bugs (unescaped newlines/backslashes, unescaped category fields) could have produced broken output in production. Tests catch them and the plan fixes them.
2. **BigDecimal construction:** Use `BigDecimal("10.50")` (String constructor) in shared fixture `txn()` to guarantee scale. `BigDecimal(10.50)` would lose scale via `Double` intermediate.
3. **File overwrite:** Both exporters embed `LocalDate.now()` in the filename, so same-day exports overwrite the same file. This is existing behavior; unchanged by this plan.
4. **Round-trip / import tests:** Out of scope. This plan covers export (serialization) only.
5. **No new dependencies:** Zero additions to `gradle/libs.versions.toml` or `app/build.gradle.kts`. Plain JUnit 4 already present.
