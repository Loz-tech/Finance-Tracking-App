package com.financetracker.ui.preview

import androidx.compose.ui.graphics.Color
import com.financetracker.domain.model.Category
import com.financetracker.domain.model.Transaction
import com.financetracker.ui.calendar.CalendarDay
import com.financetracker.ui.categories.CategoryWithProgress
import com.financetracker.ui.components.charts.DonutSegment
import com.financetracker.ui.home.CategoryBudgetProgress
import com.financetracker.ui.theme.ChartColors
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

object PreviewData {

    val foodCategory = Category(
        id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
        name = "Food",
        iconName = "Restaurant",
        colorHex = "#1DBD8E",
        isDefault = true,
        sortOrder = 0
    )

    val transportCategory = Category(
        id = UUID.fromString("22222222-2222-2222-2222-222222222222"),
        name = "Transport",
        iconName = "DirectionsCar",
        colorHex = "#FF8F5C",
        isDefault = true,
        sortOrder = 1
    )

    val entertainmentCategory = Category(
        id = UUID.fromString("33333333-3333-3333-3333-333333333333"),
        name = "Entertainment",
        iconName = "Movie",
        colorHex = "#9151B8",
        isDefault = true,
        sortOrder = 2
    )

    val shoppingCategory = Category(
        id = UUID.fromString("44444444-4444-4444-4444-444444444444"),
        name = "Shopping",
        iconName = "ShoppingCart",
        colorHex = "#FF57B0",
        isDefault = false,
        sortOrder = 3
    )

    val categories = listOf(foodCategory, transportCategory, entertainmentCategory, shoppingCategory)

    val transactions = listOf(
        Transaction(
            id = UUID.fromString("55555555-5555-5555-5555-555555555555"),
            amount = BigDecimal("42.50"),
            note = "Lunch at burger place",
            date = LocalDate.now(),
            category = foodCategory
        ),
        Transaction(
            id = UUID.fromString("66666666-6666-6666-6666-666666666666"),
            amount = BigDecimal("15.00"),
            note = "Bus ticket",
            date = LocalDate.now().minusDays(1),
            category = transportCategory
        ),
        Transaction(
            id = UUID.fromString("77777777-7777-7777-7777-777777777777"),
            amount = BigDecimal("89.99"),
            note = "Movie night",
            date = LocalDate.now().minusDays(2),
            category = entertainmentCategory
        ),
        Transaction(
            id = UUID.fromString("88888888-8888-8888-8888-888888888888"),
            amount = BigDecimal("120.00"),
            note = "New shoes",
            date = LocalDate.now().minusDays(3),
            category = shoppingCategory
        )
    )

    val donutSegments: List<DonutSegment>
        get() = listOf(
            DonutSegment("Food", "Restaurant", 127.50f, ChartColors[0]),
            DonutSegment("Transport", "DirectionsCar", 45.00f, ChartColors[1]),
            DonutSegment("Entertainment", "Movie", 89.99f, ChartColors[2]),
            DonutSegment("Shopping", "ShoppingCart", 120.00f, ChartColors[3])
        )

    val categoryBudgetProgress = listOf(
        CategoryBudgetProgress(
            categoryId = foodCategory.id,
            categoryName = foodCategory.name,
            iconName = foodCategory.iconName,
            colorHex = foodCategory.colorHex,
            spent = BigDecimal("127.50"),
            limit = BigDecimal("300.00")
        ),
        CategoryBudgetProgress(
            categoryId = transportCategory.id,
            categoryName = transportCategory.name,
            iconName = transportCategory.iconName,
            colorHex = transportCategory.colorHex,
            spent = BigDecimal("45.00"),
            limit = BigDecimal("150.00")
        )
    )

    val categoryWithProgress = listOf(
        CategoryWithProgress(foodCategory, BigDecimal("127.50"), BigDecimal("300.00"), false),
        CategoryWithProgress(transportCategory, BigDecimal("45.00"), BigDecimal("150.00"), false),
        CategoryWithProgress(entertainmentCategory, BigDecimal("89.99"), BigDecimal("200.00"), false),
        CategoryWithProgress(shoppingCategory, BigDecimal("120.00"), null, false)
    )

    val calendarDays: List<CalendarDay>
        get() {
            val today = LocalDate.now()
            val start = today.withDayOfMonth(1)
            val days = start.datesUntil(start.plusMonths(1)).toList()
            return days.mapIndexed { index, date ->
                CalendarDay(
                    date = date,
                    total = if (index % 3 == 0) (index * 12.5) else 0.0,
                    transactions = if (index % 3 == 0) listOf(transactions[index % transactions.size]) else emptyList(),
                    intensity = if (index % 3 == 0) (index / 5).coerceIn(0, 4) else 0
                )
            }
        }

    fun donutColor(index: Int): Color = ChartColors[index % ChartColors.size]
}
