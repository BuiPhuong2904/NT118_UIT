package com.example.smartfashion.data.api

import com.example.smartfashion.model.*
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

// --- CÁC DATA CLASS PHỤ TRỢ ---
data class OutfitResponse(val success: Boolean, val data: List<Outfit>)
data class FavoriteRequest(val is_favorite: Boolean)
data class SingleOutfitResponse(val success: Boolean, val data: Outfit)
data class CreateOutfitRequest(
    val user_id: Int,
    val name: String,
    val description: String? = null,
    val image_preview_url: String? = null,
    val items: List<OutfitItemRequest>,
    val tags: List<String>? = null
)
data class OutfitItemRequest(
    val clothing_id: Int,
    val position_x: Float,
    val position_y: Float,
    val scale: Float,
    val rotation: Float,
    val z_index: Int
)
data class UpdateOutfitRequest(val name: String, val description: String, val tags: List<String>)
data class UploadedImageData(val image_id: Int, val url_original: String, val url_no_bg: String?)
data class ImageUploadResponse(val success: Boolean, val message: String, val data: UploadedImageData)
data class AiAnalyzeRequest(val imageUrl: String)
data class AiClothingData(
    val name: String, val category_name: String, val color_hex: String,
    val color_family: String, val material: String, val seasons: List<String>,
    val weathers: List<String>, val occasions: List<String>, val styles: List<String>
)
data class AiAnalyzeResponse(val success: Boolean, val data: AiClothingData?)
data class AddToWishlistRequest(
    val user_id: Int, val template_id: Int? = null, val item_name: String,
    val image_url: String? = null, val price_estimate: Double? = null, val link_store: String? = null
)
data class UpdateWishlistStatusRequest(val status: String)
data class WishlistPaginatedResponse(val totalCount: Int, val totalPages: Int, val currentPage: Int, val data: List<Wishlist>)
data class PlannedDaysResponse(val success: Boolean, val data: List<Int>)
data class DailySchedulesResponse(val success: Boolean, val data: List<Schedule>)
data class SingleScheduleResponse(val success: Boolean, val data: Any? = null)
data class OutfitSummary(
    val _id: String? = null, val name: String? = null,
    val image_preview_url: String? = null, val tagNames: List<String>? = null
)
data class ScheduleRequest(
    val user_id: Int, val outfit_id: Int, val date: String,
    val event_name: String, val event_type: String, val location: String
)
data class UpdateScheduleRequest(val event_name: String, val location: String)
data class AiLogSaveRequest(
    val user_id: Int, val session_id: String, val title: String? = null,
    val input_prompt: String, val input_image_url: String? = null,
    val gemini_raw_response: String? = null, val weather_context: String? = null
)
data class AiSessionResponse(val success: Boolean, val data: List<AiSession>)
data class AiLogResponse(val success: Boolean, val data: AIPromptLog)
data class AiLogListResponse(val success: Boolean, val data: List<AIPromptLog>)
data class WeatherResponse(val success: Boolean, val data: WeatherCache)

// --- TRIPS (PLANNER) MODELS ---
// Định nghĩa model Trip khớp với Backend Node.js
data class Trip(
    val trip_id: Int,
    val user_id: Int,
    val destination: String,
    val start_date: String,
    val end_date: String,
    val trip_type: String,
    val transport: String,
    val image_url: String?,
    val total_items: Int = 0,
    val packed_items: Int = 0
)

data class TripResponse(val success: Boolean, val data: List<Trip>)
data class SingleTripResponse(val success: Boolean, val data: Trip)

data class CreateTripRequest(
    val user_id: Int,
    val destination: String,
    val start_date: String,
    val end_date: String,
    val trip_type: String,
    val transport: String,
    val image_url: String? = null
)

interface ApiService {
    // --- CLOTHES ---
    @GET("api/clothes/user/{userId}")
    suspend fun getClothesByUserId(
        @Path("userId") userId: Int, @Query("categoryId") categoryId: Int,
        @Query("page") page: Int, @Query("limit") limit: Int, @Query("search") search: String? = null
    ): Response<List<Clothing>>

    @GET("api/clothes")
    suspend fun getClothes(): Response<List<Clothing>>

    @GET("api/clothes/{id}")
    suspend fun getClothingById(@Path("id") id: Int): Response<Clothing>

    @POST("api/clothes")
    suspend fun addClothing(@Body clothing: Clothing): Response<Clothing>

    @GET("api/clothes/user/{userId}/favorites")
    suspend fun getFavoriteClothesByUser(
        @Path("userId") userId: Int, @Query("page") page: Int, @Query("limit") limit: Int
    ): Response<List<Clothing>>

    @PUT("api/clothes/{id}")
    suspend fun updateClothing(@Path("id") id: Int, @Body clothing: Clothing): Response<Clothing>

    @DELETE("api/clothes/{id}")
    suspend fun deleteClothing(@Path("id") id: Int): Response<Any>

    @GET("api/clothes/user/{userId}/declutter")
    suspend fun getDeclutterClothesByUser(@Path("userId") userId: Int): Response<List<Clothing>>

    // --- IMAGES ---
    @Multipart
    @POST("api/images/upload")
    suspend fun uploadImage(@Part image: MultipartBody.Part, @Part("user_id") userId: RequestBody): Response<ImageUploadResponse>

    // --- CATEGORIES & TAGS ---
    @GET("api/categories/{id}")
    suspend fun getCategoryById(@Path("id") id: Int): Response<Category>

