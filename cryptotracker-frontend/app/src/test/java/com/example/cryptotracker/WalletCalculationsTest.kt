package com.example.cryptotracker.utils

import org.junit.Assert.*
import org.junit.Test
import java.text.NumberFormat
import java.util.*

class WalletCalculationsTest {

    @Test
    fun `test calculate total balance with multiple holdings`() {
        // Given
        val holdings = mapOf(
            "BTC" to 0.05,
            "ETH" to 1.2,
            "SOL" to 100.0
        )
        val prices = mapOf(
            "BTC" to 50000.0,
            "ETH" to 3000.0,
            "SOL" to 100.0
        )

        // When
        var total = 0.0
        holdings.forEach { (symbol, amount) ->
            val price = prices[symbol] ?: 0.0
            total += amount * price
        }

        // Then
        assertEquals(16100.0, total, 0.001) // 2500 + 3600 + 10000
    }

    @Test
    fun `test calculate positive 24h change`() {
        // Given
        val amount = 0.05
        val priceChange = 1000.0

        // When
        val change24h = amount * priceChange

        // Then
        assertEquals(50.0, change24h, 0.001)
        assertTrue(change24h > 0)
    }

    @Test
    fun `test calculate negative 24h change`() {
        // Given
        val amount = 1.2
        val priceChange = -50.0

        // When
        val change24h = amount * priceChange

        // Then
        assertEquals(-60.0, change24h, 0.001)
        assertTrue(change24h < 0)
    }

    @Test
    fun `test calculate change percentage`() {
        // Given
        val total = 10000.0
        val change24h = 500.0

        // When
        val changePercent = (change24h / total) * 100

        // Then
        assertEquals(5.0, changePercent, 0.001)
    }

    @Test
    fun `test calculate negative change percentage`() {
        // Given
        val total = 10000.0
        val change24h = -200.0

        // When
        val changePercent = (change24h / total) * 100

        // Then
        assertEquals(-2.0, changePercent, 0.001)
    }

    @Test
    fun `test format currency to US dollars`() {
        // Given
        val balance = 16100.0
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)

        // When
        val formatted = formatter.format(balance)

        // Then
        assertTrue(formatted.contains("16,100"))
        assertTrue(formatted.startsWith("$"))
    }

    @Test
    fun `test empty holdings returns zero balance`() {
        // Given
        val holdings = emptyMap<String, Double>()
        val prices = mapOf("BTC" to 50000.0, "ETH" to 3000.0)

        // When
        var total = 0.0
        holdings.forEach { (symbol, amount) ->
            total += amount * (prices[symbol] ?: 0.0)
        }

        // Then
        assertEquals(0.0, total, 0.001)
    }

    @Test
    fun `test holding with unknown price defaults to zero`() {
        // Given
        val holdings = mapOf("UNKNOWN" to 100.0)
        val prices = mapOf("BTC" to 50000.0)

        // When
        var total = 0.0
        holdings.forEach { (symbol, amount) ->
            total += amount * (prices[symbol] ?: 0.0)
        }

        // Then
        assertEquals(0.0, total, 0.001)
    }

    @Test
    fun `test percentage format with two decimals`() {
        // Given
        val percentChange = 2.456789

        // When
        val formatted = String.format("%.2f%%", percentChange)

        // Then
        assertEquals("2.46%", formatted)
    }

    @Test
    fun `test add plus sign for positive change`() {
        // Given
        val change = 2.5

        // When
        val formatted = if (change >= 0) "+${String.format("%.2f%%", change)}"
        else String.format("%.2f%%", change)

        // Then
        assertEquals("+2.50%", formatted)
    }

    @Test
    fun `test no plus sign for negative change`() {
        // Given
        val change = -2.5

        // When
        val formatted = if (change >= 0) "+${String.format("%.2f%%", change)}"
        else String.format("%.2f%%", change)

        // Then
        assertEquals("-2.50%", formatted)
    }
}