package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: ProfileData
)

data class ProfileData(
    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("profiles_id")
    val profilesId: Int? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("avatar_url")
    val avatarUrl: String? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("height")
    val height: Double? = null,

    @SerializedName("weight")
    val weight: Double? = null,

    @SerializedName("body_shape")
    val bodyShape: String? = null,

    @SerializedName("style_favourite")
    val styleFavourite: String? = null,

    @SerializedName("colors_favourite")
    val colorsFavourite: String? = null,

    @SerializedName("skin_tone")
    val skinTone: String? = null
)
