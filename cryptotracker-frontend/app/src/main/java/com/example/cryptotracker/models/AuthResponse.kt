package com.example.cryptotracker.models

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData?
)

data class AuthData(
    val token: String,
    val user: User,
    val wallet: WalletInfo? = null  // Samo pri registraciji
)

data class User(
    val id: String,
    val email: String,
    val name: String,
    val walletAddress: String? = null
)

data class WalletInfo(
    val address: String,
    val seedPhrase: String  // ⚠️ PRIKAŽI KORISNIKU I UPOZORI DA SAČUVA!
)