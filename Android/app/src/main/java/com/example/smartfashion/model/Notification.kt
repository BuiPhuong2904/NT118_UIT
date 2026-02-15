package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Notification(
    @SerializedName("noti_id")
    val notiId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("is_read")
    val isRead: Boolean = false,

    @SerializedName("created_at")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)