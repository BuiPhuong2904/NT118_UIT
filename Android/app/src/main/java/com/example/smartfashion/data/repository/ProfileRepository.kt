package com.example.smartfashion.repository

import com.example.smartfashion.model.ProfileResponse
import com.example.smartfashion.data.api.ApiService
import retrofit2.Response
import javax.inject.Inject
import android.util.Log
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getMyProfile(): Response<ProfileResponse> {
        return apiService.getMyProfile()
    }

    // SỬA LẠI HÀM NÀY: Nhận nhiều Part thay vì 1 Request object đơn thuần
    suspend fun updateMyProfile(
        username: RequestBody?,
        phone_number: RequestBody?,
        gender: RequestBody?,
        height: RequestBody?,
        weight: RequestBody?,
        body_shape: RequestBody?,
        skin_tone: RequestBody?,
        style_favourite: RequestBody?,
        colors_favourite: RequestBody?,
        avatar: MultipartBody.Part?
    ): Response<ProfileResponse> {
        return apiService.updateMyProfile(
            username, 
            phone_number, 
            gender, 
            height, 
            weight, 
            body_shape, 
            skin_tone, 
            style_favourite, 
            colors_favourite, 
            avatar
        )
    }

    suspend fun getStats(userId: Int, year: Int, month: Int): Triple<Int, Int, Int> {
        return try {
            val clothesResponse = apiService.getClothesByUserId(userId, 0, 1, 1000)
            val outfitsResponse = apiService.getOutfitsByUser(userId)
            val schedulesResponse = apiService.getPlannedDaysInMonth(userId, year, month)

            Log.d("DEBUG", "clothes code = ${clothesResponse.code()}")
            Log.d("DEBUG", "outfits code = ${outfitsResponse.code()}")
            Log.d("DEBUG", "schedules code = ${schedulesResponse.code()}")

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