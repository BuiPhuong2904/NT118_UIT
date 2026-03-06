package com.example.smartfashion.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val gender: String = "Khác"
)
