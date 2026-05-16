# Plan: ktlint + `.editorconfig` Integration (Option B)

## Status
- **Decision:** Option B — Stable tools only
- **Scope:** Formatting enforcement via ktlint-gradle 14.2.0. detekt deferred.
- **Created:** 2026-05-16

---

## Context

FinanceTrackingApp currently has **no linting or formatting enforcement**:
- No detekt, no ktlint, no `.editorconfig`
- AGENTS.md explicitly documents this gap

Stack constraints prevent using stable detekt:
- Kotlin 2.2.10 / AGP 9.2.1 / Gradle 9.4.1
- detekt 1.23.8 stable only supports Kotlin ≤2.0.21, AGP ≤8.8.1
- detekt 2.0.0-alpha supports our stack but is pre-release

**Decision:** Use stable ktlint-gradle 14.2.0 now. Add detekt 2.0 stable when it releases.

---

## Goal

Enforce consistent Kotlin code formatting across the project with:
1. **ktlint-gradle 14.2.0** — Gradle task integration, CI-ready
2. **`.editorconfig`** — IDE-agnostic formatting rules (ktlint and IDE both respect it)
3. **One-time `ktlintFormat`** — Bring entire existing codebase into compliance
4. **Wired to `check`** — `./gradlew check` fails on formatting violations

---

## Evidence

| Source | Finding |
|--------|---------|
| `gradle/libs.versions.toml` | Version catalog exists. No ktlint entry. |
| `build.gradle.kts` (root) | 5 plugins `apply false`. Needs ktlint plugin registration. |
| `app/build.gradle.kts` | No lint/format blocks. Standard Android app setup. |
| `settings.gradle.kts` | `gradlePluginPortal()` present — required for ktlint plugin. |
| `gradle-wrapper.properties` | Gradle 9.4.1 — ktlint-gradle 14.2.0 officially supports this. |
| `gradle.properties` | `kotlin.code.style=official` — aligned with ktlint defaults. |
| ktlint-gradle 14.2.0 release | Stable, March 2026. Supports Gradle 9, Kotlin 2.x, AGP 9 new DSL. |

---

## Uncertainties / Assumptions

1. **Initial formatting flood.** The project has never been auto-formatted. We will run `ktlintFormat` once, review the diff, and commit it. There is no baseline mechanism.
2. **ktlint CLI version.** ktlint-gradle 14.2.0 bundles a default ktlint CLI. We will pin to `1.5.0` explicitly for reproducibility.
3. **Compose-specific rules.** Some ktlint rules may flag Compose idioms. Setting `android = true` in ktlint config enables Android-aware formatting.
4. **Long SQL strings in DAOs.** Multiline `@Query` annotations may violate line-length rules. `.editorconfig` sets `max_line_length = 120` as a reasonable balance.

---

## Execution Steps

### Step 1: Add ktlint to Version Catalog
**File:** `gradle/libs.versions.toml`

Add to `[versions]`:
```toml
ktlintGradle = "14.2.0"
```

Add to `[plugins]`:
```toml
ktlint = { id = "org.jlleitschuh.gradle.ktlint", version.ref = "ktlintGradle" }
```

---

### Step 2: Register in Root Build Script
**File:** `build.gradle.kts`

Add to the `plugins` block:
```kotlin
alias(libs.plugins.ktlint) apply false
```

---

### Step 3: Apply and Configure in App Module
**File:** `app/build.gradle.kts`

Add `alias(libs.plugins.ktlint)` to the `plugins` block alongside existing plugins.

Add configuration block after the `android { }` block:
```kotlin
ktlint {
    version.set("1.5.0")
    android.set(true)
    outputToConsole.set(true)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}
```

---

### Step 4: Create `.editorconfig`
**File:** `.editorconfig` (project root)

```editorconfig
root = true

[*]
charset = utf-8
end_of_line = lf
indent_size = 4
indent_style = space
insert_final_newline = true
trim_trailing_whitespace = true
max_line_length = 120

[*.{yml,yaml}]
indent_size = 2

[*.md]
trim_trailing_whitespace = false

[*.kt]
ktlint_function_naming_ignore_when_annotated_with=Composable
ktlint_code_style = android_studio
```

