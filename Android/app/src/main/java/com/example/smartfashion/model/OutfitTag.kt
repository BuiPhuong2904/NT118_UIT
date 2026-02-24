package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class OutfitTag(
    @SerializedName("outfit_tag_id")
    val outfitTagId: Int? = null,

    @SerializedName("outfit_id")
    val outfitId: Int,

    @SerializedName("tag_id")
    val tagId: Int,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)