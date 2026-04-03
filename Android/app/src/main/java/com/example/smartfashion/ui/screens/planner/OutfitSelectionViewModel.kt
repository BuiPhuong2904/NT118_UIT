package com.example.smartfashion.ui.screens.planner

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.OutfitRepository
import com.example.smartfashion.model.Outfit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutfitSelectionViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadOutfits(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Gọi API lấy danh sách tủ đồ của User
                val response = outfitRepository.getOutfitsByUser(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _outfits.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("OutfitSelectionVM", "Lỗi tải danh sách: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}