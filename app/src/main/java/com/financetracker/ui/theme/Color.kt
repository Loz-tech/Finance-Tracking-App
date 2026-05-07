package com.financetracker.ui.theme

import androidx.compose.ui.graphics.Color

// Teal Seed Palette (Light) — based on OKLCH teal design tokens
val Teal10 = Color(0xFF002021)
val Teal20 = Color(0xFF003738)
val Teal30 = Color(0xFF004F51)
val Teal40 = Color(0xFF006874)
val Teal80 = Color(0xFF4CD9E5)
val Teal90 = Color(0xFF6FF6FF)
val Teal95 = Color(0xFFB9FCFF)
val Teal99 = Color(0xFFF1FFFF)

// Secondary (Muted teal)
val TealSecondary10 = Color(0xFF061F21)
val TealSecondary20 = Color(0xFF1C3436)
val TealSecondary30 = Color(0xFF324B4D)
val TealSecondary40 = Color(0xFF496364)
val TealSecondary80 = Color(0xFFC1D9DA)
val TealSecondary90 = Color(0xFFDCF5F6)
val TealSecondary95 = Color(0xFFEBFFFF)

// Tertiary (Purple complement)
val Tertiary10 = Color(0xFF1E0035)
val Tertiary20 = Color(0xFF331155)
val Tertiary30 = Color(0xFF4A296D)
val Tertiary40 = Color(0xFF634186)
val Tertiary80 = Color(0xFFCFBEF4)
val Tertiary90 = Color(0xFFE8DEFF)
val Tertiary95 = Color(0xFFF5EEFF)

// Error
val Error10 = Color(0xFF410002)
val Error20 = Color(0xFF690005)
val Error30 = Color(0xFF93000A)
val Error40 = Color(0xFFBA1A1A)
val Error80 = Color(0xFFFFB4AB)
val Error90 = Color(0xFFFFDAD6)
val Error95 = Color(0xFFFFEDEA)

// Neutral
val Neutral10 = Color(0xFF191C1C)
val Neutral20 = Color(0xFF2E3131)
val Neutral30 = Color(0xFF444748)
val Neutral40 = Color(0xFF5C5F60)
val Neutral80 = Color(0xFFC6C7C7)
val Neutral90 = Color(0xFFE2E2E3)
val Neutral95 = Color(0xFFF0F1F1)
val Neutral99 = Color(0xFFFBFDFC)

// Accent Color Seeds (for accent picker)
enum class AccentColor(
    val label: String,
    val primaryColor: Color,
    val primaryContainerColor: Color
) {
    TEAL("Teal", Color(0xFF006874), Color(0xFFB9FCFF)),
    BLUE("Blue", Color(0xFF005CBB), Color(0xFFD7E3FF)),
    PURPLE("Purple", Color(0xFF634186), Color(0xFFE8DEFF)),
    ORANGE("Orange", Color(0xFF8B4A00), Color(0xFFFFDCC2)),
    GREEN("Green", Color(0xFF006E28), Color(0xFFA6F5B6)),
    PINK("Pink", Color(0xFF90416A), Color(0xFFFFD8E7))
}
