package com.example.cryptotracker.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // TVOJA PRAVA IP ADRESA SA RAČUNARA
    private const val BASE_URL = "http://192.168.0.100:5000/api/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Stari API (ako postoji ApiService interfejs)
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // NOVI - CryptoApiService za WalletActivity
    val cryptoApi: CryptoApiService by lazy {
        retrofit.create(CryptoApiService::class.java)
    }
}