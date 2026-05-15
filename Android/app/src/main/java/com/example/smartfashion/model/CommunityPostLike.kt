package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CommunityPostLike(
    @SerializedName("post_like_id")
    val postLikeId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("post_id")
    val postId: Int,

    @SerializedName("created_at")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)