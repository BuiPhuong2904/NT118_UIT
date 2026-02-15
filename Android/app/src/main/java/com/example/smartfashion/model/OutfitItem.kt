package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class OutfitItem(
    @SerializedName("item_id")
    val itemId: Int? = null,

    @SerializedName("outfit_id")
    val outfitId: Int,

    @SerializedName("clothing_id")
    val clothingId: Int,

    @SerializedName("position_x")
    val positionX: Double = 0.0,

    @SerializedName("position_y")
    val positionY: Double = 0.0,

    @SerializedName("scale")
    val scale: Double = 1.0,

    @SerializedName("z_index")
    val zIndex: Int = 0,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)