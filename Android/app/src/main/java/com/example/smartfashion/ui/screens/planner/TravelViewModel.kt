package com.example.smartfashion.ui.screens.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.CreateTripRequest
import com.example.smartfashion.data.api.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TravelViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchUserTrips()
    }

    // ================= GET TRIPS =================
    fun fetchUserTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = apiService.getMyTrips() 

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success) {
                        _trips.value = body.data 
                    } else {
                        _error.value = "Dữ liệu không hợp lệ"
                    }
                } else {
                    _error.value = "Server error: ${response.code()}"
                }

            } catch (e: Exception) {
                _error.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ================= CREATE TRIP =================
    fun createTrip(
        destination: String,
        startDate: String,
        endDate: String,
        tripType: String,
        transport: String,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val request = CreateTripRequest(
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
                        val newTrip = body.data
                        fetchUserTrips() // reload list
                        onSuccess(newTrip.trip_id)
                    } else {
                        _error.value = "Không tạo được chuyến đi"
                    }
                } else {
                    _error.value = "Server error: ${response.code()}"
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
