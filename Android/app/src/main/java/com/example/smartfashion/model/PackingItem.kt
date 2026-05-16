package com.example.smartfashion.model

data class PackingItem(
    val id: Int,
    val name: String,
    val category: String,
    val isPacked: Boolean = false
)