package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun getTripDetail(tripId: Int) =
        api.getTripDetail(tripId)
}
