package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.modeldata.Buku
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BukuViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    // Menggabungkan data dari API dengan filter pencarian di sisi aplikasi
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val daftarBuku: StateFlow<List<Buku>> = _refreshTrigger
        .flatMapLatest {
            repositori.getAllBuku()
                .onStart { isLoading = true; errorMessage = null }
                .onEach { isLoading = false }
                .catch { e -> 
                    isLoading = false
                    errorMessage = "Gagal memuat data buku: ${e.message}"
                    emit(emptyList())
                }
        }
        .combine(_query) { listBuku, q ->
            if (q.isBlank()) listBuku
            else listBuku.filter {
                it.judul.contains(q, ignoreCase = true) || it.penulis.contains(q, ignoreCase = true)
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