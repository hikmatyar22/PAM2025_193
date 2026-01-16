package com.example.tugasakhir.data.repositori

import android.content.Context
import com.example.tugasakhir.data.network.ApiService
import com.example.tugasakhir.utils.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ContainerApp(private val context: Context) {
    // GANTI IP DI SINI (Contoh: "http://192.168.1.5/perpustakaan_api/")
    // Pastikan akhiran "/" ada
    private val baseUrl = "http://192.168.1.16/perpustakaan_api/"

    // Interceptor untuk melihat log response di Logcat (Tag: OkHttp)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client) // Tambahkan client di sini
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    private val sessionManager: SessionManager by lazy {
        SessionManager(context)
    }

    val repositoriPerpustakaan: RepositoriPerpustakaan by lazy {
        RepositoriPerpustakaan(apiService, sessionManager)
    }
}