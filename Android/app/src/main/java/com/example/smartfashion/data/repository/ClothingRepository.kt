package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import javax.inject.Inject

class ClothingRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchAllClothes() = apiService.getClothes()
}