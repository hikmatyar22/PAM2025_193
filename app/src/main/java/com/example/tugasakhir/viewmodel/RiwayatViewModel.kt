package com.example.tugasakhir.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.modeldata.Riwayat
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.*

class RiwayatViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    var isLoading by androidx.compose.runtime.mutableStateOf(false)
    var errorMessage by androidx.compose.runtime.mutableStateOf<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val riwayatUiState: StateFlow<List<Riwayat>> = _refreshTrigger
        .flatMapLatest {
            repositori.getAllRiwayat()
                .onStart { isLoading = true; errorMessage = null }
                .onEach { isLoading = false }
                .catch { e -> 
                    isLoading = false
                    errorMessage = "Gagal memuat riwayat: ${e.message}"
                    emit(emptyList())
                }
                .combine(_query) { list, q ->
                    if (q.isBlank()) list
                    else list.filter {
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

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }
}