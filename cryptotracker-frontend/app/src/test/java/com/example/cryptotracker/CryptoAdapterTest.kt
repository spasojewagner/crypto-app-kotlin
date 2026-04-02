package com.example.cryptotracker.adapters

import com.example.cryptotracker.models.CryptoPrice
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CryptoAdapterTest {

    private lateinit var adapter: CryptoAdapter
    private var itemClickCalled = false
    private var favoriteClickCalled = false

    @Before
    fun setUp() {
        adapter = CryptoAdapter(
            onItemClick = { itemClickCalled = true },
            onFavoriteClick = { favoriteClickCalled = true }
        )
    }

    @Test
    fun `test adapter initially has zero items`() {
        // Then
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `test submitList updates item count correctly`() {
        // Given
        val cryptoList = listOf(
            createBitcoin(),
            createEthereum()
        )

        // When
        adapter.submitList(cryptoList)

        // Then
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun `test submitList with empty list`() {
        // Given
        val emptyList = emptyList<CryptoPrice>()

        // When
        adapter.submitList(emptyList)

        // Then
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `test DiffCallback identifies same items by symbol`() {
        // Given
        val callback = CryptoAdapter.CryptoDiffCallback()
        val crypto1 = createBitcoin()
        val crypto2 = crypto1.copy(price = 51000.0)

        // When
        val result = callback.areItemsTheSame(crypto1, crypto2)

        // Then
        assertTrue(result)
    }

    @Test
    fun `test DiffCallback identifies different items`() {
        // Given
        val callback = CryptoAdapter.CryptoDiffCallback()
        val bitcoin = createBitcoin()
        val ethereum = createEthereum()

        // When
        val result = callback.areItemsTheSame(bitcoin, ethereum)

        // Then
        assertFalse(result)
    }

    @Test
    fun `test DiffCallback detects content changes`() {
        // Given
        val callback = CryptoAdapter.CryptoDiffCallback()
        val crypto1 = createBitcoin()
        val crypto2 = crypto1.copy(price = 51000.0)

        // When
        val result = callback.areContentsTheSame(crypto1, crypto2)

        // Then
        assertFalse(result)
    }

    @Test
    fun `test DiffCallback detects identical content`() {
        // Given
        val callback = CryptoAdapter.CryptoDiffCallback()
        val crypto1 = createBitcoin()
        val crypto2 = crypto1.copy()

        // When
        val result = callback.areContentsTheSame(crypto1, crypto2)

        // Then
        assertTrue(result)
    }

    // Helper methods
    private fun createBitcoin() = CryptoPrice(
        symbol = "BTC",
        name = "Bitcoin",
        price = 50000.0,
        priceChange24h = 1000.0,
        priceChangePercent24h = 2.0,
        volume24h = 50000000.0,
        iconUrl = "https://example.com/btc.png",
        isFavorite = false
    )

    private fun createEthereum() = CryptoPrice(
        symbol = "ETH",
        name = "Ethereum",
        price = 3000.0,
        priceChange24h = -50.0,
        priceChangePercent24h = -1.64,
        volume24h = 25000000.0,
        iconUrl = "https://example.com/eth.png",
        isFavorite = false
    )
}