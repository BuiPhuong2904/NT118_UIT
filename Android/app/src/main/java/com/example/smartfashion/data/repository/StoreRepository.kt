package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.model.SystemClothing
import javax.inject.Inject

class StoreRepository @Inject constructor(
    private val apiService: ApiService
) {
    // ViewModel gọi (Phân trang và Lọc)
    suspend fun fetchSystemClothesPaginated(
        page: Int,
        limit: Int,
        tags: List<String>?,
        categoryId: List<Int>?,
        search: String? = null
    ) = apiService.getSystemClothesPaginated(page, limit, tags, categoryId, search)

    // hàm lấy Tags
    suspend fun fetchTags() = apiService.getTags()

    // cập nhật SystemClothing
    suspend fun updateSystemClothing(id: Int, systemClothing: SystemClothing) =
        apiService.updateSystemClothing(id, systemClothing)

    // Lấy chi tiết 1 System Clothing
    suspend fun getSystemClothingById(id: Int) = apiService.getSystemClothingById(id)

    suspend fun getFavoriteSystemClothes(page: Int, limit: Int) =
        apiService.getFavoriteSystemClothes(page, limit)
}