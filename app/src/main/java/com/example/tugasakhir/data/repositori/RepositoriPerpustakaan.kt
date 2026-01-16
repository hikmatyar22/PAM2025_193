package com.example.tugasakhir.data.repositori

import com.example.tugasakhir.data.modeldata.Admin
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.data.modeldata.Peminjaman
import com.example.tugasakhir.data.modeldata.Riwayat
import com.example.tugasakhir.data.network.ApiService
import com.example.tugasakhir.utils.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class RepositoriPerpustakaan(
    private val apiService: ApiService,
    val sessionManager: SessionManager
) {

    // === ADMIN ===
    // Di RepositoriPerpustakaan.kt
    // Ubah di RepositoriPerpustakaan.kt
    suspend fun login(loginData: Map<String, String>): Admin? = apiService.login(loginData)
    suspend fun countAdmin(): Int = apiService.countAdmin()
    suspend fun insertAdmin(admin: Admin) = apiService.insertAdmin(admin)

    // === BUKU ===
    fun getAllBuku(): Flow<List<Buku>> = flow {
        emit(apiService.getAllBuku())
    }

    fun getBukuById(id: Int): Flow<Buku?> = flow {
        emit(apiService.getBukuById(id))
    }

    fun searchBuku(keyword: String): Flow<List<Buku>> = flow {
        emit(apiService.searchBuku(keyword))
    }

    suspend fun insertBuku(buku: Buku) = apiService.insertBuku(buku)
    suspend fun updateBuku(buku: Buku) = apiService.updateBuku(buku)
    suspend fun deleteBuku(id: Int) = apiService.deleteBuku(id)

    // === PEMINJAMAN ===
    fun getAllPeminjaman(): Flow<List<Peminjaman>> = flow {
        emit(apiService.getAllPeminjaman())
    }

    fun getPeminjamanById(id: Int): Flow<Peminjaman?> = flow {
        emit(apiService.getPeminjamanById(id))
    }

    suspend fun insertPeminjaman(peminjaman: Peminjaman) = apiService.insertPeminjaman(peminjaman)

    // PERBAIKAN: Menambahkan fungsi update yang dicari oleh ViewModel
    suspend fun updatePeminjaman(peminjaman: Peminjaman) {
        // Karena ApiService hanya menerima @Body, cukup kirim 1 argumen saja
        apiService.updatePeminjaman(peminjaman)
    }

    suspend fun selesaikanPeminjaman(id: Int, tanggalKembali: String) {
        apiService.selesaikanPeminjaman(id, tanggalKembali)
    }

    // === RIWAYAT ===
    // === RIWAYAT ===
    fun getAllRiwayat(): Flow<List<Riwayat>> = flow {
        emit(apiService.getAllRiwayat())
    }

    fun getRiwayatById(id: Int): Flow<Riwayat?> = flow {
        emit(apiService.getRiwayatById(id))
    }
}