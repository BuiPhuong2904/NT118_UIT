package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class WeatherCache(
    @SerializedName("weather_id")
    val weatherId: Int? = null,

    @SerializedName("location_name")
    val locationName: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("temp")
    val temp: Double, // Nhiệt độ

    @SerializedName("condition")
    val condition: String,

    @SerializedName("icon_url")
    val iconUrl: String,

    @SerializedName("expired_at")
    val expiredAt: Date,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)