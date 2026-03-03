package com.example.smartfashion.data.api

import com.example.smartfashion.data.model.RegisterRequest
import com.example.smartfashion.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}
