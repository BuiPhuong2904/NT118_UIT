package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.model.RegisterRequest
import com.example.smartfashion.data.model.RegisterResponse
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Hàm gọi API đăng ký
    suspend fun registerUser(request: RegisterRequest): Response<RegisterResponse> {
        return apiService.register(request)
    }
}