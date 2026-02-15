package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Outfit(
    @SerializedName("outfit_id")
    val outfitId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("rating")
    val rating: Int? = null,

    @SerializedName("is_ai_suggested")
    val isAiSuggested: Boolean = false,

    @SerializedName("image_preview_url")
    val imagePreviewUrl: String? = null,

    @SerializedName("created_at")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)