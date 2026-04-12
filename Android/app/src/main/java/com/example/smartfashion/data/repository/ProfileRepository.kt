package com.example.smartfashion.repository

import com.example.smartfashion.model.ProfileResponse
import com.example.smartfashion.model.UpdateProfileRequest
import com.example.smartfashion.data.api.ApiService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getMyProfile(): Response<ProfileResponse> {
        return apiService.getMyProfile()
    }

    suspend fun updateMyProfile(request: UpdateProfileRequest): Response<ProfileResponse> {
        return apiService.updateMyProfile(request)
    }

    suspend fun uploadAvatar(file: MultipartBody.Part): Response<ProfileResponse> {
        return apiService.uploadAvatar(file)
    }
}
