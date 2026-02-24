package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class UsageHistory(
    @SerializedName("usage_id")
    val usageId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("clothing_id")
    val clothingId: Int,

    @SerializedName("used_at")
    val usedAt: Date = Date(),

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)