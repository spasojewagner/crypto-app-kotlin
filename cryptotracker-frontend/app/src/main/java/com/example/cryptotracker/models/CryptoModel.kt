package com.example.cryptotracker.models

import com.google.gson.annotations.SerializedName

data class CryptoPrice(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Double,
    @SerializedName("priceChange24h") val priceChange24h: Double,
    @SerializedName("priceChangePercent24h") val priceChangePercent24h: Double,
    @SerializedName("volume24h") val volume24h: Double,
    @SerializedName("iconUrl") val iconUrl: String,
    @SerializedName("isFavorite") var isFavorite: Boolean = false  // ← PROMENI U 'var'
)

data class CryptoResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<CryptoPrice>,
    @SerializedName("timestamp") val timestamp: String
)

data class PortfolioItem(
    val symbol: String,
    val name: String,
    val amount: Double,
    val averageBuyPrice: Double,
    val currentPrice: Double,
    val currentValue: Double,
    val profitLoss: Double,
    val profitLossPercent: Double,
    val priceChange24h: Double,
    val isFavorite: Boolean
)

data class PortfolioResponse(
    val success: Boolean,
    val data: PortfolioData
)

data class PortfolioData(
    val portfolio: List<PortfolioItem>,
    val totalBalanceUSD: Double,
    val lastUpdated: String
)