package com.example.smartfashion.repository

import com.example.smartfashion.model.ProfileResponse
import com.example.smartfashion.model.UpdateProfileRequest
import com.example.smartfashion.data.api.ApiService
import retrofit2.Response
import javax.inject.Inject
import android.util.Log


class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getMyProfile(): Response<ProfileResponse> {
        return apiService.getMyProfile()
    }

    suspend fun updateMyProfile(request: UpdateProfileRequest): Response<ProfileResponse> {
        return apiService.updateMyProfile(request)
    }

    suspend fun getStats(userId: Int, year: Int, month: Int): Triple<Int, Int, Int> {
    return try {
        val clothesResponse = apiService.getClothesByUserId(userId, 0, 1, 1000)
        val outfitsResponse = apiService.getOutfitsByUser(userId)
        val schedulesResponse = apiService.getPlannedDaysInMonth(userId, year, month)

        // 👉 THÊM LOG Ở ĐÂY
        Log.d("DEBUG", "clothes code = ${clothesResponse.code()}")
        Log.d("DEBUG", "outfits code = ${outfitsResponse.code()}")
        Log.d("DEBUG", "schedules code = ${schedulesResponse.code()}")

        Log.d("DEBUG", "clothes body = ${clothesResponse.body()}")
        Log.d("DEBUG", "outfits body = ${outfitsResponse.body()}")
        Log.d("DEBUG", "schedules body = ${schedulesResponse.body()}")

        val itemCount = clothesResponse.body()?.size ?: 0
        val outfitCount = outfitsResponse.body()?.data?.size ?: 0
        val eventCount = schedulesResponse.body()?.data?.size ?: 0

        Triple(itemCount, outfitCount, eventCount)

    } catch (e: Exception) {
        e.printStackTrace()
        Triple(0, 0, 0)
    }
}

}
