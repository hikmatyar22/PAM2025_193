package com.example.tugasakhir.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhir.data.modeldata.Admin
import com.example.tugasakhir.data.repositori.RepositoriPerpustakaan
import com.example.tugasakhir.utils.parseErrorMessage
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(private val repositori: RepositoriPerpustakaan) : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var email by mutableStateOf("")

    var usernameError by mutableStateOf<String?>(null)
    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    // PERBAIKAN: Tambahkan variabel success agar bisa dibaca oleh HalamanRegister.kt
    var success by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun register() {
        // Reset Errors
        usernameError = null
        emailError = null
        passwordError = null
        errorMessage = null

        var hasError = false

        // 1. Validasi Input Lokal
        if (username.isBlank()) {
            usernameError = "Username wajib diisi"
            hasError = true
        }

        if (email.isBlank()) {
            emailError = "Email wajib diisi"
            hasError = true
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Format email tidak valid"
            hasError = true
        }

        if (password.isBlank()) {
            passwordError = "Password wajib diisi"
            hasError = true
        } else if (password.length < 6) {
            passwordError = "Password minimal 6 karakter"
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null 
            try {
                val admin = Admin(username = username, password_hash = password, email = email)
                repositori.insertAdmin(admin)

                success = true
            } catch (e: HttpException) {
                // Tangkap error dari server (misal: 400 Bad Request, Username exists)
                errorMessage = e.parseErrorMessage()
            } catch (e: Exception) {
                errorMessage = "Registrasi Gagal: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}