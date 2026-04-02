package com.example.cryptotracker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cryptotracker.repository.AuthRepository
import com.example.cryptotracker.utils.SharedPrefsManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var prefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        authRepository = AuthRepository()
        prefsManager = SharedPrefsManager(this)

        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val btnRegister = findViewById<MaterialButton>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Popuni sva polja!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password mora imati minimum 6 karaktera!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "Kreiranje naloga..."

            lifecycleScope.launch {
                try {
                    val response = authRepository.register(email, password, name)

                    if (response.isSuccessful && response.body()?.success == true) {
                        val authData = response.body()?.data

                        authData?.let {
                            // Sačuvaj token i user info
                            prefsManager.saveAuthToken(it.token)
                            prefsManager.saveUserInfo(it.user.email, it.user.name)

                            // 🔥 PRIKAŽI SEED PHRASE - VAŽNO!
                            it.wallet?.let { wallet ->
                                prefsManager.saveWalletAddress(wallet.address)
                                showSeedPhraseDialog(wallet.seedPhrase, wallet.address)
                            }
                        }

                    } else {
                        val errorMsg = response.body()?.message ?: "Greška pri registraciji"
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Greška: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                } finally {
                    btnRegister.isEnabled = true
                    btnRegister.text = "Registruj se"
                }
            }
        }
    }

    private fun showSeedPhraseDialog(seedPhrase: String, walletAddress: String) {
        AlertDialog.Builder(this)
            .setTitle("⚠️ VAŽNO - SAČUVAJ OVU FRAZU!")
            .setMessage(
                "Tvoj Wallet Address:\n$walletAddress\n\n" +
                        "Tvoja Seed Phrase (12 reči):\n\n" +
                        "$seedPhrase\n\n" +
                        "⚠️ ZAPIŠI OVU FRAZU NA PAPIRU!\n" +
                        "Bez nje ne možeš obnoviti wallet!\n" +
                        "Nikad je ne deli ni sa kim!"
            )
            .setCancelable(false)
            .setPositiveButton("Sačuvao sam!") { _, _ ->
                Toast.makeText(this, "Uspešna registracija! 🎉", Toast.LENGTH_SHORT).show()
                navigateToWallet()
            }
            .show()
    }

    private fun navigateToWallet() {
        val intent = Intent(this, WalletActivity::class.java)
        startActivity(intent)
        finish() // Zatvori RegisterActivity
    }
}