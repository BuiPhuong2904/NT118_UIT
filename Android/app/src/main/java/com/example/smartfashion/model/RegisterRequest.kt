package com.example.smartfashion.data.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val gender: String = "Khác"
)
