package com.example.smartfashion.ui.screens.planner

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.UpdateScheduleRequest
import com.example.smartfashion.data.repository.ScheduleRepository
import com.example.smartfashion.model.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    // Ngày đang được chọn
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Tháng đang hiển thị trên lịch
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    // Danh sách các ngày (số) có lịch trình trong tháng
    private val _plannedDays = MutableStateFlow<List<Int>>(emptyList())
    val plannedDays: StateFlow<List<Int>> = _plannedDays.asStateFlow()

    // Danh sách các lịch trình chi tiết của 1 ngày (để in ra OutfitCard)
    private val _dailySchedules = MutableStateFlow<List<Schedule>>(emptyList())
    val dailySchedules: StateFlow<List<Schedule>> = _dailySchedules.asStateFlow()

    private var currentUserId: Int = -1

    fun initData(userId: Int) {
        currentUserId = userId
        fetchPlannedDays()
        fetchDailySchedules()
    }

    // Khi bấm nút qua/lại tháng
    fun changeMonth(offset: Long) {
        _currentMonth.value = _currentMonth.value.plusMonths(offset)
        fetchPlannedDays() // Load lại dấu chấm đỏ cho tháng mới
    }

    // Khi click vào 1 ngày trên lịch
    fun selectDate(day: Int) {
        val newDate = _currentMonth.value.atDay(day)
        _selectedDate.value = newDate
        fetchDailySchedules() // Load chi tiết của ngày đó
    }

    private fun fetchPlannedDays() {
        if (currentUserId == -1) return
        viewModelScope.launch {
            try {
                val year = _currentMonth.value.year
                val month = _currentMonth.value.monthValue
                val response = scheduleRepository.getPlannedDaysInMonth(currentUserId, year, month)
                if (response.isSuccessful && response.body()?.success == true) {
                    _plannedDays.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("CalendarVM", "Lỗi lấy ngày có lịch: ${e.message}")
            }
        }
    }

    private fun fetchDailySchedules() {
        if (currentUserId == -1) return
        viewModelScope.launch {
            try {
                val dateString = _selectedDate.value.toString() // Trả ra chuỗi "YYYY-MM-DD" chuẩn
                val response = scheduleRepository.getSchedulesByDate(currentUserId, dateString)
                if (response.isSuccessful && response.body()?.success == true) {
                    _dailySchedules.value = response.body()?.data ?: emptyList()
                } else {
                    _dailySchedules.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("CalendarVM", "Lỗi lấy chi tiết lịch: ${e.message}")
                _dailySchedules.value = emptyList()
            }
        }
    }

    // Gọi hàm này từ BottomSheet để lưu lịch lên Server
    suspend fun createNewSchedule(schedule: com.example.smartfashion.data.api.ScheduleRequest): String {
        return try {
            val response = scheduleRepository.createSchedule(schedule)
            if (response.isSuccessful && response.body()?.success == true) {
                "SUCCESS"
            } else {
                // Ép Android đọc chính xác lỗi mà Server Node.js gửi về
                val errBody = response.errorBody()?.string() ?: "Lỗi không xác định từ Server"
                "Lỗi Server: $errBody"
            }
        } catch (e: Exception) {
            // Lỗi do sập mạng hoặc code Android dịch sai dữ liệu
            "Lỗi App: ${e.message}"
        }
    }

    // Hàm gọi xóa lịch
    suspend fun deleteSchedule(scheduleId: Int): Boolean {
        return try {
            val response = scheduleRepository.deleteSchedule(scheduleId)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // Hàm gọi cập nhật lịch trình
    suspend fun updateSchedule(scheduleId: Int, eventName: String, location: String): Boolean {
        return try {
            val request = UpdateScheduleRequest(event_name = eventName, location = location)
            val response = scheduleRepository.updateSchedule(scheduleId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                fetchDailySchedules()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}