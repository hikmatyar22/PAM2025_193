package com.example.tugasakhir.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ADMIN_NAME = "admin_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveAdminName(name: String) {
        prefs.edit().putString(KEY_ADMIN_NAME, name).apply()
    }

    fun getAdminName(): String {
        return prefs.getString(KEY_ADMIN_NAME, "Admin") ?: "Admin"
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
