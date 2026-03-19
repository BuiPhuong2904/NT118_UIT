package com.example.smartfashion.data.api

import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.Outfit
import com.example.smartfashion.model.RegisterRequest
import com.example.smartfashion.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

data class OutfitResponse(
    val success: Boolean,
    val data: List<Outfit>
)

data class FavoriteRequest(
    val is_favorite: Boolean
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

    // Gọi API lấy danh sách outfit của 1 user cụ thể (có hỗ trợ lọc)
    @GET("api/outfits/user/{userId}")
    suspend fun getOutfitsByUser(
        @Path("userId") userId: Int,
        @Query("is_favorite") isFavorite: Boolean? = null,
        @Query("tags") tags: List<String>? = null
    ): Response<OutfitResponse>

    // Gọi API lấy chi tiết 1 outfit theo ID
    @GET("api/outfits/{id}")
    suspend fun getOutfitById(@Path("id") id: Int): Response<SingleOutfitResponse>

    // Gọi API cập nhật trạng thái yêu thích
    @PUT("api/outfits/{id}/favorite")
    suspend fun updateFavoriteStatus(
        @Path("id") id: Int,
        @Body request: FavoriteRequest
    ): Response<SingleOutfitResponse>

    // Gọi API đăng ký
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}
