package com.example.smartfashion.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit { putString("USER_TOKEN", token) }
    }
    fun getToken(): String? {
        return prefs.getString("USER_TOKEN", null)
    }
    fun clearToken() {
        prefs.edit { clear() }
    }

    fun saveUserId(userId: Int) {
        prefs.edit { putInt("USER_ID", userId) }
    }
    fun getUserId(): Int {
        return prefs.getInt("USER_ID", -1)
    }

    fun saveUsername(username: String) {
        prefs.edit { putString("USERNAME", username) }
    }
    fun getUsername(): String {
        return prefs.getString("USERNAME", "Người dùng") ?: "Người dùng"
    }
}