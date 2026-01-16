package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.modeldata.Riwayat
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import com.example.tugasakhir.view.route.DestinasiDetailRiwayat
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailRiwayatViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositori: RepositoriPerpustakaan
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle[DestinasiDetailRiwayat.idRiwayat])

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val detailUiState: StateFlow<Riwayat?> = _refreshTrigger
        .flatMapLatest { _ ->
            repositori.getRiwayatById(id)
                .onStart { isLoading = true; errorMessage = null }
                .onEach { isLoading = false }
                .catch { e -> 
                    isLoading = false
                    errorMessage = "Gagal memuat detail riwayat: ${e.message}"
                    emit(null)
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun refresh() {
        _refreshTrigger.tryEmit(Unit)
    }
}
