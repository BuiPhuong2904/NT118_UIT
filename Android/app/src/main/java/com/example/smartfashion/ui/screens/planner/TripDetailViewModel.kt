package com.example.smartfashion.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.Trip
import com.example.smartfashion.ui.screens.planner.DayPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var trip by mutableStateOf<Trip?>(null)
        private set

    var dayPlans by mutableStateOf<List<DayPlan>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val tripId: String? = savedStateHandle["tripId"]
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        tripId?.let { loadTrip(it.toIntOrNull() ?: 0) }
    }

    fun loadTrip(id: Int) {
        if (id == 0) return
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getTripById(id)
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    trip = data
                    data?.let { 
                        dayPlans = generateDayPlans(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun generateDayPlans(trip: Trip): List<DayPlan> {
        return try {
            val start = LocalDate.parse(trip.start_date, formatter)
            val end = LocalDate.parse(trip.end_date, formatter)
            val totalDays = ChronoUnit.DAYS.between(start, end).toInt() + 1

            List(totalDays) { index ->
                val currentDate = start.plusDays(index.toLong())
                DayPlan(
                    dayNumber = index + 1,
                    date = currentDate,
                    location = trip.destination,
                    weatherTemp = "30°C", // Mock tạm thời
                    isSunny = true,
                    outfitImageUrl = null // Sau này map từ API outfit nếu có
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}