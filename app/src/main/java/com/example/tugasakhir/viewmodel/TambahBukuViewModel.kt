package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import com.example.tugasakhir.utils.parseErrorMessage
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TambahBukuViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)
    
    // Field Errors
    var judulError by mutableStateOf<String?>(null)
    var penulisError by mutableStateOf<String?>(null)
    var penerbitError by mutableStateOf<String?>(null)

    fun simpanBuku(
        judul: String,
        penulis: String,
        penerbit: String,
        tahun: Int,
        stok: Int,
        onSuccess: () -> Unit = {}
    ) {
        // Reset States
        errorMessage = null
        judulError = null
        penulisError = null
        penerbitError = null
        success = false

        // Client-side validation before launching coroutine
        var hasError = false
        if (judul.isBlank()) {
            judulError = "Judul wajib diisi"
            hasError = true
        }
        if (penulis.isBlank()) {
            penulisError = "Penulis wajib diisi"
            hasError = true
        }
        if (penerbit.isBlank()) {
            penerbitError = "Penerbit wajib diisi"
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            isLoading = true
            try {
                val buku = Buku(
                    judul = judul.trim(),
                    penulis = penulis.trim(),
                    penerbit = penerbit.trim(),
                    tahun_terbit = tahun,
                    jumlah_stok = stok,
                    jumlah_tersedia = stok
                )
                
                repositori.insertBuku(buku)
                success = true
            } catch (e: HttpException) {
                val errorMsg = e.parseErrorMessage()
                val lowerMsg = errorMsg.lowercase()
                
                if (lowerMsg.contains("judul") || lowerMsg.contains("hanya spasi")) {
                    judulError = errorMsg
                } else if (lowerMsg.contains("penulis")) {
                    penulisError = errorMsg
                } else if (lowerMsg.contains("penerbit")) {
                    penerbitError = errorMsg
                } else if (lowerMsg.contains("sudah ada") || lowerMsg.contains("duplikat")) {
                    errorMessage = "Buku dengan judul dan penulis ini sudah ada"
                } else {
                    errorMessage = errorMsg
                }
            } catch (e: Exception) {
                errorMessage = "Gagal menyimpan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
