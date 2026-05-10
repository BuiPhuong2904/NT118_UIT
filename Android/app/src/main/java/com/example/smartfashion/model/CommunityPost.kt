package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CommunityPost(
    @SerializedName("post_id")
    val postId: Int? = null,

    @SerializedName("user_id")
    val userId: Int? = null,

    @SerializedName("outfit_id")
    val outfitId: Int? = null,

    @SerializedName("image_url")
    val imageUrl: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("likes_count")
    val likesCount: Int = 0,

    @SerializedName("height_ratio")
    val heightRatio: Float,

    // ------

    @SerializedName("author_name")
    val authorName: String? = null,

    @SerializedName("author_avatar")
    val authorAvatar: String? = null,

    @SerializedName("tags")
    val tags: List<String>? = null,

    @SerializedName("is_liked")
    val isLiked: Boolean = false,

    @SerializedName("created_at")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)