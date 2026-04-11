package com.example.smartfashion.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class AIPromptLog(
    @SerializedName("ailog_id")
    val aiLogId: Int? = null,

    @SerializedName("session_id")
    val sessionId: String,

    @SerializedName("input_prompt")
    val inputPrompt: String,

    @SerializedName("input_image_url")
    val inputImageUrl: String? = null,

    @SerializedName("gemini_raw_response")
    val geminiRawResponse: String? = null,

    @SerializedName("weather_context")
    val weatherContext: String? = null,

    @SerializedName("created_at")
    val createdAt: Date? = null
)