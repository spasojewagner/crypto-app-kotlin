package com.example.cryptotracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptotracker.adapters.CryptoAdapter
import com.example.cryptotracker.api.RetrofitClient
import com.example.cryptotracker.api.ToggleFavoriteRequest
import com.example.cryptotracker.models.CryptoPrice
import com.example.cryptotracker.utils.SharedPrefsManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.util.*

class WalletActivity : AppCompatActivity() {

    private lateinit var prefsManager: SharedPrefsManager
    private lateinit var cryptoAdapter: CryptoAdapter
    private lateinit var rvCryptoList: RecyclerView
    private lateinit var tvTotalBalance: TextView
    private lateinit var tvBalanceChange: TextView
    private lateinit var btnBuy: MaterialCardView
    private lateinit var btnSell: MaterialCardView
    private lateinit var btnDeposit: MaterialCardView
    private lateinit var btnSend: MaterialCardView
    private lateinit var fabTests: FloatingActionButton
    private lateinit var ivLogout: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        prefsManager = SharedPrefsManager(this)

        initViews()
        setupRecyclerView()
        setupActionButtons()
        setupTestButton()
        setupLogoutButton()

        loadCryptoPrices()
        startAutoRefresh()
    }

    private fun initViews() {
        tvTotalBalance = findViewById(R.id.tvTotalBalance)
        tvBalanceChange = findViewById(R.id.tvBalanceChange)
        rvCryptoList = findViewById(R.id.rvCryptoList)
        btnBuy = findViewById(R.id.btnBuy)
        btnSell = findViewById(R.id.btnSell)
        btnDeposit = findViewById(R.id.btnDeposit)
        btnSend = findViewById(R.id.btnSend)
        fabTests = findViewById(R.id.fabTests)
        ivLogout = findViewById(R.id.ivLogout)
    }

    private fun setupRecyclerView() {
        cryptoAdapter = CryptoAdapter(
            onItemClick = { crypto: CryptoPrice ->
                Toast.makeText(this, "Clicked: ${crypto.name}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { crypto: CryptoPrice ->
                toggleFavorite(crypto)
            }
        )

        rvCryptoList.apply {
            layoutManager = LinearLayoutManager(this@WalletActivity)
            adapter = cryptoAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupActionButtons() {
        btnBuy.setOnClickListener {
            Toast.makeText(this, "Buy clicked", Toast.LENGTH_SHORT).show()
        }

        btnSell.setOnClickListener {
            Toast.makeText(this, "Sell clicked", Toast.LENGTH_SHORT).show()
        }

        btnDeposit.setOnClickListener {
            Toast.makeText(this, "Deposit clicked", Toast.LENGTH_SHORT).show()
        }

        btnSend.setOnClickListener {
            Toast.makeText(this, "Send clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTestButton() {
        fabTests.setOnClickListener {
            val intent = Intent(this, TestResultsActivity::class.java)
            startActivity(intent)
        }
    }

    // 🔥 LOGOUT FUNKCIONALNOST
    private fun setupLogoutButton() {
        ivLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Da li si siguran da želiš da se odjaviš?")
            .setPositiveButton("Da") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Ne", null)
            .show()
    }

    private fun performLogout() {
        scope.launch {
            try {
                val token = prefsManager.getAuthToken()

                if (!token.isNullOrEmpty()) {
                    // Pozovi backend logout (opciono)
                    withContext(Dispatchers.IO) {
                        try {
                            RetrofitClient.apiService.logout("Bearer $token")
                        } catch (e: Exception) {
                            // Backend logout failed, ali nastavi sa lokalnim logout-om
                            e.printStackTrace()
                        }
                    }
                }

                // Obriši sve lokalne podatke
                prefsManager.clearAll()

                // Zaustavi auto-refresh
                updateRunnable?.let { handler.removeCallbacks(it) }

                // Idi na Login ekran
                Toast.makeText(this@WalletActivity, "Uspešno si se odjavio!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@WalletActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@WalletActivity,
                    "Greška pri odjavi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun loadCryptoPrices() {
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.cryptoApi.getAllPrices()
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val prices = response.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {
                        cryptoAdapter.submitList(prices.toMutableList())

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (rvCryptoList.childCount == 0) {
                                cryptoAdapter.notifyDataSetChanged()
                                rvCryptoList.requestLayout()
                            }
                        }, 500)
                    }

                    calculateTotalBalance(prices)

                } else {
                    Toast.makeText(
                        this@WalletActivity,
                        "Failed to load prices",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@WalletActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun calculateTotalBalance(prices: List<CryptoPrice>) {
        var total = 0.0
        var change24h = 0.0

        val holdings = mapOf(
            "BTC" to 0.05,
            "ETH" to 1.2,
            "SOL" to 100.0
        )

        prices.forEach { crypto ->
            val amount = holdings[crypto.symbol] ?: 0.0
            total += amount * crypto.price
            change24h += amount * crypto.priceChange24h
        }

        val balanceFormat = NumberFormat.getCurrencyInstance(Locale.US)
        tvTotalBalance.text = balanceFormat.format(total)

        val changePercent = if (total > 0) (change24h / total) * 100 else 0.0
        val changeText = String.format(
            "%s (%.2f%%) 24H",
            if (change24h >= 0) "+${balanceFormat.format(change24h)}" else balanceFormat.format(change24h),
            changePercent
        )
        tvBalanceChange.text = changeText

        val color = if (change24h >= 0) {
            getColor(R.color.green_profit)
        } else {
            getColor(R.color.red_loss)
        }
        tvBalanceChange.setTextColor(color)
    }

    private fun toggleFavorite(crypto: CryptoPrice) {
        val userId = prefsManager.getUserId() ?: return

        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.cryptoApi.toggleFavorite(
                        ToggleFavoriteRequest(userId, crypto.symbol)
                    )
                }

                if (response.isSuccessful) {
                    val currentList = cryptoAdapter.currentList.toMutableList()
                    val index = currentList.indexOfFirst { it.symbol == crypto.symbol }

                    if (index != -1) {
                        currentList[index] = crypto.copy(isFavorite = !crypto.isFavorite)
                        cryptoAdapter.submitList(currentList)

                        Toast.makeText(
                            this@WalletActivity,
                            if (crypto.isFavorite) "Removed from favorites" else "Added to favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@WalletActivity,
                    "Error updating favorite",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startAutoRefresh() {
        updateRunnable = Runnable {
            loadCryptoPrices()
            handler.postDelayed(updateRunnable!!, 30000)
        }
        handler.post(updateRunnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateRunnable?.let { handler.removeCallbacks(it) }
        scope.cancel()
    }
}