package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class User(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password_hash")
    val passwordHash: String,

    @SerializedName("gender")
    val gender: String = "Khác",

    @SerializedName("created_at")
    val createdAt: Date? = null
)