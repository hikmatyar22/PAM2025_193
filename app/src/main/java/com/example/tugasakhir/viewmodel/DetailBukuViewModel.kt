
package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import com.example.tugasakhir.utils.parseErrorMessage
// Import DestinasiDetailBuku yang baru
import com.example.tugasakhir.view.route.DestinasiDetailBuku
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailBukuViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositori: RepositoriPerpustakaan
) : ViewModel() {

    // PERBAIKAN: Gunakan DestinasiDetailBuku.idBuku sesuai dengan file rute Anda
    private val bukuId: Int = checkNotNull(savedStateHandle[DestinasiDetailBuku.idBuku])

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var updateSuccess by mutableStateOf(false)
    var deleteSuccess by mutableStateOf(false)

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val detailUiState: StateFlow<Buku?> = _refreshTrigger
        .flatMapLatest {
            repositori.getBukuById(bukuId)
                .onStart { isLoading = true; errorMessage = null }
                .onEach { isLoading = false }
                .catch { e -> 
                    isLoading = false
                    errorMessage = "Gagal memuat detail buku: ${e.message}"
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun refresh() {
        _refreshTrigger.tryEmit(Unit)
    }

    fun updateBuku(buku: Buku) {
        errorMessage = null
        updateSuccess = false
        
        viewModelScope.launch {
            isLoading = true
            try {
                // LOGIK: Hitung ulang jumlah_tersedia berdasarkan (Stok Baru - Sedang Dipinjam)
                val allLoans = repositori.getAllPeminjaman().first()
                val currentlyBorrowed = allLoans.count { it.id_buku == buku.id_buku }
                
                // Rumus: Stok Baru (Input Admin) - Buku yang masih dibawa peminjam
                val recalculatedTersedia = (buku.jumlah_stok - currentlyBorrowed).coerceAtLeast(0)
                
                val updatedBuku = buku.copy(jumlah_tersedia = recalculatedTersedia)
                
                repositori.updateBuku(updatedBuku)
                updateSuccess = true
                refresh() // Refresh data after update
            } catch (e: HttpException) {
                errorMessage = e.parseErrorMessage()
            } catch (e: Exception) {
                errorMessage = "Gagal memperbarui: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun hapusBuku() {
        errorMessage = null
        deleteSuccess = false
        
        viewModelScope.launch {
            isLoading = true
            try {
                repositori.deleteBuku(bukuId)
                deleteSuccess = true
            } catch (e: HttpException) {
                val errorMsg = e.parseErrorMessage()
                if (errorMsg.contains("transaksi") || errorMsg.contains("aktif")) {
                    errorMessage = "Buku tidak bisa dihapus karena masih dalam transaksi peminjaman aktif"
                } else {
                    errorMessage = errorMsg
                }
            } catch (e: Exception) {
                errorMessage = "Gagal menghapus: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}