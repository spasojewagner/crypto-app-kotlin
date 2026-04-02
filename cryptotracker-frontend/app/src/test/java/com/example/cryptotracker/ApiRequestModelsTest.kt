package com.example.cryptotracker.api

import org.junit.Assert.*
import org.junit.Test

class ApiRequestModelsTest {

    @Test
    fun `test ToggleFavoriteRequest creation`() {
        // Given & When
        val request = ToggleFavoriteRequest(
            userId = "user123",
            symbol = "BTC"
        )

        // Then
        assertEquals("user123", request.userId)
        assertEquals("BTC", request.symbol)
    }

    @Test
    fun `test AddPortfolioRequest with valid data`() {
        // Given & When
        val request = AddPortfolioRequest(
            userId = "user456",
            symbol = "ETH",
            name = "Ethereum",
            amount = 5.0,
            buyPrice = 3000.0
        )

        // Then
        assertEquals("user456", request.userId)
        assertEquals("ETH", request.symbol)
        assertEquals("Ethereum", request.name)
        assertEquals(5.0, request.amount, 0.001)
        assertEquals(3000.0, request.buyPrice, 0.001)
    }

    @Test
    fun `test ApiResponse with success`() {
        // Given & When
        val response = ApiResponse(
            success = true,
            message = "Operacija uspešna"
        )

        // Then
        assertTrue(response.success)
        assertEquals("Operacija uspešna", response.message)
    }

    @Test
    fun `test ApiResponse with failure`() {
        // Given & When
        val response = ApiResponse(
            success = false,
            message = "Greška pri izvršavanju"
        )

        // Then
        assertFalse(response.success)
        assertEquals("Greška pri izvršavanju", response.message)
    }

    @Test
    fun `test ChartData with OHLCV values`() {
        // Given & When
        val chartData = ChartData(
            time = 1640000000000L,
            open = 50000.0,
            high = 51000.0,
            low = 49000.0,
            close = 50500.0,
            volume = 1000000.0
        )

        // Then
        assertEquals(1640000000000L, chartData.time)
        assertEquals(50000.0, chartData.open, 0.001)
        assertEquals(51000.0, chartData.high, 0.001)
        assertEquals(49000.0, chartData.low, 0.001)
        assertEquals(50500.0, chartData.close, 0.001)
        assertEquals(1000000.0, chartData.volume, 0.001)
    }

    @Test
    fun `test ChartData high is greater than or equal to low`() {
        // Given & When
        val chartData = ChartData(
            time = 1640000000000L,
            open = 50000.0,
            high = 52000.0,
            low = 49000.0,
            close = 51000.0,
            volume = 1200000.0
        )

        // Then
        assertTrue(chartData.high >= chartData.low)
    }

    @Test
    fun `test ChartResponse with multiple data points`() {
        // Given
        val dataList = listOf(
            ChartData(1640000000000L, 50000.0, 51000.0, 49000.0, 50500.0, 1000000.0),
            ChartData(1640003600000L, 50500.0, 52000.0, 50000.0, 51500.0, 1200000.0),
            ChartData(1640007200000L, 51500.0, 53000.0, 51000.0, 52500.0, 1400000.0)
        )

        // When
        val response = ChartResponse(
            success = true,
            data = dataList
        )

        // Then
        assertTrue(response.success)
        assertEquals(3, response.data.size)
    }

    @Test
    fun `test ChartResponse with empty data`() {
        // Given & When
        val response = ChartResponse(
            success = false,
            data = emptyList()
        )

        // Then
        assertFalse(response.success)
        assertTrue(response.data.isEmpty())
    }
}