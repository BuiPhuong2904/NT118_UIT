package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.DailySchedulesResponse
import com.example.smartfashion.data.api.PlannedDaysResponse
import com.example.smartfashion.data.api.ScheduleRequest
import com.example.smartfashion.data.api.SingleScheduleResponse
import com.example.smartfashion.model.Schedule
import retrofit2.Response
import javax.inject.Inject

class ScheduleRepository @Inject constructor(
    private val apiService: ApiService
) {
    // 1. Gọi API lấy danh sách các ngày có lịch trong tháng
    suspend fun getPlannedDaysInMonth(userId: Int, year: Int, month: Int): Response<PlannedDaysResponse> {
        return apiService.getPlannedDaysInMonth(userId, year, month)
    }

    // 2. Gọi API lấy danh sách lịch trình chi tiết của 1 ngày
    suspend fun getSchedulesByDate(userId: Int, date: String): Response<DailySchedulesResponse> {
        return apiService.getSchedulesByDate(userId, date)
    }

    // 3. Gọi API thêm lịch trình mới
    suspend fun createSchedule(schedule: ScheduleRequest): Response<SingleScheduleResponse> {
        return apiService.createSchedule(schedule)
    }

    // 4. Gọi API xóa lịch
    suspend fun deleteSchedule(scheduleId: Int): Response<Any> {
        return apiService.deleteSchedule(scheduleId)
    }
}