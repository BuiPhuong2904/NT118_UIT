package com.example.smartfashion.data.model

data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)
