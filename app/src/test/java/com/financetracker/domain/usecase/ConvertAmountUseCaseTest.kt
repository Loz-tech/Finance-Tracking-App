package com.financetracker.domain.usecase

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertAmountUseCaseTest {

    private val useCase = ConvertAmountUseCase()

    @Test
    fun `same currency returns same amount`() {
        val result = useCase(
            amount = BigDecimal("100.00"),
            fromCurrency = "USD",
            toCurrency = "USD",
            rates = emptyMap()
        )
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("100.00"), result.getOrThrow())
    }

    @Test
    fun `converts USD to EUR correctly`() {
        val rates = mapOf(
            "USD" to BigDecimal.ONE,
            "EUR" to BigDecimal("0.92")
        )
        val result = useCase(
            amount = BigDecimal("100.00"),
            fromCurrency = "USD",
            toCurrency = "EUR",
            rates = rates
        )
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("92.00"), result.getOrThrow())
    }

    @Test
    fun `converts EUR to USD correctly`() {
        val rates = mapOf(
            "USD" to BigDecimal.ONE,
            "EUR" to BigDecimal("0.92")
        )
        val result = useCase(
            amount = BigDecimal("92.00"),
            fromCurrency = "EUR",
            toCurrency = "USD",
            rates = rates
        )
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("100.00"), result.getOrThrow())
    }

    @Test
    fun `converts EUR to JPY via cross rate`() {
        val rates = mapOf(
            "USD" to BigDecimal.ONE,
            "EUR" to BigDecimal("0.92"),
            "JPY" to BigDecimal("145.50")
        )
        val result = useCase(
            amount = BigDecimal("100.00"),
            fromCurrency = "EUR",
            toCurrency = "JPY",
            rates = rates
        )
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("15815"), result.getOrThrow())
    }

    @Test
    fun `JPY has zero decimal places`() {
        val rates = mapOf(
            "USD" to BigDecimal.ONE,
            "JPY" to BigDecimal("145.50")
        )
        val result = useCase(
            amount = BigDecimal("100.50"),
            fromCurrency = "USD",
            toCurrency = "JPY",
            rates = rates
        )
        assertTrue(result.isSuccess)
        assertEquals(0, result.getOrThrow().scale())
    }

    @Test
    fun `handles very small amounts`() {
        val rates = mapOf(
            "USD" to BigDecimal.ONE,
            "EUR" to BigDecimal("0.92")
        )
        val result = useCase(
            amount = BigDecimal("0.01"),
            fromCurrency = "USD",
            toCurrency = "EUR",
            rates = rates
        )
        assertTrue(result.isSuccess)
        assertEquals(BigDecimal("0.01"), result.getOrThrow())
    }

    @Test
    fun `fails when fromCurrency rate is missing`() {
        val rates = mapOf("USD" to BigDecimal.ONE)
        val result = useCase(
            amount = BigDecimal("100.00"),
            fromCurrency = "EUR",
            toCurrency = "USD",
            rates = rates
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `fails when toCurrency rate is missing`() {
        val rates = mapOf("USD" to BigDecimal.ONE)
        val result = useCase(
            amount = BigDecimal("100.00"),
            fromCurrency = "USD",
            toCurrency = "EUR",
            rates = rates
        )
        assertTrue(result.isFailure)
    }
}
