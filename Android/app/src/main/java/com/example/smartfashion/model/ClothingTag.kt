package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ClothingTag(
    @SerializedName("clothing_tag_id")
    val clothingTagId: Int? = null,

    @SerializedName("clothing_id")
    val clothingId: Int,

    @SerializedName("tag_id")
    val tagId: Int,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)