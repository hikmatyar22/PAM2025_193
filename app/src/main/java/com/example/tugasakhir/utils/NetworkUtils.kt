package com.example.tugasakhir.utils

import org.json.JSONObject
import retrofit2.HttpException

fun HttpException.parseErrorMessage(): String {
    return try {
        val errorBody = response()?.errorBody()?.string()
        if (errorBody != null) {
            val jsonObject = JSONObject(errorBody)
            jsonObject.getString("message")
        } else {
            "Terjadi kesalahan pada server"
        }
    } catch (e: Exception) {
        "Gagal terhubung ke server: ${e.message}"
    }
}
