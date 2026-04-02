package com.example.cryptotracker.api

import com.example.cryptotracker.models.AuthResponse
import com.example.cryptotracker.models.LoginRequest
import com.example.cryptotracker.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/verify")
    suspend fun verifyToken(@Body token: Map<String, String>): Response<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<AuthResponse>
}