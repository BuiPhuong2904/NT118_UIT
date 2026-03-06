package com.example.smartfashion.data.api

import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.Outfit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class OutfitResponse(
    val success: Boolean,
    val data: List<Outfit>
)

data class SingleOutfitResponse(
    val success: Boolean,
    val data: Outfit
)

interface ApiService {
    // Gọi API lấy danh sách quần áo
    @GET("api/clothes")
    suspend fun getClothes(): Response<List<Clothing>>

    // Gọi API lấy chi tiết 1 món đồ theo ID
    @GET("api/clothes/{id}")
    suspend fun getClothingById(@Path("id") id: Int): Response<Clothing>

    // Gọi API lấy danh sách outfit của 1 user cụ thể
    @GET("api/outfits/user/{userId}")
    suspend fun getOutfitsByUser(@Path("userId") userId: Int): Response<OutfitResponse>

    // Gọi API lấy chi tiết 1 outfit theo ID
    @GET("api/outfits/{id}")
    suspend fun getOutfitById(@Path("id") id: Int): Response<SingleOutfitResponse>
}