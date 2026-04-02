package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.AddToWishlistRequest
import com.example.smartfashion.data.api.UpdateWishlistStatusRequest
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val apiService: ApiService
) {
    // --- SYSTEM CLOTHES ---
    // ViewModel gọi (Phân trang và Lọc)
    suspend fun fetchSystemClothesPaginated(
        page: Int,
        limit: Int,
        tags: List<String>?,
        categoryId: List<Int>?,
        search: String? = null
    ) = apiService.getSystemClothesPaginated(page, limit, tags, categoryId, search)

    // Lấy chi tiết 1 System Clothing
    suspend fun getSystemClothingById(id: Int) = apiService.getSystemClothingById(id)

    // --- TAGS ---
    suspend fun fetchTags() = apiService.getTags()

    // --- WISHLIST ---
    // Thêm vào Wishlist (Thả tim)
    suspend fun addToWishlist(request: AddToWishlistRequest) =
        apiService.addToWishlist(request)

    // Lấy danh sách Wishlist theo ID User
    suspend fun getUserWishlist(userId: Int, page: Int, limit: Int, status: String? = null) =
        apiService.getUserWishlist(userId, page, limit, status)

    // Bỏ thả tim (Xóa khỏi wishlist)
    suspend fun removeFromWishlist(wishlistId: Int, userId: Int) =
        apiService.removeFromWishlist(wishlistId, userId)

    // Cập nhật trạng thái đồ trong Wishlist (Đã mua / Đang chờ)
    suspend fun updateWishlistStatus(wishlistId: Int, status: String) =
        apiService.updateWishlistStatus(wishlistId, UpdateWishlistStatusRequest(status))
}