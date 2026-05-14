package com.example.smartfashion.models

import com.google.gson.annotations.SerializedName

data class Trip(
    @SerializedName("_id")
    val id: String,

    @SerializedName("trip_id")
    val tripId: Int,

    @SerializedName("user_id")
    val userId: Int,

    val destination: String,

    @SerializedName("start_date")
    val startDate: String,

    @SerializedName("end_date")
    val endDate: String,

    @SerializedName("trip_type")
    val tripType: String,

    val transport: String,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("total_items")
    val totalItems: Int,

    @SerializedName("packed_items")
    val packedItems: Int
)
