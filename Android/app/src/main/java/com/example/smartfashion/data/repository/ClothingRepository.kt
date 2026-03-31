package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.AiAnalyzeRequest
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.model.Clothing
import retrofit2.Response
import javax.inject.Inject

class ClothingRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchClothesByUserId(
        userId: Int,
        categoryId: Int,
        page: Int,
        limit: Int,
        search: String? = null
    ): Response<List<Clothing>> {
        return apiService.getClothesByUserId(userId, categoryId, page, limit, search)
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

    suspend fun uploadImage(image: okhttp3.MultipartBody.Part, userId: okhttp3.RequestBody) =
        apiService.uploadImage(image, userId)

    // GỌI AI PHÂN TÍCH ẢNH
    suspend fun analyzeClothingWithAi(imageUrl: String) =
        apiService.analyzeClothing(AiAnalyzeRequest(imageUrl))
}