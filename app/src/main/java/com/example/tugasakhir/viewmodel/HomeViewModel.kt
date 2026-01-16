package com.example.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch



// Pastikan nama variabelnya adalah namaAdmin (case-sensitive)
// Pastikan nama variabelnya adalah namaAdmin (case-sensitive)
data class HomeUiState(
    val namaAdmin: String = "Admin",
    val jumlahBuku: Int = 0,
    val jumlahDipinjam: Int = 0,
    val jumlahSelesai: Int = 0,
    val isError: Boolean = false,
    val isLoading: Boolean = true
)



@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {

    // Trigger untuk refresh/retry
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        // Trigger awal saat ViewModel dibuat
        refresh()
    }

    val homeUiState: StateFlow<HomeUiState> = _refreshTrigger
        .flatMapLatest {
            combine(
                repositori.getAllBuku(),
                repositori.getAllPeminjaman(),
                repositori.getAllRiwayat()
            ) { buku, pinjam, riwayat ->
                HomeUiState(
                    namaAdmin = repositori.sessionManager.getAdminName(),
                    jumlahBuku = buku.size,
                    jumlahDipinjam = pinjam.size,
                    jumlahSelesai = riwayat.size,
                    isError = false,
                    isLoading = false
                )
            }
        }
        .onStart {
            emit(HomeUiState(isLoading = true))
        }
        .catch {
            emit(HomeUiState(isError = true, isLoading = false))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )

    fun refresh() {
        viewModelScope.launch {
            _refreshTrigger.emit(Unit)
        }
    }
}