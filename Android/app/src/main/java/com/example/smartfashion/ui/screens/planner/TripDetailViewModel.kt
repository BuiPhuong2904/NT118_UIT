package com.example.smartfashion.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.AssignOutfitRequest
import com.example.smartfashion.data.api.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import com.example.smartfashion.data.ai.GeminiService
import com.example.smartfashion.model.PackingItem
import com.example.smartfashion.data.api.CreatePackingRequest
import com.example.smartfashion.data.api.PackingItemCreate
import android.util.Log
import com.example.smartfashion.data.api.DayPlan
import com.example.smartfashion.data.api.PlannedOutfit
import com.example.smartfashion.data.api.RemoveOutfitRequest
import com.example.smartfashion.data.api.SinglePackingItemRequest
import com.example.smartfashion.data.api.UpdatePackingItemRequest

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val apiService: ApiService,
    private val geminiService: GeminiService
) : ViewModel() {
    private var cachedTrip: Trip? = null
    var trip by mutableStateOf<Trip?>(null)
        private set
    var dayPlans by mutableStateOf<List<DayPlan>>(emptyList())
        private set
    var packingItems by mutableStateOf<List<PackingItem>>(emptyList())
        private set
    var isLoading by mutableStateOf(false)
        private set
    private var currentTripId: Int = 0
    private val outfitCache = mutableMapOf<Int, String?>()

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
                        loadPackingItems()
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun getOutfitImage(outfitId: Int): String? {
        return try {
            outfitCache[outfitId]?.let { return it }
            val response = apiService.getOutfitById(outfitId)
            val image = if (response.isSuccessful) response.body()?.data?.imagePreviewUrl else null
            outfitCache[outfitId] = image
            image
        } catch (e: Exception) { null }
    }

    private suspend fun loadPackingItems() {
        try {
            val response = apiService.getPackingItems(currentTripId)
            if (response.isSuccessful && !response.body()?.data.isNullOrEmpty()) {
                packingItems = response.body()!!.data
                updatePackingProgress()
            } else {
                generateChecklistAI()
            }
        } catch (e: Exception) {
            generateChecklistAI()
        }
    }

    private suspend fun buildDayPlans(trip: Trip): List<DayPlan> {
        val start = LocalDate.parse(trip.start_date.take(10))
        val end = LocalDate.parse(trip.end_date.take(10))
        if (end.isBefore(start)) return emptyList()

        val totalDays = ChronoUnit.DAYS.between(start, end).toInt() + 1
        val schedule = trip.outfitSchedule.orEmpty()

        return List(totalDays) { index ->
            val dayNum = index + 1
            val currentDate = start.plusDays(index.toLong())

            val dayOutfits = schedule.filter { it.day == dayNum }.mapNotNull {
                val imageUrl = it.outfitImage ?: getOutfitImage(it.outfitId)
                if (imageUrl != null) PlannedOutfit(it.outfitId, imageUrl) else null
            }

            DayPlan(
                dayNumber = dayNum,
                date = currentDate,
                location = trip.destination,
                weatherTemp = "25°C",
                isSunny = true,
                outfits = dayOutfits // Gán nguyên List vào đây
            )
        }
    }

    fun assignOutfitToDay(dayNumber: Int, outfitId: Int, imageUrl: String?) {
        if (imageUrl == null) return
        val old = dayPlans

        dayPlans = dayPlans.map { plan ->
            if (plan.dayNumber == dayNumber) {
                plan.copy(outfits = plan.outfits + PlannedOutfit(outfitId, imageUrl))
            } else plan
        }

        viewModelScope.launch {
            try {
                val response = apiService.assignOutfitToDay(
                    currentTripId,
                    AssignOutfitRequest(day = dayNumber, outfit_id = outfitId, outfit_image = imageUrl)
                )
                if (response.isSuccessful) {
                    outfitCache[outfitId] = imageUrl
                    response.body()?.data?.let { updatedTrip ->
                        trip = updatedTrip
                        dayPlans = buildDayPlans(updatedTrip)
                    }
                } else {
                    dayPlans = old
                }
            } catch (e: Exception) {
                dayPlans = old
            }
        }
    }

    fun removeOutfitFromDay(dayNumber: Int, outfitId: Int) {
        val old = dayPlans
        dayPlans = dayPlans.map { plan ->
            if (plan.dayNumber == dayNumber) {
                plan.copy(outfits = plan.outfits.filter { it.outfitId != outfitId })
            } else plan
        }

        viewModelScope.launch {
            try {
                val response = apiService.removeOutfitFromDay(
                    currentTripId,
                    RemoveOutfitRequest(day = dayNumber, outfit_id = outfitId)
                )
                if (response.isSuccessful) {
                    response.body()?.data?.let { updatedTrip ->
                        trip = updatedTrip
                        dayPlans = buildDayPlans(updatedTrip)
                    }
                } else {
                    dayPlans = old
                }
            } catch (e: Exception) {
                dayPlans = old
            }
        }
    }

    private suspend fun generateChecklistAI() {
        val result = geminiService.generateChecklist(
            destination = trip?.destination ?: "",
            tripType = trip?.trip_type ?: "",
            transport = trip?.transport ?: ""
        )
        val aiItems = result.lines()
            .map { it.trim() }
            .filter {
                it.isNotBlank() &&
                        !it.contains("chào", ignoreCase = true) &&
                        !it.contains("checklist", ignoreCase = true) &&
                        !it.contains("dưới đây", ignoreCase = true)
            }
            .map {
                PackingItemCreate(
                    name = it.replace("-", "").replace("*", "").replace(Regex("""^\d+\."""), "").trim(),
                    category = "AI Suggestion"
                )
            }
        try {
            val response = apiService.createPackingItems(CreatePackingRequest(tripId = currentTripId, items = aiItems))
            if (response.isSuccessful) {
                packingItems = response.body()?.data ?: emptyList()
                updatePackingProgress()
                loadTrip(currentTripId)
            }
        } catch (e: Exception) {
            packingItems = emptyList()
        }
    }

    private fun updatePackingProgress() {
        val packed = packingItems.count { it.isPacked }
        val total = packingItems.size
        trip = trip?.copy(packed_items = packed, total_items = total)
    }

    fun togglePacked(itemId: String) {
        val old = packingItems
        packingItems = packingItems.map { if (it.id == itemId) it.copy(isPacked = !it.isPacked) else it }
        updatePackingProgress()
        viewModelScope.launch {
            try {
                val response = apiService.togglePackingItem(itemId)
                if (response.isSuccessful) loadTrip(currentTripId)
                else { packingItems = old; updatePackingProgress() }
            } catch (e: Exception) {
                packingItems = old
                updatePackingProgress()
            }
        }
    }

    fun deleteTrip(onSuccess: () -> Unit) {
        if (currentTripId == 0) return
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.deleteTrip(currentTripId)
                if (response.isSuccessful) onSuccess()
            } catch (e: Exception) {
                Log.e("DELETE_TRIP", "Exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun addPackingItem(name: String, onSuccess: () -> Unit) {
        if (name.isBlank()) return
        val tempId = "temp_${System.currentTimeMillis()}"
        val newItem = PackingItem(id = tempId, name = name, category = "Cá nhân", isPacked = false)
        packingItems = listOf(newItem) + packingItems
        updatePackingProgress()

        viewModelScope.launch {
            try {
                val response = apiService.addPackingItem(SinglePackingItemRequest(currentTripId, name))
                if (response.isSuccessful) {
                    val realItem = response.body()?.data
                    if (realItem != null) {
                        packingItems = packingItems.map { if (it.id == tempId) realItem else it }
                    } else {
                        loadTrip(currentTripId)
                    }
                    onSuccess()
                } else {
                    packingItems = packingItems.filter { it.id != tempId }
                    updatePackingProgress()
                }
            } catch (e: Exception) {
                packingItems = packingItems.filter { it.id != tempId }
                updatePackingProgress()
            }
        }
    }

    fun editPackingItem(itemId: String, newName: String) {
        if (newName.isBlank()) return
        packingItems = packingItems.map { if (it.id == itemId) it.copy(name = newName) else it }
        viewModelScope.launch {
            try {
                apiService.updatePackingItem(itemId, UpdatePackingItemRequest(newName))
            } catch (e: Exception) {
                Log.e("EDIT_ITEM", "Error: ${e.message}")
            }
        }
    }

    fun deletePackingItem(itemId: String, onSuccess: () -> Unit) {
        packingItems = packingItems.filter { it.id != itemId }
        updatePackingProgress()
        viewModelScope.launch {
            try {
                apiService.deletePackingItem(itemId)
                onSuccess()
            } catch (e: Exception) {
                Log.e("DELETE_ITEM", "Error: ${e.message}")
            }
        }
    }
}