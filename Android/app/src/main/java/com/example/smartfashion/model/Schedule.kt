package com.example.smartfashion.model

import com.example.smartfashion.data.api.OutfitSummary
import com.google.gson.annotations.SerializedName
import java.util.Date

data class Schedule(
    @SerializedName("schedule_id")
    val scheduleId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("outfit_id")
    val outfitInfo: OutfitSummary? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("event_name")
    val eventName: String? = null,

    @SerializedName("event_type")
    val eventType: String = "Daily",

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("weather_note")
    val weatherNote: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)