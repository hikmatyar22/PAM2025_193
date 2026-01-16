package com.example.tugasakhir

import android.app.Application
// PERBAIKAN: Tambahkan .data agar sesuai dengan package ContainerApp yang sebenarnya
import com.example.tugasakhir.data.repositori.ContainerApp

class TugasAkhirApplication : Application() {

    // Menggunakan by lazy agar lebih efisien (memory safe)
    val container: ContainerApp by lazy {
        ContainerApp(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}