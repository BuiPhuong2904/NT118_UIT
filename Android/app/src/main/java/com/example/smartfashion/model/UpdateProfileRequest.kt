package com.example.smartfashion.model

data class UpdateProfileRequest(
    val username: String? = null,
    val phone_number: String? = null,
    val gender: String? = null,
    val height: Double? = null,
    val weight: Double? = null,
    val body_shape: String? = null,
    val skin_tone: String? = null,
    val style_favourite: String? = null,
    val colors_favourite: String? = null
)
