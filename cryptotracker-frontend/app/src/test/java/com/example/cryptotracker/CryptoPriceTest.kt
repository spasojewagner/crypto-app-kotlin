package com.example.cryptotracker.models

import org.junit.Assert.*
import org.junit.Test

class CryptoPriceTest {

    @Test
    fun `test CryptoPrice creation with all fields`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "BTC",
            name = "Bitcoin",
            price = 50000.0,
            priceChange24h = 1000.0,
            priceChangePercent24h = 2.0,
            volume24h = 50000000.0,
            iconUrl = "https://example.com/btc.png",
            isFavorite = true
        )

        // Then
        assertEquals("BTC", crypto.symbol)
        assertEquals("Bitcoin", crypto.name)
        assertEquals(50000.0, crypto.price, 0.001)
        assertEquals(1000.0, crypto.priceChange24h, 0.001)
        assertEquals(2.0, crypto.priceChangePercent24h, 0.001)
        assertEquals(50000000.0, crypto.volume24h, 0.001)
        assertEquals("https://example.com/btc.png", crypto.iconUrl)
        assertTrue(crypto.isFavorite)
    }

    @Test
    fun `test CryptoPrice with negative price change`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "ETH",
            name = "Ethereum",
            price = 3000.0,
            priceChange24h = -50.0,
            priceChangePercent24h = -1.64,
            volume24h = 25000000.0,
            iconUrl = "https://example.com/eth.png",
            isFavorite = false
        )

        // Then
        assertTrue(crypto.priceChange24h < 0)
        assertTrue(crypto.priceChangePercent24h < 0)
    }

    @Test
    fun `test CryptoPrice with positive price change`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "SOL",
            name = "Solana",
            price = 100.0,
            priceChange24h = 5.0,
            priceChangePercent24h = 5.26,
            volume24h = 5000000.0,
            iconUrl = "https://example.com/sol.png",
            isFavorite = false
        )

        // Then
        assertTrue(crypto.priceChange24h > 0)
        assertTrue(crypto.priceChangePercent24h > 0)
    }

    @Test
    fun `test CryptoPrice copy function`() {
        // Given
        val original = CryptoPrice(
            symbol = "ADA",
            name = "Cardano",
            price = 0.50,
            priceChange24h = 0.05,
            priceChangePercent24h = 11.11,
            volume24h = 2000000.0,
            iconUrl = "https://example.com/ada.png",
            isFavorite = false
        )

        // When
        val copied = original.copy(isFavorite = true)

        // Then
        assertEquals(original.symbol, copied.symbol)
        assertEquals(original.name, copied.name)
        assertEquals(original.price, copied.price, 0.001)
        assertFalse(original.isFavorite)
        assertTrue(copied.isFavorite)
    }

    @Test
    fun `test CryptoPrice equality`() {
        // Given
        val crypto1 = CryptoPrice(
            symbol = "BTC",
            name = "Bitcoin",
            price = 50000.0,
            priceChange24h = 1000.0,
            priceChangePercent24h = 2.0,
            volume24h = 50000000.0,
            iconUrl = "https://example.com/btc.png",
            isFavorite = false
        )
        val crypto2 = crypto1.copy()

        // Then
        assertEquals(crypto1, crypto2)
    }

    @Test
    fun `test CryptoPrice inequality when price differs`() {
        // Given
        val crypto1 = CryptoPrice(
            symbol = "BTC",
            name = "Bitcoin",
            price = 50000.0,
            priceChange24h = 1000.0,
            priceChangePercent24h = 2.0,
            volume24h = 50000000.0,
            iconUrl = "https://example.com/btc.png",
            isFavorite = false
        )
        val crypto2 = crypto1.copy(price = 51000.0)

        // Then
        assertNotEquals(crypto1, crypto2)
    }

    @Test
    fun `test CryptoPrice handles zero price change`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "USDT",
            name = "Tether",
            price = 1.0,
            priceChange24h = 0.0,
            priceChangePercent24h = 0.0,
            volume24h = 100000000.0,
            iconUrl = "https://example.com/usdt.png",
            isFavorite = false
        )

        // Then
        assertEquals(0.0, crypto.priceChange24h, 0.001)
        assertEquals(0.0, crypto.priceChangePercent24h, 0.001)
    }

    @Test
    fun `test CryptoPrice favorite toggle`() {
        // Given
        val crypto = CryptoPrice(
            symbol = "BNB",
            name = "Binance Coin",
            price = 300.0,
            priceChange24h = 10.0,
            priceChangePercent24h = 3.45,
            volume24h = 15000000.0,
            iconUrl = "https://example.com/bnb.png",
            isFavorite = false
        )

        // When
        val favorited = crypto.copy(isFavorite = true)
        val unfavorited = favorited.copy(isFavorite = false)

        // Then
        assertFalse(crypto.isFavorite)
        assertTrue(favorited.isFavorite)
        assertFalse(unfavorited.isFavorite)
    }

    @Test
    fun `test CryptoPrice with small decimal price`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "SHIB",
            name = "Shiba Inu",
            price = 0.00001234,
            priceChange24h = 0.00000056,
            priceChangePercent24h = 4.76,
            volume24h = 5000000.0,
            iconUrl = "https://example.com/shib.png",
            isFavorite = false
        )

        // Then
        assertEquals(0.00001234, crypto.price, 0.00000001)
        assertEquals(0.00000056, crypto.priceChange24h, 0.00000001)
    }

    @Test
    fun `test CryptoPrice with large price`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "BTC",
            name = "Bitcoin",
            price = 98765.43,
            priceChange24h = 2345.67,
            priceChangePercent24h = 2.43,
            volume24h = 75000000.0,
            iconUrl = "https://example.com/btc.png",
            isFavorite = true
        )

        // Then
        assertEquals(98765.43, crypto.price, 0.01)
        assertEquals(2345.67, crypto.priceChange24h, 0.01)
    }

    @Test
    fun `test multiple CryptoPrice instances are independent`() {
        // Given
        val btc = CryptoPrice(
            symbol = "BTC",
            name = "Bitcoin",
            price = 50000.0,
            priceChange24h = 1000.0,
            priceChangePercent24h = 2.0,
            volume24h = 50000000.0,
            iconUrl = "https://example.com/btc.png",
            isFavorite = false
        )

        val eth = CryptoPrice(
            symbol = "ETH",
            name = "Ethereum",
            price = 3000.0,
            priceChange24h = -50.0,
            priceChangePercent24h = -1.64,
            volume24h = 25000000.0,
            iconUrl = "https://example.com/eth.png",
            isFavorite = true
        )

        // Then
        assertNotEquals(btc.symbol, eth.symbol)
        assertNotEquals(btc.price, eth.price, 0.001)
        assertNotEquals(btc.isFavorite, eth.isFavorite)
        assertNotEquals(btc.iconUrl, eth.iconUrl)
    }

    @Test
    fun `test CryptoPrice with high volume`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "BTC",
            name = "Bitcoin",
            price = 50000.0,
            priceChange24h = 1000.0,
            priceChangePercent24h = 2.0,
            volume24h = 999999999.99,
            iconUrl = "https://example.com/btc.png",
            isFavorite = false
        )

        // Then
        assertEquals(999999999.99, crypto.volume24h, 0.01)
        assertTrue(crypto.volume24h > 0)
    }

    @Test
    fun `test CryptoPrice volume is always positive or zero`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "ETH",
            name = "Ethereum",
            price = 3000.0,
            priceChange24h = -50.0,
            priceChangePercent24h = -1.64,
            volume24h = 0.0,
            iconUrl = "https://example.com/eth.png",
            isFavorite = false
        )

        // Then
        assertTrue(crypto.volume24h >= 0)
    }

    @Test
    fun `test CryptoPrice iconUrl is not empty`() {
        // Given & When
        val crypto = CryptoPrice(
            symbol = "BTC",
            name = "Bitcoin",
            price = 50000.0,
            priceChange24h = 1000.0,
            priceChangePercent24h = 2.0,
            volume24h = 50000000.0,
            iconUrl = "https://example.com/btc.png",
            isFavorite = false
        )

        // Then
        assertFalse(crypto.iconUrl.isEmpty())
        assertTrue(crypto.iconUrl.startsWith("https://"))
    }
}