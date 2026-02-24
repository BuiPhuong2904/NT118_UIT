package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Wishlist(
    @SerializedName("wishlist_id")
    val wishlistId: Int? = null, // ID tự tăng từ Counter

    @SerializedName("user_id")
    val userId: Int, // ID của chủ nhân wishlist

    @SerializedName("item_name")
    val itemName: String, // Tên món đồ muốn mua

    @SerializedName("image_url")
    val imageUrl: String? = null, // Ảnh món đồ (có thể lấy từ web)

    @SerializedName("price_estimate")
    val priceEstimate: Double? = null, // Giá dự kiến (dùng Double cho tiền tệ)

    @SerializedName("link_store")
    val linkStore: String? = null, // Link cửa hàng online

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)