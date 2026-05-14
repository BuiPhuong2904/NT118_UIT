package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.AssignOutfitRequest
import javax.inject.Inject

class TripRepository @Inject constructor(
    private val api: ApiService
) {

    // LẤY DANH SÁCH TRIP CỦA USER
    suspend fun getMyTrips() =
        api.getMyTrips()

    // LẤY CHI TIẾT 1 TRIP
    suspend fun getTripDetail(tripId: Int) =
        api.getTripDetail(tripId)

    // FIX LỖI Ở ĐÂY
    suspend fun assignOutfitToDay(
        tripId: Int,
        dayNumber: Int,
        outfitId: Int,
        imageUrl: String?
    ) = api.assignOutfitToDay(
        tripId,
        AssignOutfitRequest(
            day = dayNumber,
            outfit_id = outfitId,
            outfit_image = imageUrl
        )
    )
}