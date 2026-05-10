package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CommunityPostTag(
    @SerializedName("post_tag_id")
    val postTagId: Int? = null,

    @SerializedName("post_id")
    val postId: Int,

    @SerializedName("tag_id")
    val tagId: Int,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)