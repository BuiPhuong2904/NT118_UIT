package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.FavoriteRequest
import javax.inject.Inject

class OutfitRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getOutfitsByUser(
        userId: Int,
        isFavorite: Boolean? = null,
        tags: List<String>? = null
    ) = apiService.getOutfitsByUser(userId, isFavorite, tags)

    suspend fun getOutfitById(id: Int) = apiService.getOutfitById(id)

    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean) =
        apiService.updateFavoriteStatus(id, FavoriteRequest(is_favorite = isFavorite))
}