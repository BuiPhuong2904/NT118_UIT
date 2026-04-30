package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val api: ApiService
) {

    // LẤY DANH SÁCH TRIP CỦA USER (API /me)
    suspend fun getMyTrips() =
        api.getMyTrips()

    // LẤY CHI TIẾT 1 TRIP
    suspend fun getTripDetail(tripId: Int) =
        api.getTripDetail(tripId)
}
