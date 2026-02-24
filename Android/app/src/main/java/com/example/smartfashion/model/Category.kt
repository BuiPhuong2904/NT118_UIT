package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("category_id")
    val categoryId: Int? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("parent_id")
    val parentId: Int? = null
)