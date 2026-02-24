package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Schedule(
    @SerializedName("schedule_id")
    val scheduleId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("outfit_id")
    val outfitId: Int,

    @SerializedName("date")
    val date: Date,

    @SerializedName("event_name")
    val eventName: String? = null,

    @SerializedName("event_type")
    val eventType: String = "Daily",

    @SerializedName("location")
    val location: String? = null,

    @SerializedName("weather_note")
    val weatherNote: String? = null,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)