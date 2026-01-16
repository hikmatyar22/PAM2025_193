package com.example.tugasakhir.data.modeldata


import com.google.gson.annotations.SerializedName

// Perbaiki di PerpustakaanModel.kt
// File: data/modeldata/PerpustakaanModel.kt
data class Admin(
    val id_admin: Int = 0,
    val username: String,
    @SerializedName("password")
    val password_hash: String? = null, // Gunakan String? agar tidak error saat field kosong
    val email: String
)
data class Buku(
    val id_buku: Int = 0,
    val judul: String,
    val penulis: String,
    val penerbit: String,
    val tahun_terbit: Int,
    val jumlah_stok: Int,
    val jumlah_tersedia: Int
)

data class Peminjaman(
    val id_peminjaman: Int = 0,
    val id_buku: Int,
    val nama_peminjam: String,
    val email_peminjam: String?,
    val tanggal_pinjam: String,
    val tanggal_jatuh_tempo: String,
    @SerializedName("judul")
    val judul_buku: String? = "" // Nullable to prevent NPE from GSON/Copy
)

data class Riwayat(
    val id_riwayat: Int = 0,
    val id_buku: Int,
    val nama_peminjam: String,
    val tanggal_pinjam: String,
    val tanggal_jatuh_tempo: String,
    val tanggal_kembali: String,
    val status: String = "SELESAI",
    @SerializedName("judul")
    val judul_buku: String? = "", // New field for UI display
    val penulis: String? = null,
    val penerbit: String? = null
)