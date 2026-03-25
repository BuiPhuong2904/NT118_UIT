package com.example.smartfashion.model

// ================= LỚP DÙNG CHUNG =================
data class MessageResponse(
    val message: String
)

// ================= ĐĂNG NHẬP =================
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User? = null
)

// ================= ĐĂNG KÝ =================
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val gender: String? = null
)

data class RegisterResponse(
    val message: String,
    val user: User? = null,
    val token: String? = null
)

// ================= QUÊN / ĐẶT LẠI MẬT KHẨU =================
data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)