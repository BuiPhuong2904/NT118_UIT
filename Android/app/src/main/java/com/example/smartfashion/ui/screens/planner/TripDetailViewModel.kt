package com.example.smartfashion.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.AssignOutfitRequest
import com.example.smartfashion.data.api.Trip
import com.example.smartfashion.model.Outfit
import com.example.smartfashion.ui.screens.planner.DayPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private var cachedTrip: Trip? = null

    var trip by mutableStateOf<Trip?>(null)
        private set

    var dayPlans by mutableStateOf<List<DayPlan>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    private var currentTripId: Int = 0

    private val outfitCache = mutableMapOf<Int, String?>()

    // =========================
    // LOAD TRIP
    // =========================
    fun loadTrip(id: Int) {
        if (id == 0) return

        currentTripId = id

        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getTripById(id)

                if (response.isSuccessful) {
                    response.body()?.data?.let { data ->
                        cachedTrip = data
                        trip = data
                        dayPlans = buildDayPlans(data)
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }

    // =========================
    // GET OUTFIT IMAGE (FIX HERE)
    // =========================
    private suspend fun getOutfitImage(outfitId: Int): String? {
        return try {

            outfitCache[outfitId]?.let { return it }

            val response = apiService.getOutfitById(outfitId)

            val image = if (response.isSuccessful) {
                response.body()?.data?.imagePreviewUrl   // ✅ FIX HERE
            } else null

            outfitCache[outfitId] = image
            image

        } catch (e: Exception) {
            null
        }
    }

    // =========================
    // BUILD DAY PLANS
    // =========================
    private suspend fun buildDayPlans(trip: Trip): List<DayPlan> {

        val start = LocalDate.parse(trip.start_date.take(10))
        val end = LocalDate.parse(trip.end_date.take(10))

        if (end.isBefore(start)) return emptyList()

        val totalDays = ChronoUnit.DAYS.between(start, end).toInt() + 1
        val schedule = trip.outfitSchedule.orEmpty()

        return List(totalDays) { index ->

            val dayNum = index + 1
            val currentDate = start.plusDays(index.toLong())

            val matched = schedule.firstOrNull { it.day == dayNum }

            val imageUrl = matched?.let {
                getOutfitImage(it.outfitId)
            }

            DayPlan(
                dayNumber = dayNum,
                date = currentDate,
                location = trip.destination,
                weatherTemp = "30°C",
                isSunny = true,

                // ưu tiên cache API nếu có
                outfitImageUrl = matched?.outfitImage ?: imageUrl
            )
        }
    }

    // =========================
    // ASSIGN OUTFIT (REALTIME + SYNC DB)
    // =========================
    fun assignOutfitToDay(
        dayNumber: Int,
        outfitId: Int,
        imageUrl: String?
    ) {
        val old = dayPlans

        // realtime UI
        dayPlans = dayPlans.map {
            if (it.dayNumber == dayNumber)
                it.copy(outfitImageUrl = imageUrl)
            else it
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

                if (response.isSuccessful) {
                    outfitCache[outfitId] = imageUrl
                } else {
                    dayPlans = old
                }

            } catch (e: Exception) {
                dayPlans = old
            }
        }
    }
}