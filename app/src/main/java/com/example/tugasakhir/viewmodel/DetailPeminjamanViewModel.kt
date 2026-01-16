package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.data.modeldata.Peminjaman
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import com.example.tugasakhir.utils.parseErrorMessage
// PERBAIKAN: Import DestinasiDetailPeminjaman yang baru
import com.example.tugasakhir.view.route.DestinasiDetailPeminjaman
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.*

class DetailPeminjamanViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositori: RepositoriPerpustakaan
) : ViewModel() {

    // PERBAIKAN: Gunakan DestinasiDetailPeminjaman.idPeminjaman sesuai file rute Anda
    private val id: Int = checkNotNull(savedStateHandle[DestinasiDetailPeminjaman.idPeminjaman])
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var updateSuccess by mutableStateOf(false)
    var deleteSuccess by mutableStateOf(false)

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val detailUiState: StateFlow<Peminjaman?> = _refreshTrigger
        .flatMapLatest {
            repositori.getPeminjamanById(id)
                .onStart { isLoading = true; errorMessage = null }
                .onEach { isLoading = false }
                .catch { e -> 
                    isLoading = false
                    errorMessage = "Gagal memuat detail peminjaman: ${e.message}"
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun refresh() {
        _refreshTrigger.tryEmit(Unit)
    }

    // 2. State untuk mengambil data Buku berdasarkan id_buku yang ada di peminjaman
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val bukuTerkait: StateFlow<Buku?> = detailUiState
        .filterNotNull()
        .flatMapLatest { peminjaman ->
            repositori.getBukuById(peminjaman.id_buku)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // 4. Fungsi untuk Update Data
    fun updatePeminjaman(peminjaman: Peminjaman) {
        errorMessage = null
        viewModelScope.launch {
            isLoading = true
            try {
                repositori.updatePeminjaman(peminjaman)
                updateSuccess = true
                kotlinx.coroutines.delay(800)
                refresh()
            } catch (e: HttpException) {
                errorMessage = e.parseErrorMessage()
            } catch (e: Exception) {
                errorMessage = "Gagal memperbarui: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // 4. Fungsi Selesaikan Peminjaman
    fun selesaikanPeminjaman() {
        errorMessage = null
        val currentLoan = detailUiState.value ?: return
        
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Catat info buku sebelum peminjaman dihapus (selesaikan moves it to history)
                val bookId = currentLoan.id_buku
                val relatedBook = repositori.getBukuById(bookId).firstOrNull()

                // 2. Tandai Selesai (Hapus dari table peminjaman, masuk ke riwayat)
                val tglSelesai = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                repositori.selesaikanPeminjaman(id, tglSelesai)
                
                // 3. Update Stok Buku secara Pintar (Jumlah Total - Yang Sedang Dipinjam)
                if (relatedBook != null) {
                    try {
                        val allLoans = repositori.getAllPeminjaman().firstOrNull() ?: emptyList()
                        val countBorrowed = allLoans.count { it.id_buku == bookId }
                        val newTersedia = (relatedBook.jumlah_stok - countBorrowed).coerceAtLeast(0)
                        
                        repositori.updateBuku(relatedBook.copy(jumlah_tersedia = newTersedia))
                    } catch (e: Exception) {
                        // Gagal update stok tidak membatalkan sukses penyelesaian
                    }
                }

                deleteSuccess = true // Memicu navigasi kembali ke daftar
            } catch (e: HttpException) {
                errorMessage = e.parseErrorMessage()
            } catch (e: Exception) {
                errorMessage = "Gagal menyelesaikan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}