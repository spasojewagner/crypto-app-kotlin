package com.example.cryptotracker

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cryptotracker.repository.AuthRepository
import com.example.cryptotracker.utils.SharedPrefsManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize
        authRepository = AuthRepository()
        prefsManager = SharedPrefsManager(this)

        // Check if already logged in
        if (prefsManager.isLoggedIn()) {
            navigateToWallet()
            return
        }

        // Animacija krypto simbola
        startCryptoAnimation()

        // Setup buttons
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)

        // LOGIN BUTTON
        btnLogin.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Popuni sva polja!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button while loading
            btnLogin.isEnabled = false
            btnLogin.text = "Učitavanje..."

            // Login sa backend-om
            lifecycleScope.launch {
                try {
                    val response = authRepository.login(email, password)

                    if (response.isSuccessful && response.body()?.success == true) {
                        val authData = response.body()?.data

                        // Save token and user info
                        authData?.let {
                            prefsManager.saveAuthToken(it.token)
                            prefsManager.saveUserInfo(it.user.email, it.user.name)
                            it.user.walletAddress?.let { address ->
                                prefsManager.saveWalletAddress(address)
                            }
                        }

                        Toast.makeText(
                            this@LoginActivity,
                            "Uspešno si se ulogovao! 🚀",
                            Toast.LENGTH_SHORT
                        ).show()

                        navigateToWallet()

                    } else {
                        val errorMsg = response.body()?.message ?: "Greška pri logovanju"
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Greška: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                } finally {
                    btnLogin.isEnabled = true
                    btnLogin.text = "Uloguj se"
                }
            }
        }

        // REGISTER BUTTON - OVDE, NE UNUTAR btnLogin!
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToWallet() {
        val intent = Intent(this, WalletActivity::class.java)
        startActivity(intent)
        finish() // Zatvori LoginActivity
    }

    private fun startCryptoAnimation() {
        val bitcoin = findViewById<ImageView>(R.id.imgBitcoin)
        val ethereum = findViewById<ImageView>(R.id.imgEthereum)
        val cardano = findViewById<ImageView>(R.id.imgCardano)
        val ripple = findViewById<ImageView>(R.id.imgRipple)

        ObjectAnimator.ofFloat(bitcoin, "rotation", 0f, 360f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(ethereum, "rotation", 360f, 0f).apply {
            duration = 3500
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(cardano, "rotation", 0f, 360f).apply {
            duration = 4000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }

        ObjectAnimator.ofFloat(ripple, "rotation", 360f, 0f).apply {
            duration = 3200
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }
}