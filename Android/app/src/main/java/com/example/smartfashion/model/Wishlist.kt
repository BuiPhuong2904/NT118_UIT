package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Wishlist(
    @SerializedName("wishlist_id")
    val wishlistId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("item_name")
    val itemName: String,

    @SerializedName("image_url")
    val imageUrl: String? = null,

    @SerializedName("price_estimate")
    val priceEstimate: Double? = null,

    @SerializedName("link_store")
    val linkStore: String? = null,

    @SerializedName("is_favorite")
    val isFavorite: Boolean = false,

    @SerializedName("status")
    val status: String = "pending",

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)