    @GET("api/categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("api/tags")
    suspend fun getTags(): Response<List<com.example.smartfashion.model.Tag>>

    // --- SYSTEM CLOTHES (KHO MẪU) ---
    @GET("api/system-clothes")
    suspend fun getSystemClothesPaginated(
        @Query("page") page: Int, @Query("limit") limit: Int,
        @Query("tags") tags: List<String>?, @Query("categoryId") categoryId: List<Int>?,
        @Query("search") search: String? = null
    ): Response<List<SystemClothing>>

    @GET("api/system-clothes/{id}")
    suspend fun getSystemClothingById(@Path("id") id: Int): Response<SystemClothing>

    // --- WISHLIST ---
    @POST("api/wishlists")
    suspend fun addToWishlist(@Body request: AddToWishlistRequest): Response<Wishlist>

    @GET("api/wishlists/user/{userId}")
    suspend fun getUserWishlist(
        @Path("userId") userId: Int, @Query("page") page: Int,
        @Query("limit") limit: Int, @Query("status") status: String? = null
    ): Response<WishlistPaginatedResponse>

    @DELETE("api/wishlists/{id}")
    suspend fun removeFromWishlist(@Path("id") wishlistId: Int, @Query("user_id") userId: Int): Response<Any>

    @PATCH("api/wishlists/{id}/status")
    suspend fun updateWishlistStatus(@Path("id") wishlistId: Int, @Body request: UpdateWishlistStatusRequest): Response<Wishlist>

    // --- AI CHAT LOGS ---
    @POST("api/ai-logs")
    suspend fun saveAiLog(@Body request: AiLogSaveRequest): Response<AiLogResponse>

    @GET("api/ai-logs/user/{userId}/sessions")
    suspend fun getAiSessions(@Path("userId") userId: Int): Response<AiSessionResponse>

    @GET("api/ai-logs/user/{userId}/sessions/{sessionId}")
    suspend fun getSessionMessages(@Path("userId") userId: Int, @Path("sessionId") sessionId: String): Response<AiLogListResponse>

    // --- AI ---
    @POST("api/ai/analyze-clothing")
    suspend fun analyzeClothing(@Body request: AiAnalyzeRequest): Response<AiAnalyzeResponse>

    // --- OUTFITS ---
    @GET("api/outfits/user/{userId}")
    suspend fun getOutfitsByUser(
        @Path("userId") userId: Int, @Query("is_favorite") isFavorite: Boolean? = null,
        @Query("tags") tags: List<String>? = null
    ): Response<OutfitResponse>

    @GET("api/outfits/{id}")
    suspend fun getOutfitById(@Path("id") id: Int): Response<SingleOutfitResponse>

    @POST("api/outfits")
    suspend fun createOutfit(@Body request: CreateOutfitRequest): Response<SingleOutfitResponse>

    @PUT("api/outfits/{id}/favorite")
    suspend fun updateFavoriteStatus(@Path("id") id: Int, @Body request: FavoriteRequest): Response<SingleOutfitResponse>

    @PUT("api/outfits/{id}")
    suspend fun updateOutfit(@Path("id") id: Int, @Body request: UpdateOutfitRequest): Response<Any>

    @DELETE("api/outfits/{id}")
    suspend fun deleteOutfit(@Path("id") id: Int): Response<Any>

    // --- PROFILE ---
    @GET("api/profile/me")
    suspend fun getMyProfile(): Response<ProfileResponse>

    @Multipart
    @PUT("api/profile/me")
    suspend fun updateMyProfile(
        @Part("username") username: RequestBody?, @Part("phone_number") phone: RequestBody?,
        @Part("gender") gender: RequestBody?, @Part("height") height: RequestBody?,
        @Part("weight") weight: RequestBody?, @Part("body_shape") bodyShape: RequestBody?,
        @Part("skin_tone") skinTone: RequestBody?, @Part("style_favourite") style: RequestBody?,
        @Part("colors_favourite") colors: RequestBody?, @Part avatar: MultipartBody.Part?
    ): Response<ProfileResponse>

    // --- AUTH ---
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>

    // --- SCHEDULES ---
    @GET("api/schedules/user/{userId}/month")
    suspend fun getPlannedDaysInMonth(@Path("userId") userId: Int, @Query("year") year: Int, @Query("month") month: Int): Response<PlannedDaysResponse>

    @GET("api/schedules/user/{userId}/date")
    suspend fun getSchedulesByDate(@Path("userId") userId: Int, @Query("date") date: String): Response<DailySchedulesResponse>

    @POST("api/schedules")
    suspend fun createSchedule(@Body schedule: ScheduleRequest): Response<SingleScheduleResponse>

    @DELETE("api/schedules/{id}")
    suspend fun deleteSchedule(@Path("id") id: Int): Response<Any>

    @PUT("api/schedules/{id}")
    suspend fun updateSchedule(@Path("id") id: Int, @Body request: UpdateScheduleRequest): Response<SingleScheduleResponse>

    // --- WEATHER ---
    @GET("api/weather")
    suspend fun getCurrentWeather(@Query("lat") lat: Double = 10.8231, @Query("lon") lon: Double = 106.6297): Response<WeatherResponse>

    // --- TRIPS (PLANNER) ---
    @GET("api/trips/user/{userId}")
    suspend fun getTripsByUser(@Path("userId") userId: Int): Response<TripResponse>

    @POST("api/trips")
    suspend fun createTrip(@Body request: CreateTripRequest): Response<SingleTripResponse>

    @GET("api/trips/{id}")
    suspend fun getTripById(@Path("id") id: Int): Response<SingleTripResponse>
}