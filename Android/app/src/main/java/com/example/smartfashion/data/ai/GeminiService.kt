package com.example.smartfashion.data.ai

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.QuotaExceededException
import com.example.smartfashion.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiService @Inject constructor() {

    private val TAG = "GeminiService"

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY25
    )

    suspend fun generateChecklist(
        destination: String,
        tripType: String,
        transport: String
    ): String {

        val prompt = """
            Bạn là AI Stylist du lịch.

            Hãy tạo checklist hành lý phù hợp cho:

            Điểm đến: $destination
            Loại chuyến đi: $tripType
            Phương tiện: $transport

            Trả về danh sách ngắn.
            Mỗi dòng là 1 món đồ.
        """.trimIndent()

        return try {

            Log.d(TAG, "Calling Gemini API...")

            val response = model.generateContent(prompt)

            val result = response.text ?: ""

            Log.d(TAG, "Gemini success: $result")

            result

        } catch (e: QuotaExceededException) {

            Log.e(TAG, "Quota exceeded: ${e.message}")
            "⚠️ Hết giới hạn API, vui lòng thử lại sau"

        } catch (e: Exception) {
            Log.e(TAG, "Gemini FULL ERROR", e)
            Log.e(TAG, "Message: ${e.message}")

            "❌ Có lỗi khi gọi AI: ${e.message}"
        }
    }
}