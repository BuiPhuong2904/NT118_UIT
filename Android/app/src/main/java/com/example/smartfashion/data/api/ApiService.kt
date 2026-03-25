package com.example.smartfashion.data.api

import com.example.smartfashion.model.*
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
    // Lấy quần áo theo ID của User
    @GET("api/clothes/user/{userId}")
    suspend fun getClothesByUserId(
        @Path("userId") userId: Int,
        @Query("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<Clothing>>

    // Gọi API lấy danh sách quần áo
    @GET("api/clothes")
    suspend fun getClothes(): Response<List<Clothing>>

    // REGISTER
    // Gọi API lấy chi tiết 1 món đồ theo ID
    @GET("api/clothes/{id}")
    suspend fun getClothingById(@Path("id") id: Int): Response<Clothing>

    @POST("api/clothes")
    suspend fun addClothing(@Body clothing: Clothing): Response<Clothing>

    // Lấy danh sách đồ yêu thích của User (Có phân trang)
    @GET("api/clothes/user/{userId}/favorites")
    suspend fun getFavoriteClothesByUser(
        @Path("userId") userId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<Clothing>>

    @PUT("api/clothes/{id}")
    suspend fun updateClothing(
        @Path("id") id: Int,
        @Body clothing: Clothing
    ): Response<Clothing>

    // Xóa 1 món đồ
    @DELETE("api/clothes/{id}")
    suspend fun deleteClothing(@Path("id") id: Int): Response<Any>

    // Gọi API lấy chi tiết 1 danh mục theo ID
    @GET("api/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): Response<Category>

    @GET("api/tags")
    suspend fun getTags(): Response<List<Tag>>

    // Gọi API lấy kho mẫu CÓ PHÂN TRANG VÀ LỌC TAGS
    @GET("api/system-clothes")
    suspend fun getSystemClothesPaginated(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("tags") tags: List<String>?,
        @Query("categoryId") categoryId: List<Int>?
    ): Response<List<SystemClothing>>

    // Gọi API lấy Wishlist (Kho mẫu yêu thích)
    @GET("api/system-clothes/favorites/list")
    suspend fun getFavoriteSystemClothes(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<List<SystemClothing>>

    // Gọi API lấy đồ dọn tủ (> 30 ngày)
    @GET("api/clothes/user/{userId}/declutter")
    suspend fun getDeclutterClothesByUser(@Path("userId") userId: Int): Response<List<Clothing>>

    // Gọi API lấy danh sách outfit của 1 user cụ thể
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

    // Gọi API lấy danh sách danh mục gốc
    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    // Gọi API lấy kho mẫu
    @GET("api/system-clothes")
    suspend fun getSystemClothes(): Response<List<SystemClothing>>

    // Gọi API cập nhật thông tin kho mẫu (dùng để lưu trạng thái thả tim)
    @PUT("api/system-clothes/{id}")
    suspend fun updateSystemClothing(
        @Path("id") id: Int,
        @Body systemClothing: SystemClothing
    ): Response<SystemClothing>

    // Gọi API lấy chi tiết 1 món đồ trong kho mẫu
    @GET("api/system-clothes/{id}")
    suspend fun getSystemClothingById(@Path("id") id: Int): Response<SystemClothing>

    // LOGIN
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // FORGOT PASSWORD
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<MessageResponse>

    // RESET PASSWORD
    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<MessageResponse>
}

