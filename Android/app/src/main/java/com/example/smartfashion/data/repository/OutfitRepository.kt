package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import javax.inject.Inject

class OutfitRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getOutfitsByUser(userId: Int) = apiService.getOutfitsByUser(userId)

    suspend fun getOutfitById(id: Int) = apiService.getOutfitById(id)
}