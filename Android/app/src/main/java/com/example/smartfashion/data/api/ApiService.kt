package com.example.smartfashion.data.api

import com.example.smartfashion.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH
import retrofit2.http.Query
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part

// --- CÁC DATA CLASS  ---
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
data class CreateOutfitRequest(
    val user_id: Int,
    val name: String,
    val description: String? = null,
    val image_preview_url: String? = null,
    val items: List<OutfitItemRequest>
)
data class OutfitItemRequest(
    val clothing_id: Int,
    val position_x: Float,
    val position_y: Float,
    val scale: Float,
    val rotation: Float,
    val z_index: Int
)
data class UploadedImageData(
    val image_id: Int,
    val url_original: String,
    val url_no_bg: String?
)
data class ImageUploadResponse(
    val success: Boolean,
    val message: String,
    val data: UploadedImageData
)
data class AiAnalyzeRequest(
    val imageUrl: String
)
data class AiClothingData(
    val name: String,
    val category_name: String,
    val color_hex: String,
    val color_family: String,
    val material: String,
    val seasons: List<String>,
    val weathers: List<String>,
    val occasions: List<String>,
    val styles: List<String>
)
data class AiAnalyzeResponse(
    val success: Boolean,
    val data: AiClothingData?
)

data class AddToWishlistRequest(
    val user_id: Int,
    val template_id: Int? = null,
    val item_name: String,
    val image_url: String? = null,
    val price_estimate: Double? = null,
    val link_store: String? = null
)

data class UpdateWishlistStatusRequest(
    val status: String
)

data class WishlistPaginatedResponse(
    val totalCount: Int,
    val totalPages: Int,
    val currentPage: Int,
    val data: List<Wishlist>
)

interface ApiService {
    // --- CLOTHES ---
    @GET("api/clothes/user/{userId}")
    suspend fun getClothesByUserId(
        @Path("userId") userId: Int,
        @Query("categoryId") categoryId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("search") search: String? = null
    ): Response<List<Clothing>>

    @GET("api/clothes")
    suspend fun getClothes(): Response<List<Clothing>>

    @GET("api/clothes/{id}")
    suspend fun getClothingById(
        @Path("id") id: Int
    ): Response<Clothing>

    @POST("api/clothes")
    suspend fun addClothing(
        @Body clothing: Clothing
    ): Response<Clothing>

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

    @DELETE("api/clothes/{id}")
    suspend fun deleteClothing(
        @Path("id") id: Int
    ): Response<Any>

    @GET("api/clothes/user/{userId}/declutter")
    suspend fun getDeclutterClothesByUser(
        @Path("userId") userId: Int
    ): Response<List<Clothing>>

    // --- IMAGES ---
    @Multipart
    @POST("api/images/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("user_id") userId: RequestBody
    ): Response<ImageUploadResponse>

    // --- CATEGORIES & TAGS ---
    @GET("api/categories/{id}")
    suspend fun getCategoryById(
        @Path("id") id: Int
    ): Response<Category>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("api/tags")
    suspend fun getTags(): Response<List<Tag>>

    // --- SYSTEM CLOTHES (KHO MẪU) ---
    @GET("api/system-clothes")
    suspend fun getSystemClothesPaginated(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("tags") tags: List<String>?,
        @Query("categoryId") categoryId: List<Int>?,
        @Query("search") search: String? = null
    ): Response<List<SystemClothing>>

    @GET("api/system-clothes")
    suspend fun getSystemClothes(): Response<List<SystemClothing>>

    @GET("api/system-clothes/{id}")
    suspend fun getSystemClothingById(
        @Path("id") id: Int
    ): Response<SystemClothing>

    // --- WISHLIST ---
    @POST("api/wishlists")
    suspend fun addToWishlist(
        @Body request: AddToWishlistRequest
    ): Response<Wishlist>

    @GET("api/wishlists/user/{userId}")
    suspend fun getUserWishlist(
        @Path("userId") userId: Int,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: String? = null
    ): Response<WishlistPaginatedResponse>

    @DELETE("api/wishlists/{id}")
    suspend fun removeFromWishlist(
        @Path("id") wishlistId: Int,
        @Query("user_id") userId: Int
    ): Response<Any>

    @PATCH("api/wishlists/{id}/status")
    suspend fun updateWishlistStatus(
        @Path("id") wishlistId: Int,
        @Body request: UpdateWishlistStatusRequest
    ): Response<Wishlist>

    // --- AI ---
    @POST("api/ai/analyze-clothing")
    suspend fun analyzeClothing(
        @Body request: AiAnalyzeRequest
    ): Response<AiAnalyzeResponse>

    // --- OUTFITS ---
    @GET("api/outfits/user/{userId}")
    suspend fun getOutfitsByUser(
        @Path("userId") userId: Int,
        @Query("is_favorite") isFavorite: Boolean? = null,
        @Query("tags") tags: List<String>? = null
    ): Response<OutfitResponse>

    @GET("api/outfits/{id}")
    suspend fun getOutfitById(
        @Path("id") id: Int
    ): Response<SingleOutfitResponse>

    @POST("api/outfits")
    suspend fun createOutfit(
        @Body request: CreateOutfitRequest
    ): Response<SingleOutfitResponse>

    @PUT("api/outfits/{id}/favorite")
    suspend fun updateFavoriteStatus(
        @Path("id") id: Int,
        @Body request: FavoriteRequest
    ): Response<SingleOutfitResponse>

    // --- AUTH ---
    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<MessageResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<MessageResponse>
}