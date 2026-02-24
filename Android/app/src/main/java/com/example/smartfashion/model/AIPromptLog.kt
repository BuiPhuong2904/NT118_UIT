package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AIPromptLog(
    @SerializedName("ailog_id")
    val aiLogId: Int? = null,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("input_prompt")
    val inputPrompt: String,

    @SerializedName("gemini_raw_response")
    val geminiRawResponse: String? = null,

    @SerializedName("weather_context")
    val weatherContext: String? = null,

    @SerializedName("created_at")
    val createdAt: Date? = null,

    @SerializedName("updatedAt")
    val updatedAt: Date? = null
)