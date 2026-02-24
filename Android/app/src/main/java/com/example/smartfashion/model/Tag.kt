package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Tag(
    @SerializedName("tag_id")
    val tagId: Int? = null,

    @SerializedName("tag_name")
    val tagName: String,

    @SerializedName("tag_group")
    val tagGroup: String,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)