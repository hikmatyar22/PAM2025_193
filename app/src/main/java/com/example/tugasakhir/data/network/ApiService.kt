package com.example.tugasakhir.data.network

import com.example.tugasakhir.data.modeldata.Admin
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.data.modeldata.Peminjaman
import com.example.tugasakhir.data.modeldata.Riwayat
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("admin/admin_login.php")
    suspend fun login(@Body loginData: Map<String, String>): Admin?

    @GET("admin/admin_dashboard.php") // Di gambar folder Anda namanya admin_dashboard.php
    suspend fun countAdmin(): Int

    @POST("admin/admin_register.php")
    suspend fun insertAdmin(@Body admin: Admin): Unit

    // === BUKU ===
    @GET("buku/buku_get_all.php")
    suspend fun getAllBuku(): List<Buku>

    // Fitur Get By ID untuk Buku
    @GET("buku/buku_get_by_id.php")
    suspend fun getBukuById(@Query("id") id: Int): Buku?

    // Fitur Search untuk Buku
    @GET("buku/buku_search.php")
    suspend fun searchBuku(@Query("keyword") keyword: String): List<Buku>

    @POST("buku/buku_insert.php")
    suspend fun insertBuku(@Body buku: Buku): Unit

    @POST("buku/buku_update.php")
    suspend fun updateBuku(@Body buku: Buku): Unit

    @GET("buku/buku_delete.php")
    suspend fun deleteBuku(@Query("id") id: Int): Unit

    // === PEMINJAMAN ===
    @GET("peminjaman/peminjaman_get_all.php")
    suspend fun getAllPeminjaman(): List<Peminjaman>

    @GET("peminjaman/peminjaman_get_by_id.php")
    suspend fun getPeminjamanById(@Query("id") id: Int): Peminjaman?

    @POST("peminjaman/peminjaman_insert.php")
    suspend fun insertPeminjaman(@Body peminjaman: Peminjaman): Unit

    // Menggunakan nama file sesuai folder: peminjaman_update.php
    @POST("peminjaman/peminjaman_update.php")
    suspend fun updatePeminjaman(@Body peminjaman: Peminjaman): Unit

    // Logika Selesaikan Peminjaman (biasanya diarahkan ke script khusus)
    @GET("peminjaman/peminjaman_delete.php") // <--- Pastikan nama file ini benar sesuai di hosting/XAMPP
    suspend fun selesaikanPeminjaman(
        @Query("id_peminjaman") idPeminjaman: Int,
        @Query("tanggal_kembali") tanggalKembali: String
    ): Unit

    // === RIWAYAT ===
    @GET("riwayat/riwayat_get_all.php")
    suspend fun getAllRiwayat(): List<Riwayat>

    @GET("riwayat/riwayat_get_by_id.php")
    suspend fun getRiwayatById(@Query("id") id: Int): Riwayat?
}