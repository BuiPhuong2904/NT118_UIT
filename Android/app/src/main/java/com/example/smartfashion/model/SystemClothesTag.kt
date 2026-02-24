package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SystemClothesTag(
    @SerializedName("system_tag_id")
    val systemTagId: Int? = null,

    @SerializedName("template_id")
    val templateId: Int,

    @SerializedName("tag_id")
    val tagId: Int,

    @SerializedName("createdAt")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)