package com.financetracker.data.export

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JsonExporterTest {

    @Test
    fun `export empty list creates valid JSON array`() {
        val output = JsonExporter.buildJsonString(emptyList())
        assertTrue(output.trim().startsWith("["))
        assertTrue(output.trim().endsWith("]"))
        assertFalse(output.contains("date"))
    }

    @Test
    fun `export single transaction outputs correct fields`() {
        val txn = TransactionFixtures.txn("10.50")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"date\": \"2026-05-18\""))
        assertTrue(output.contains("\"category\": \"Food\""))
        assertTrue(output.contains("\"iconName\": \"🍔\""))
        assertTrue(output.contains("\"amount\": 10.50"))
        assertTrue(output.contains("\"originalAmount\": 10.50"))
        assertTrue(output.contains("\"originalCurrencyCode\": \"USD\""))
        assertTrue(output.contains("\"note\": \"\""))
    }

    @Test
    fun `export multiple transactions has comma between objects`() {
        val txn1 = TransactionFixtures.txn("10.50")
        val txn2 = TransactionFixtures.txn("20.00")
        val output = JsonExporter.buildJsonString(listOf(txn1, txn2))

        val objectCount = output.count { it == '{' }
        assertEquals(2, objectCount)
        assertTrue(output.contains("},"))
    }

    @Test
    fun `export category name with quote is escaped`() {
        val cat = TransactionFixtures.cat(name = "Salary \"Net\"")
        val txn = TransactionFixtures.txn("10.50", category = cat)
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"category\": \"Salary \\\"Net\\\"\""))
    }

    @Test
    fun `export note with double quotes escapes quotes`() {
        val txn = TransactionFixtures.txn("10.50", note = "Salary \"Net\"")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"note\": \"Salary \\\"Net\\\"\""))
    }

    @Test
    fun `export note with backslash escapes backslash`() {
        val txn = TransactionFixtures.txn("10.50", note = "Path \\ Folder")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"note\": \"Path \\\\ Folder\""))
    }

    @Test
    fun `export note with newline escapes newline`() {
        val txn = TransactionFixtures.txn("10.50", note = "Line1\nLine2")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"note\": \"Line1\\nLine2\""))
    }

    @Test
    fun `export note with carriage return escapes carriage return`() {
        val txn = TransactionFixtures.txn("10.50", note = "Line1\rLine2")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"note\": \"Line1\\rLine2\""))
    }

    @Test
    fun `export note with tab escapes tab`() {
        val txn = TransactionFixtures.txn("10.50", note = "Col1\tCol2")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"note\": \"Col1\\tCol2\""))
    }

    @Test
    fun `export amount preserves decimal scale`() {
        val txn = TransactionFixtures.txn("10.50")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"amount\": 10.50"))
        assertFalse(output.contains("\"amount\": 10.5,"))
    }

    @Test
    fun `export negative amount preserves negative sign`() {
        val txn = TransactionFixtures.txn("-50.00")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"amount\": -50.00"))
    }

    @Test
    fun `export empty note outputs empty string`() {
        val txn = TransactionFixtures.txn("10.50", note = "")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"note\": \"\""))
    }

    @Test
    fun `export note with unicode preserves characters`() {
        val txn = TransactionFixtures.txn("10.50", note = "Lunch 🍔 at café")
        val output = JsonExporter.buildJsonString(listOf(txn))

        assertTrue(output.contains("\"note\": \"Lunch 🍔 at café\""))
    }
}
