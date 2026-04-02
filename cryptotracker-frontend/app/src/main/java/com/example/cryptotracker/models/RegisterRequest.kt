package com.example.cryptotracker.models

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)