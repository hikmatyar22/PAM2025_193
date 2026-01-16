package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.modeldata.Peminjaman
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import kotlinx.coroutines.flow.*

class PeminjamanViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    var isLoading by androidx.compose.runtime.mutableStateOf(false)
    var errorMessage by androidx.compose.runtime.mutableStateOf<String?>(null)

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val daftarPeminjaman: StateFlow<List<Peminjaman>> = _refreshTrigger
        .flatMapLatest {
            combine(
                repositori.getAllPeminjaman()
                    .onStart { isLoading = true; errorMessage = null }
                    .onEach { isLoading = false }
                    .catch { e -> 
                        isLoading = false
                        errorMessage = "Gagal memuat data peminjaman: ${e.message}"
                        emit(emptyList())
                    },
                repositori.getAllBuku().catch { emit(emptyList()) },
                _query
            ) { listPeminjaman, listBuku, q ->
                // Gabungkan data untuk mendapatkan judul buku
                val joinedList = listPeminjaman.map { peminjaman ->
                    val buku = listBuku.find { it.id_buku == peminjaman.id_buku }
                    peminjaman.copy(judul_buku = buku?.judul ?: "Buku tidak ditemukan")
                }

                // Filter berdasarkan query pencarian
                if (q.isBlank()) joinedList
                else joinedList.filter { 
                    it.nama_peminjam.contains(q, ignoreCase = true) || 
                    (it.judul_buku?.contains(q, ignoreCase = true) == true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    fun refresh() {
        _refreshTrigger.tryEmit(Unit)
    }
}