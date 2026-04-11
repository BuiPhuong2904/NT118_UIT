package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.AiLogSaveRequest
import com.example.smartfashion.data.api.AiSessionResponse
import com.example.smartfashion.data.api.AiLogListResponse
import com.example.smartfashion.data.api.AiLogResponse
import com.example.smartfashion.data.api.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Lưu tin nhắn vào Database
    suspend fun saveAiLog(request: AiLogSaveRequest): Response<AiLogResponse> {
        return apiService.saveAiLog(request)
    }

    // Lấy danh sách các cuộc trò chuyện
    suspend fun getAiSessions(userId: Int): Response<AiSessionResponse> {
        return apiService.getAiSessions(userId)
    }

    // Lấy chi tiết toàn bộ tin nhắn của một cuộc trò chuyện
    suspend fun getSessionMessages(userId: Int, sessionId: String): Response<AiLogListResponse> {
        return apiService.getSessionMessages(userId, sessionId)
    }
}