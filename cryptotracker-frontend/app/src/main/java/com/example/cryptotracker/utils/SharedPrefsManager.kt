package com.example.cryptotracker.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("CryptoTrackerPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_WALLET_ADDRESS = "wallet_address"
    }

    // Save auth token
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    // Get auth token
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    // Save user info
    fun saveUserInfo(email: String, name: String, userId: String? = null) {
        prefs.edit().apply {
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            userId?.let { putString(KEY_USER_ID, it) }
        }.apply()
    }

    // Get user email
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    // Get user name
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    // Get user ID
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    // Save wallet address
    fun saveWalletAddress(address: String) {
        prefs.edit().putString(KEY_WALLET_ADDRESS, address).apply()
    }

    // Get wallet address
    fun getWalletAddress(): String? {
        return prefs.getString(KEY_WALLET_ADDRESS, null)
    }

    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrEmpty()
    }

    // 🔥 LOGOUT - Obriši sve podatke
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}