package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SystemClothing(
    @SerializedName("template_id")
    val templateId: Int? = null,

    @SerializedName("category_id")
    val categoryId: Int,

    @SerializedName("image_url")
    val imageUrl: String,

    @SerializedName("color_hex")
    val colorHex: String? = null,

    @SerializedName("color_family")
    val colorFamily: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)