---

### Step 5: Run Initial Format and Review
```bash
./gradlew ktlintFormat
./gradlew ktlintCheck
```

**Review checklist for the generated diff:**
- [ ] All changes are mechanical (indentation, spacing, imports, trailing commas)
- [ ] No semantic/logic changes introduced
- [ ] No broken Compose modifiers or DSL structures
- [ ] SQL strings in `@Query` annotations remain intact

If clean, commit the formatting changes separately from configuration changes.

Then create `.git-blame-ignore-revs`:
```bash
# After committing the formatting changes
git rev-parse HEAD > .git-blame-ignore-revs
git config blame.ignoreRevsFile .git-blame-ignore-revs
```

**Note:** Future `git blame` will skip the formatting commit. Team members must run `git config blame.ignoreRevsFile .git-blame-ignore-revs` locally, or configure it globally.

---

### Step 6: Wire to `check` Task
**File:** `app/build.gradle.kts`

Verify `ktlintCheck` runs as part of `check`. If ktlint-gradle does not auto-wire, add:
```kotlin
tasks.check {
    dependsOn(tasks.ktlintCheck)
}
```

---

### Step 7: Create CI Workflow
**File:** `.github/workflows/ci.yml`

```yaml
name: CI

on:
  push:
    branches: [main]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '21'
          cache: 'gradle'
      - name: Run checks
        run: ./gradlew check --no-daemon
```

> **Note:** `./gradlew check` includes `ktlintCheck`, compilation, and unit tests. No emulator tests.
>
> **Optional pre-commit hook:** Run `ktlintCheck` before commit with a git hook. Example `.git/hooks/pre-commit`:
> ```bash
> #!/bin/bash
> set -e
> ./gradlew ktlintCheck --quiet
> ```
> Set executable: `chmod +x .git/hooks/pre-commit`. Not mandatory — CI still catches violations on push.

---

### Step 8: Update AGENTS.md
**File:** `AGENTS.md` — Section 15 (Code Style / Lint)

Replace:
```
- No detekt, ktlint, or `.editorconfig`
- No custom lint configuration
```

With:
```
- **ktlint** — `org.jlleitschuh.gradle.ktlint` 14.2.0, configured in `app/build.gradle.kts`
- **`.editorconfig`** — project root, shared by ktlint and IDE
- Run `./gradlew ktlintCheck` to verify formatting
- Run `./gradlew ktlintFormat` to auto-fix formatting
- **CI** — `.github/workflows/ci.yml` runs `./gradlew check` on every PR and push to `main`
- **detekt** — deferred until stable 2.0 supports Kotlin 2.2.10 + AGP 9.2.1
```

---

### Step 9: Validate
Run the following and confirm all pass:
```bash
./gradlew ktlintCheck
./gradlew check
```

---

## Rollback Instructions

If the integration causes problems:
1. Revert `gradle/libs.versions.toml` changes (remove ktlint version/plugin entries)
2. Revert `build.gradle.kts` (remove ktlint plugin alias)
3. Revert `app/build.gradle.kts` (remove ktlint plugin + config block)
4. Delete `.editorconfig`
5. Delete `.github/workflows/ci.yml`
6. Delete `.git-blame-ignore-revs`
7. Revert the formatting commit via git

---

## Post-Integration Notes

**Existing branches:** After the formatting commit lands on `main`, all open branches will conflict on merge. Rebase onto `main`, then run `./gradlew ktlintFormat` on the branch before opening a PR.

---

## Future Work (Blocked)

- **detekt 2.0 stable** — Add when released with Kotlin 2.2.10 + AGP 9.2.1 support
  - Will need: `detekt.yml`, baseline generation, complexity thresholds tuned to project size
  - Must disable detekt's `formatting` ruleset (ktlint already handles formatting)
