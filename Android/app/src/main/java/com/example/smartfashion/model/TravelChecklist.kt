package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class TravelChecklist(
    @SerializedName("travel_id")
    val travelId: Int? = null,

    @SerializedName("schedule_id")
    val scheduleId: Int,

    @SerializedName("clothing_id")
    val clothingId: Int,

    @SerializedName("is_packed")
    val isPacked: Boolean = false,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)