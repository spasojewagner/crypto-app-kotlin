package com.example.cryptotracker.repository

import com.example.cryptotracker.api.RetrofitClient
import com.example.cryptotracker.models.AuthResponse
import com.example.cryptotracker.models.LoginRequest
import com.example.cryptotracker.models.RegisterRequest
import retrofit2.Response

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun login(email: String, password: String): Response<AuthResponse> {
        return api.login(LoginRequest(email, password))
    }

    suspend fun register(email: String, password: String, name: String): Response<AuthResponse> {
        return api.register(RegisterRequest(email, password, name))
    }

    suspend fun verifyToken(token: String): Response<AuthResponse> {
        return api.verifyToken(mapOf("token" to token))
    }
}