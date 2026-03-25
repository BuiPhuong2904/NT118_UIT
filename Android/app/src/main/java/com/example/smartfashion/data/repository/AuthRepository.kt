package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
// Import các model mà bạn đã gom lại ở bước trước
import com.example.smartfashion.model.*
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    // 1. Hàm gọi API Đăng ký
    suspend fun registerUser(request: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(request)
    }

    // 2. Hàm gọi API Đăng nhập
    suspend fun loginUser(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    // 3. Hàm gọi API Quên mật khẩu (Gửi email chứa OTP)
    suspend fun forgotPassword(request: ForgotPasswordRequest): Response<MessageResponse> {
        return apiService.forgotPassword(request)
    }

    // 4. Hàm gọi API Đặt lại mật khẩu (Gửi OTP + Mật khẩu mới)
    suspend fun resetPassword(request: ResetPasswordRequest): Response<MessageResponse> {
        return apiService.resetPassword(request)
    }
}