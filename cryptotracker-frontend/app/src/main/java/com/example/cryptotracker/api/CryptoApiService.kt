package com.example.cryptotracker.api

import com.example.cryptotracker.models.*
import retrofit2.Response
import retrofit2.http.*

interface CryptoApiService {

    // Get all crypto prices
    @GET("crypto/prices")
    suspend fun getAllPrices(): Response<CryptoResponse>

    // Get single crypto price
    @GET("crypto/price/{symbol}")
    suspend fun getSinglePrice(@Path("symbol") symbol: String): Response<CryptoResponse>

    // Get chart data
    @GET("crypto/chart/{symbol}")
    suspend fun getChartData(
        @Path("symbol") symbol: String,
        @Query("interval") interval: String = "1h",
        @Query("limit") limit: Int = 24
    ): Response<ChartResponse>

    // Get user portfolio
    @GET("crypto/portfolio/{userId}")
    suspend fun getUserPortfolio(@Path("userId") userId: String): Response<PortfolioResponse>

    // Add to portfolio
    @POST("crypto/portfolio/add")
    suspend fun addToPortfolio(@Body request: AddPortfolioRequest): Response<ApiResponse>

    // Toggle favorite
    @PUT("crypto/portfolio/favorite")
    suspend fun toggleFavorite(@Body request: ToggleFavoriteRequest): Response<ApiResponse>
}

data class ChartResponse(
    val success: Boolean,
    val data: List<ChartData>
)

data class ChartData(
    val time: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double
)

data class AddPortfolioRequest(
    val userId: String,
    val symbol: String,
    val name: String,
    val amount: Double,
    val buyPrice: Double
)

data class ToggleFavoriteRequest(
    val userId: String,
    val symbol: String
)

data class ApiResponse(
    val success: Boolean,
    val message: String
)