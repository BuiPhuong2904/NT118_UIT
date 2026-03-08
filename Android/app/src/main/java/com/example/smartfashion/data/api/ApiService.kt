package com.example.smartfashion.data.api
import com.example.smartfashion.data.model.LoginRequest
import com.example.smartfashion.data.model.LoginResponse
import com.example.smartfashion.data.model.RegisterRequest
import com.example.smartfashion.data.model.RegisterResponse

import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.Outfit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT

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

    // REGISTER
    // Gọi API lấy chi tiết 1 món đồ theo ID
    @GET("api/clothes/{id}")
    suspend fun getClothingById(@Path("id") id: Int): Response<Clothing>

    @POST("api/clothes")
    suspend fun addClothing(@Body clothing: Clothing): Response<Clothing>

    @PUT("api/clothes/{id}")
    suspend fun updateClothing(
        @Path("id") id: Int,
        @Body clothing: Clothing
    ): Response<Clothing>

    // Xóa 1 món đồ
    @DELETE("api/clothes/{id}")
    suspend fun deleteClothing(@Path("id") id: Int): Response<Any>

    // Gọi API lấy danh sách outfit của 1 user cụ thể
    @GET("api/outfits/user/{userId}")
    suspend fun getOutfitsByUser(@Path("userId") userId: Int): Response<OutfitResponse>

    // Gọi API lấy chi tiết 1 outfit theo ID
    @GET("api/outfits/{id}")
    suspend fun getOutfitById(@Path("id") id: Int): Response<SingleOutfitResponse>

    // Gọi API đăng ký
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
