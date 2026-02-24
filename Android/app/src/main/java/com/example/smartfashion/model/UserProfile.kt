package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class UserProfile(
    @SerializedName("profiles_id")
    val profilesId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("height")
    val height: Double?,

    @SerializedName("weight")
    val weight: Double?,

    @SerializedName("body_shape")
    val bodyShape: String?,

    @SerializedName("skin_tone")
    val skinTone: String?,

    @SerializedName("style_favourite")
    val styleFavourite: String?,

    @SerializedName("colors_favourite")
    val colorsFavourite: String?,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)