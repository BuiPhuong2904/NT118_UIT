package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: ProfileData? = null
)
