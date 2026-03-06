package com.example.smartfashion.ui.screens.studio

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
class OutfitDetailViewModel @Inject constructor(
    private val repository: OutfitRepository
) : ViewModel() {

    private val _outfit = MutableStateFlow<Outfit?>(null)
    val outfit: StateFlow<Outfit?> = _outfit.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchOutfitDetail(outfitId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getOutfitById(outfitId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _outfit.value = response.body()?.data
                }
            } catch (e: Exception) {
                // Xử lý lỗi
            } finally {
                _isLoading.value = false
            }
        }
    }
}