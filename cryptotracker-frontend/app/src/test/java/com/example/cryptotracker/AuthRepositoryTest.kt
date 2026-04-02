package com.example.cryptotracker.repository

import com.example.cryptotracker.api.ApiService
import com.example.cryptotracker.models.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

class AuthRepositoryTest {

    @Mock
    private lateinit var mockApiService: ApiService

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Ovde bi trebalo da injectujemo mock api service
        authRepository = AuthRepository()
    }

    @Test
    fun `test login success returns valid auth response`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val expectedResponse = AuthResponse(
            success = true,
            message = "Login successful",
            data = AuthData(
                token = "fake-jwt-token",
                user = User(
                    id = "user123",
                    email = email,
                    name = "Test User",
                    walletAddress = "0xABC123"
                )
            )
        )

        `when`(mockApiService.login(any())).thenReturn(Response.success(expectedResponse))

        // When
        val result = authRepository.login(email, password)

        // Then
        assertTrue(result.isSuccessful)
        assertEquals(true, result.body()?.success)
        assertEquals("fake-jwt-token", result.body()?.data?.token)
    }

    @Test
    fun `test login failure returns error message`() = runTest {
        // Given
        val email = "wrong@example.com"
        val password = "wrongpass"
        val errorResponse = AuthResponse(
            success = false,
            message = "Greška pri logovanju",
            data = null
        )

        `when`(mockApiService.login(any())).thenReturn(Response.success(errorResponse))

        // When
        val result = authRepository.login(email, password)

        // Then
        assertTrue(result.isSuccessful)
        assertEquals(false, result.body()?.success)
        assertEquals("Greška pri logovanju", result.body()?.message)
    }

    @Test
    fun `test register success returns wallet info`() = runTest {
        // Given
        val email = "novi@example.com"
        val password = "password123"
        val name = "Novi Korisnik"
        val expectedResponse = AuthResponse(
            success = true,
            message = "Registration successful",
            data = AuthData(
                token = "new-token",
                user = User(
                    id = "newuser123",
                    email = email,
                    name = name,
                    walletAddress = "0xNEW123"
                ),
                wallet = WalletInfo(
                    address = "0xNEW123",
                    seedPhrase = "word1 word2 word3 word4 word5 word6 word7 word8 word9 word10 word11 word12"
                )
            )
        )

        `when`(mockApiService.register(any())).thenReturn(Response.success(expectedResponse))

        // When
        val result = authRepository.register(email, password, name)

        // Then
        assertTrue(result.isSuccessful)
        assertEquals(true, result.body()?.success)
        assertNotNull(result.body()?.data?.wallet)
        assertEquals(12, result.body()?.data?.wallet?.seedPhrase?.split(" ")?.size)
    }

    @Test
    fun `test register with existing email returns error`() = runTest {
        // Given
        val email = "existing@example.com"
        val password = "password123"
        val name = "Existing User"
        val errorResponse = AuthResponse(
            success = false,
            message = "Email već postoji",
            data = null
        )

        `when`(mockApiService.register(any())).thenReturn(Response.success(errorResponse))

        // When
        val result = authRepository.register(email, password, name)

        // Then
        assertTrue(result.isSuccessful)
        assertEquals(false, result.body()?.success)
        assertEquals("Email već postoji", result.body()?.message)
    }

    @Test
    fun `test verify valid token returns success`() = runTest {
        // Given
        val token = "valid-jwt-token"
        val expectedResponse = AuthResponse(
            success = true,
            message = "Token validan",
            data = AuthData(
                token = token,
                user = User(
                    id = "user123",
                    email = "test@example.com",
                    name = "Test User",
                    walletAddress = "0xABC123"
                )
            )
        )

        `when`(mockApiService.verifyToken(any())).thenReturn(Response.success(expectedResponse))

        // When
        val result = authRepository.verifyToken(token)

        // Then
        assertTrue(result.isSuccessful)
        assertEquals(true, result.body()?.success)
    }

    @Test
    fun `test verify invalid token returns error`() = runTest {
        // Given
        val token = "invalid-token"
        val errorResponse = AuthResponse(
            success = false,
            message = "Token nevažeći ili istekao",
            data = null
        )

        `when`(mockApiService.verifyToken(any())).thenReturn(Response.success(errorResponse))

        // When
        val result = authRepository.verifyToken(token)

        // Then
        assertTrue(result.isSuccessful)
        assertEquals(false, result.body()?.success)
    }
}