package com.example.smartfashion.models

data class TripDetail(
    val id: Int,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val tripType: String,
    val transport: String,
    val coverImageUrl: String?,
    val dayPlans: List<DayPlan> = emptyList()
)

data class DayPlan(
    val id: String,
    val dayNumber: Int,
    val dateStr: String,
    val location: String,
    val weatherTemp: String,
    val isSunny: Boolean,
    val outfitImageUrl: String? = null,
    val outfitName: String? = null,
    val itemCount: Int = 0
)