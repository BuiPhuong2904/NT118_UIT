package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchCategories() = apiService.getCategories()
    // Gọi sang ApiService để lấy 1 danh mục
    suspend fun getCategoryById(id: Int) = apiService.getCategoryById(id)
}