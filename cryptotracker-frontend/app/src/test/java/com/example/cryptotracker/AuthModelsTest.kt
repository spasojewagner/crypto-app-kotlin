package com.example.cryptotracker.models

import org.junit.Assert.*
import org.junit.Test

class AuthModelsTest {

    @Test
    fun `test AuthResponse with successful login`() {
        // Given & When
        val response = AuthResponse(
            success = true,
            message = "Login successful",
            data = AuthData(
                token = "jwt-token",
                user = User(
                    id = "123",
                    email = "test@example.com",
                    name = "Test User",
                    walletAddress = "0xABC"
                )
            )
        )

        // Then
        assertTrue(response.success)
        assertEquals("Login successful", response.message)
        assertNotNull(response.data)
        assertEquals("jwt-token", response.data?.token)
    }

    @Test
    fun `test AuthResponse with failed login`() {
        // Given & When
        val response = AuthResponse(
            success = false,
            message = "Pogrešna lozinka",
            data = null
        )

        // Then
        assertFalse(response.success)
        assertEquals("Pogrešna lozinka", response.message)
        assertNull(response.data)
    }

    @Test
    fun `test AuthData with wallet info on registration`() {
        // Given & When
        val authData = AuthData(
            token = "new-token",
            user = User(
                id = "456",
                email = "novi@example.com",
                name = "Novi Korisnik",
                walletAddress = "0xNEW"
            ),
            wallet = WalletInfo(
                address = "0xNEW",
                seedPhrase = "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12"
            )
        )

        // Then
        assertNotNull(authData.wallet)
        assertEquals("0xNEW", authData.wallet?.address)
        val wordCount = authData.wallet?.seedPhrase?.split(" ")?.size
        assertEquals(12, wordCount)
    }

    @Test
    fun `test User model with all fields`() {
        // Given & When
        val user = User(
            id = "user123",
            email = "korisnik@example.com",
            name = "Marko Marković",
            walletAddress = "0x123ABC"
        )

        // Then
        assertEquals("user123", user.id)
        assertEquals("korisnik@example.com", user.email)
        assertEquals("Marko Marković", user.name)
        assertEquals("0x123ABC", user.walletAddress)
    }

    @Test
    fun `test User model with null wallet address`() {
        // Given & When
        val user = User(
            id = "user456",
            email = "novi@example.com",
            name = "Novi Korisnik",
            walletAddress = null
        )

        // Then
        assertNull(user.walletAddress)
    }

    @Test
    fun `test WalletInfo contains valid seed phrase`() {
        // Given & When
        val wallet = WalletInfo(
            address = "0xWALLET123",
            seedPhrase = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
        )

        // Then
        assertEquals("0xWALLET123", wallet.address)
        assertFalse(wallet.seedPhrase.isEmpty())
        assertTrue(wallet.seedPhrase.contains(" "))
    }

    @Test
    fun `test LoginRequest model`() {
        // Given & When
        val loginRequest = LoginRequest(
            email = "test@example.com",
            password = "password123"
        )

        // Then
        assertEquals("test@example.com", loginRequest.email)
        assertEquals("password123", loginRequest.password)
    }

    @Test
    fun `test RegisterRequest model`() {
        // Given & When
        val registerRequest = RegisterRequest(
            email = "novi@example.com",
            password = "lozinka123",
            name = "Novi Korisnik"
        )

        // Then
        assertEquals("novi@example.com", registerRequest.email)
        assertEquals("lozinka123", registerRequest.password)
        assertEquals("Novi Korisnik", registerRequest.name)
    }
}