package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.modeldata.Peminjaman
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.example.tugasakhir.utils.parseErrorMessage

class TambahPeminjamanViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var success by mutableStateOf(false)

    // Field-specific error states
    var namaError by mutableStateOf<String?>(null)
    var bukuError by mutableStateOf<String?>(null)
    var tglPinjamError by mutableStateOf<String?>(null)
    var tglTempoError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null) // New email error state

    private val _uiState = MutableStateFlow(PeminjamanUiState())
    val uiState: StateFlow<PeminjamanUiState> = _uiState.asStateFlow()

    val daftarBuku = repositori.getAllBuku()

    fun updateUiState(event: PeminjamanEvent) {
        _uiState.value = PeminjamanUiState(peminjamanEvent = event)
        // Reset errors when user types
        namaError = null
        bukuError = null
        tglPinjamError = null
        tglTempoError = null
        emailError = null
    }

    fun simpanPeminjaman() {
        val event = _uiState.value.peminjamanEvent
        
        // Final Client-side validation before sending
        var hasError = false
        if (event.nama_peminjam.isBlank()) { namaError = "Nama wajib diisi"; hasError = true }
        if (event.id_buku == 0) { bukuError = "Pilih buku terlebih dahulu"; hasError = true }
        if (event.email_peminjam.isBlank()) { 
            emailError = "Email peminjam wajib diisi"
            hasError = true 
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(event.email_peminjam).matches()) {
            emailError = "Format email tidak valid"
            hasError = true
        }
        if (event.tanggal_pinjam.isBlank()) { tglPinjamError = "Tanggal pinjam wajib diisi"; hasError = true }
        if (event.tanggal_jatuh_tempo.isBlank()) { tglTempoError = "Tanggal jatuh tempo wajib diisi"; hasError = true }
        
        if (hasError) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Cek ketersediaan buku terlebih dahulu
                val targetBuku = repositori.getBukuById(event.id_buku).firstOrNull()
                if (targetBuku == null) {
                    errorMessage = "Buku tidak ditemukan"
                    isLoading = false
                    return@launch
                }
                
                if (targetBuku.jumlah_tersedia <= 0) {
                    errorMessage = "Stok buku habis"
                    isLoading = false
                    return@launch
                }

                val peminjaman = Peminjaman(
                    id_buku = event.id_buku,
                    nama_peminjam = event.nama_peminjam.trim(),
                    email_peminjam = event.email_peminjam.trim(),
                    tanggal_pinjam = event.tanggal_pinjam,
                    tanggal_jatuh_tempo = event.tanggal_jatuh_tempo,
                )
                
                // 2. Simpan Peminjaman
                repositori.insertPeminjaman(peminjaman)
                
                // 3. Update Stok Buku (Gunakan Logika Pintar: Total - Sedang Dipinjam)
                try {
                    val allLoans = repositori.getAllPeminjaman().firstOrNull() ?: emptyList()
                    val countBorrowed = allLoans.count { it.id_buku == targetBuku.id_buku }
                    val newTersedia = (targetBuku.jumlah_stok - countBorrowed).coerceAtLeast(0)
                    
                    repositori.updateBuku(targetBuku.copy(jumlah_tersedia = newTersedia))
                } catch (e: Exception) {
                    // Penyesuaian stok gagal bukan fatal bagi transaksi utama
                }

                success = true
            } catch (e: HttpException) {
                errorMessage = e.parseErrorMessage()
            } catch (e: Exception) {
                errorMessage = "Gagal menyimpan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}

// --- DATA CLASS PENDUKUNG ---
// Ini harus ada agar UI bisa mengenali properti .peminjamanEvent
data class PeminjamanUiState(
    val peminjamanEvent: PeminjamanEvent = PeminjamanEvent()
)

data class PeminjamanEvent(
    val id_buku: Int = 0,
    val nama_peminjam: String = "",
    val email_peminjam: String = "",
    val tanggal_pinjam: String = "",
    val tanggal_jatuh_tempo: String = ""
)