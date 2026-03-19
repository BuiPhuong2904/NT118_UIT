package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.model.Clothing
import retrofit2.Response
import javax.inject.Inject

class ClothingRepository @Inject constructor(
    private val apiService: ApiService
) {
    // CẬP NHẬT LẠI: Truyền đủ 4 tham số sang ApiService để phân trang & lọc danh mục
    suspend fun fetchClothesByUserId(
        userId: Int,
        categoryId: Int,
        page: Int,
        limit: Int
    ): Response<List<Clothing>> {
        return apiService.getClothesByUserId(userId, categoryId, page, limit)
    }

    // ĐỌC - Lấy danh sách toàn bộ tủ đồ
    suspend fun fetchAllClothes() = apiService.getClothes()

    // Lấy chi tiết 1 món đồ theo ID
    suspend fun fetchClothingById(id: Int) = apiService.getClothingById(id)

    // THÊM - Thêm một món đồ mới vào tủ
    suspend fun addClothing(clothing: Clothing) = apiService.addClothing(clothing)

    // SỬA - Cập nhật thông tin món đồ theo ID
    suspend fun updateClothing(id: Int, clothing: Clothing) = apiService.updateClothing(id, clothing)

    // XÓA - Xóa món đồ theo ID
    suspend fun getDeclutterClothesByUser(userId: Int) = apiService.getDeclutterClothesByUser(userId)
    suspend fun deleteClothing(id: Int) = apiService.deleteClothing(id)

    suspend fun getFavoriteClothesByUser(userId: Int, page: Int, limit: Int) =
        apiService.getFavoriteClothesByUser(userId, page, limit)
}