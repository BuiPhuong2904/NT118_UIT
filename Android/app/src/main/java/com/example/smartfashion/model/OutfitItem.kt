package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class OutfitItem(
    @SerializedName("item_id")
    val itemId: Int? = null,

    @SerializedName("outfit_id")
    val outfitId: Int,

    @SerializedName("item_ref_id")
    val itemRefId: Int,

    @SerializedName("item_type")
    val itemType: String = "personal",

    @SerializedName("position_x")
    val positionX: Double = 0.0,

    @SerializedName("position_y")
    val positionY: Double = 0.0,

    @SerializedName("scale")
    val scale: Double = 1.0,

    @SerializedName("rotation")
    val rotation: Double = 0.0,

    @SerializedName("z_index")
    val zIndex: Int = 0,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)