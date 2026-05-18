package com.financetracker.data.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CsvExporterTest {

    @Test
    fun `export empty list outputs header only`() {
        val output = CsvExporter.buildCsvContent(emptyList())
        val lines = output.lines().filter { it.isNotBlank() }
        assertEquals(1, lines.size)
        assertEquals("Date,Category,Emoji,Amount,Note", lines[0])
    }

    @Test
    fun `export single transaction outputs correct row`() {
        val txn = TransactionFixtures.txn("10.50", note = "lunch")
        val output = CsvExporter.buildCsvContent(listOf(txn))

        val lines = output.lines().filter { it.isNotBlank() }
        assertEquals(2, lines.size)
        assertEquals("Date,Category,Emoji,Amount,Note", lines[0])
        assertTrue(lines[1].startsWith("2026-05-18,"))
        assertTrue(lines[1].contains(",\"Food\",\"🍔\",10.50,\"lunch\""))
    }

    @Test
    fun `export note with quotes escapes quotes`() {
        val txn = TransactionFixtures.txn("10.50", note = "Salary \"Net\"")
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains(",\"Salary \"\"Net\"\"\"\n"))
    }

    @Test
    fun `export note with commas preserved inside quotes`() {
        val txn = TransactionFixtures.txn("10.50", note = "Food, Drinks")
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains(",\"Food, Drinks\"\n"))
    }

    @Test
    fun `export note with newline is quoted`() {
        val txn = TransactionFixtures.txn("10.50", note = "Line1\nLine2")
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains(",\"Line1\nLine2\"\n"))
    }

    @Test
    fun `export category name with comma is quoted`() {
        val cat = TransactionFixtures.cat(name = "Food, Drinks")
        val txn = TransactionFixtures.txn("10.50", category = cat)
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains("2026-05-18,\"Food, Drinks\""))
    }

    @Test
    fun `export category name with quote is escaped`() {
        val cat = TransactionFixtures.cat(name = "Salary \"Net\"")
        val txn = TransactionFixtures.txn("10.50", category = cat)
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains("2026-05-18,\"Salary \"\"Net\"\"\",\""))
    }

    @Test
    fun `export category emoji with comma is quoted`() {
        val cat = TransactionFixtures.cat(emoji = "🍔,🍕")
        val txn = TransactionFixtures.txn("10.50", category = cat)
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains(",\"🍔,🍕\""))
    }

    @Test
    fun `export multiple transactions has multiple rows`() {
        val txn1 = TransactionFixtures.txn("10.50")
        val txn2 = TransactionFixtures.txn("20.00")
        val output = CsvExporter.buildCsvContent(listOf(txn1, txn2))

        val lines = output.lines().filter { it.isNotBlank() }
        assertEquals(3, lines.size)
    }

    @Test
    fun `export amount uses plain number`() {
        val txn = TransactionFixtures.txn("10.50")
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains(",10.50,"))
    }

    @Test
    fun `export negative amount preserves negative sign`() {
        val txn = TransactionFixtures.txn("-50.00")
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains(",-50.00,"))
    }

    @Test
    fun `export empty note outputs quoted empty`() {
        val txn = TransactionFixtures.txn("10.50", note = "")
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains(",\"\"\n"))
    }

    @Test
    fun `export note with unicode preserves characters`() {
        val txn = TransactionFixtures.txn("10.50", note = "Lunch 🍔 at café")
        val output = CsvExporter.buildCsvContent(listOf(txn))

        assertTrue(output.contains(",\"Lunch 🍔 at café\"\n"))
    }
}
