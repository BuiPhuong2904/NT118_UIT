package com.example.smartfashion.data.api

import com.example.smartfashion.model.Clothing
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // Gọi API lấy danh sách quần áo
    @GET("api/clothes")
    suspend fun getClothes(): Response<List<Clothing>>

    // Gọi API lấy chi tiết 1 món đồ theo ID
    @GET("api/clothes/{id}")
    suspend fun getClothingById(@Path("id") id: Int): Response<Clothing>
}