package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.WeatherResponse
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Gọi API lấy thời tiết hiện tại
    suspend fun getCurrentWeather(lat: Double = 10.8231, lon: Double = 106.6297): Response<WeatherResponse> {
        return apiService.getCurrentWeather(lat, lon)
    }
}