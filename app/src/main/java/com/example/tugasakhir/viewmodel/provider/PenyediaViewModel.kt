package com.example.tugasakhir.viewmodel.provider

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.tugasakhir.TugasAkhirApplication
import com.example.tugasakhir.viewmodel.*

class PenyediaViewModel : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        // Mengambil instance aplikasi untuk mendapatkan container repositori
        val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TugasAkhirApplication
        val repositori = application.container.repositoriPerpustakaan

        // Membuat SavedStateHandle secara otomatis dari extras jika diperlukan (untuk Detail Screen)
        val savedStateHandle = extras.createSavedStateHandle()

        return when {
            modelClass.isAssignableFrom(EntryViewModel::class.java) ->
                EntryViewModel(repositori) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(repositori) as T

            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(repositori) as T

            modelClass.isAssignableFrom(BukuViewModel::class.java) ->
                BukuViewModel(repositori) as T

            modelClass.isAssignableFrom(TambahBukuViewModel::class.java) ->
                TambahBukuViewModel(repositori) as T

            modelClass.isAssignableFrom(DetailBukuViewModel::class.java) ->
                DetailBukuViewModel(savedStateHandle, repositori) as T

            modelClass.isAssignableFrom(PeminjamanViewModel::class.java) ->
                PeminjamanViewModel(repositori) as T

            modelClass.isAssignableFrom(TambahPeminjamanViewModel::class.java) ->
                TambahPeminjamanViewModel(repositori) as T

            modelClass.isAssignableFrom(DetailPeminjamanViewModel::class.java) ->
                DetailPeminjamanViewModel(savedStateHandle, repositori) as T

            modelClass.isAssignableFrom(RiwayatViewModel::class.java) ->
                RiwayatViewModel(repositori) as T

            modelClass.isAssignableFrom(DetailRiwayatViewModel::class.java) ->
                DetailRiwayatViewModel(savedStateHandle, repositori) as T

            else -> throw IllegalArgumentException("ViewModel Unknown: " + modelClass.name)
        }
    }
}