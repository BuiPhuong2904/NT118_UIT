package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AiSession(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("session_id")
    val sessionId: String,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("created_at")
    val createdAt: Date? = null,

    @SerializedName("updated_at")
    val updatedAt: Date? = null
)