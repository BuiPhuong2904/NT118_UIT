package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Image(
    @SerializedName("image_id")
    val imageId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("url_original")
    val urlOriginal: String,

    @SerializedName("url_no_bg")
    val urlNoBg: String? = null,

    @SerializedName("storage_type")
    val storageType: String? = null,

    @SerializedName("created_at")
    val createdAt: Date? = null
)