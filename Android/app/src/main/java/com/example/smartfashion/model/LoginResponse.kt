package com.example.smartfashion.data.model

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String
)
