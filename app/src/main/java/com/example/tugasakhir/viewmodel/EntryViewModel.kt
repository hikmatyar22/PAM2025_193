package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import com.example.tugasakhir.utils.parseErrorMessage
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EntryViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var usernameError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    // Di EntryViewModel.kt
    // Perbaikan fungsi login di EntryViewModel.kt
    // File: viewmodel/EntryViewModel.kt
    var loginSuccess by mutableStateOf(false)

    fun login(onSuccess: () -> Unit) {
        // Reset errors
        usernameError = null
        passwordError = null
        errorMessage = null
        loginSuccess = false

        var hasError = false

        if (username.isBlank()) {
            usernameError = "Username wajib diisi"
            hasError = true
        }

        if (password.isBlank()) {
            passwordError = "Password wajib diisi"
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            isLoading = true
            try {
                val requestBody = mapOf(
                    "username" to username.trim(),
                    "password" to password.trim()
                )

                val admin = repositori.login(requestBody)

                if (admin != null) {
                    // Simpan nama admin ke session
                    repositori.sessionManager.saveAdminName(admin.username)
                    repositori.sessionManager.setLoggedIn(true)
                    
                    // Login Sukses
                    loginSuccess = true
                    // Delay sebentar di UI sebelum navigasi
                } else {
                    errorMessage = "Gagal memproses data akun"
                }
            } catch (e: HttpException) {
                // Tangkap error dan parsing
                val errorMsg = e.parseErrorMessage()
                val lowerMsg = errorMsg.lowercase()

                if (lowerMsg.contains("user") || lowerMsg.contains("tidak ditemukan") || lowerMsg.contains("unregistered")) {
                    usernameError = "Username tidak ditemukan"
                } else if (lowerMsg.contains("password") || lowerMsg.contains("salah") || lowerMsg.contains("invalid")) {
                    passwordError = "Password salah"
                } else {
                    errorMessage = errorMsg
                }
            } catch (e: Exception) {
                errorMessage = "Gagal Login: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}