package com.example.smartfashion.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.Trip
import com.example.smartfashion.data.api.AssignOutfitRequest
import com.example.smartfashion.ui.screens.planner.DayPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.util.Log
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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
                    dayPlans = if (data != null) {
                        generateDayPlans(data)
                    } else {
                        emptyList()
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
    // ASSIGN OUTFIT (FIXED)
    // ===============================
    fun assignOutfitToDay(
        dayNumber: Int,
        outfitId: Int,
        imageUrl: String?
    ) {
        val oldPlans = dayPlans

        // 1. OPTIMISTIC UPDATE UI
        dayPlans = oldPlans.map { plan ->
            if (plan.dayNumber == dayNumber) {
                plan.copy(outfitImageUrl = imageUrl)
            } else plan
        }

        viewModelScope.launch {
            try {
                val response = apiService.assignOutfitToDay(
                    currentTripId,
                    AssignOutfitRequest(
                        day = dayNumber,
                        outfit_id = outfitId,
                        outfit_image = imageUrl
                    )
                )

                dayPlans = dayPlans.map { plan ->
                    if (plan.dayNumber == dayNumber) {
                        plan.copy(outfitImageUrl = imageUrl)
                    } else plan
                }

                Log.d("ASSIGN", "success=${response.isSuccessful}")
                Log.d("ASSIGN", "body=${response.body()}")
                Log.d("ASSIGN", "schedule=${response.body()?.data?.outfitSchedule}")

                if (response.isSuccessful) {
                    val updatedTrip = response.body()?.data

                    if (updatedTrip != null) {
                        trip = updatedTrip
                        //dayPlans = generateDayPlans(updatedTrip)
                    } else {
                        loadTrip(currentTripId)
                    }
                } else {
                    // rollback nếu API fail
                    dayPlans = oldPlans
                }

            } catch (e: Exception) {
                e.printStackTrace()
                dayPlans = oldPlans
            }
        }
    }

    // ===============================
    // GENERATE DAY PLANS
    // ===============================
    private fun generateDayPlans(trip: Trip): List<DayPlan> {
        return try {

            val start = LocalDate.parse(trip.start_date.take(10))
            val end = LocalDate.parse(trip.end_date.take(10))

            if (end.isBefore(start)) return emptyList()

            val totalDays = ChronoUnit.DAYS.between(start, end).toInt() + 1
            val schedule = trip.outfitSchedule.orEmpty()

            List(totalDays) { index ->
                val dayNum = index + 1
                val currentDate = start.plusDays(index.toLong())

                val matched = schedule.firstOrNull { it.day == dayNum }

                DayPlan(
                    dayNumber = dayNum,
                    date = currentDate,
                    location = trip.destination,
                    weatherTemp = "30°C",
                    isSunny = true,
                    outfitImageUrl = matched?.outfitImage
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}