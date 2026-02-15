package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Clothing(
    @SerializedName("clothing_id")
    val clothingId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("image_id")
    val imageId: Int,

    @SerializedName("category_id")
    val categoryId: Int,

    @SerializedName("color_hex")
    val colorHex: String? = null,

    @SerializedName("color_family")
    val colorFamily: String? = null,

    @SerializedName("material")
    val material: String? = null,

    @SerializedName("size")
    val size: String? = null,

    @SerializedName("brand_name")
    val brandName: String? = null,

    @SerializedName("is_favorite")
    val isFavorite: Boolean = false,

    @SerializedName("status")
    val status: String = "active",

    @SerializedName("last_worn")
    val lastWorn: Date? = null,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)