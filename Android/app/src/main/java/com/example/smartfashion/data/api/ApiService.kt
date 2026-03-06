package com.example.smartfashion.data.api
import com.example.smartfashion.data.model.LoginRequest
import com.example.smartfashion.data.model.LoginResponse
import com.example.smartfashion.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // REGISTER
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    // LOGIN
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}
