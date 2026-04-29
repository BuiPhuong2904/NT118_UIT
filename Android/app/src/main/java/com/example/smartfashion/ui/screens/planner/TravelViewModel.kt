package com.example.smartfashion.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.CreateTripRequest
import com.example.smartfashion.data.api.Trip // Đảm bảo import đúng Trip từ ApiService
import com.example.smartfashion.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TravelViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchUserTrips()
    }

    /**
     * Bước 1: Lấy danh sách chuyến đi từ Backend
     */
    fun fetchUserTrips() {
        val userId = tokenManager.getUserId()
        if (userId == -1) {
            _error.value = "Không tìm thấy thông tin người dùng"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getTripsByUser(userId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        // FIX: Ép kiểu tường minh từ List<Any> sang List<Trip>
                        val data = body.data as? List<Trip>
                        _trips.value = data ?: emptyList()
                    }
                } else {
                    _error.value = "Không thể tải danh sách chuyến đi"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Bước 2: Tạo chuyến đi mới và gửi lên Backend
     */
    fun createTrip(
        destination: String,
        startDate: String,
        endDate: String,
        tripType: String,
        transport: String,
        onSuccess: (Int) -> Unit
    ) {
        val userId = tokenManager.getUserId()
        if (userId == -1) {
            _error.value = "Phiên đăng nhập hết hạn"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val request = CreateTripRequest(
                    user_id = userId,
                    destination = destination,
                    start_date = startDate,
                    end_date = endDate,
                    trip_type = tripType,
                    transport = transport
                )
                
                val response = apiService.createTrip(request)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        val newTripData = body.data
                        fetchUserTrips()
                        // FIX: Sử dụng trip_id thay vì id để khớp với data class trong ApiService
                        onSuccess(newTripData.trip_id)
                    }
                } else {
                    _error.value = "Lỗi từ máy chủ: Không thể tạo chuyến đi"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}