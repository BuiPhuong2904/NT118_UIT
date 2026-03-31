package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Gọi sang ApiService để lấy danh sách thẻ (Tags)
    suspend fun getTags() = apiService.getTags()
}