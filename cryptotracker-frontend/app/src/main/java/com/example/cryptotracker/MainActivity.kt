package com.example.cryptotracker

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Animacija kartica
        val card1 = findViewById<androidx.cardview.widget.CardView>(R.id.card1)
        val card2 = findViewById<androidx.cardview.widget.CardView>(R.id.card2)

        val slideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        card1.startAnimation(slideIn)
        card2.startAnimation(slideIn)

        // Button klik - otvara Login stranicu
        val btnNext = findViewById<FloatingActionButton>(R.id.btnNext)
        btnNext.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}