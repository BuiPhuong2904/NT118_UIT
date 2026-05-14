package com.example.smartfashion.ui.viewmodel

import androidx.compose.runtime.*
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
    private val apiService: ApiService
) : ViewModel() {

    var trip by mutableStateOf<Trip?>(null)
        private set

    var dayPlans by mutableStateOf<List<DayPlan>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // ✅ Lưu lại tripId hiện tại
    private var currentTripId: Int = 0

    // ===============================
    // LOAD TRIP
    // ===============================
    fun loadTrip(id: Int) {
        if (id == 0) return

        currentTripId = id

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

    // ===============================
    // ASSIGN OUTFIT
    // ===============================
    fun assignOutfitToDay(
        dayNumber: Int,
        outfitId: Int,
        imageUrl: String?
    ) {
        val oldPlans = dayPlans

        // ✅ Update UI trước (optimistic update)
        dayPlans = dayPlans.map {
            if (it.dayNumber == dayNumber) {
                it.copy(outfitImageUrl = imageUrl)
            } else it
        }

        // ✅ Gọi API
        viewModelScope.launch {
            try {
                apiService.assignOutfitToDay(
                    currentTripId,
                    dayNumber,
                    outfitId
                )
            } catch (e: Exception) {
                e.printStackTrace()

                // ❗ Rollback nếu lỗi
                dayPlans = oldPlans
            }
        }
    }

    // ===============================
    // GENERATE DAY PLANS
    // ===============================
    private fun generateDayPlans(trip: Trip): List<DayPlan> {
        return try {
            val start = LocalDate.parse(trip.start_date.split("T")[0], formatter)
            val end = LocalDate.parse(trip.end_date.split("T")[0], formatter)

            val totalDays = ChronoUnit.DAYS.between(start, end).toInt() + 1

            List(totalDays) { index ->
                val currentDate = start.plusDays(index.toLong())
                val dayNum = index + 1

                DayPlan(
                    dayNumber = dayNum,
                    date = currentDate,
                    location = trip.destination,
                    weatherTemp = "30°C",
                    isSunny = true,
                    outfitImageUrl = dayPlans.find { it.dayNumber == dayNum }?.outfitImageUrl
                )
            }

        } catch (e: Exception) {
            emptyList()
        }
    }
}
