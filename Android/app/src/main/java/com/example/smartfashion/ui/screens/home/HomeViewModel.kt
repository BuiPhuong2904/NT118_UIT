package com.example.smartfashion.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.repository.OutfitRepository
import com.example.smartfashion.model.Outfit
import com.example.smartfashion.model.SystemClothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val outfitRepository: OutfitRepository,
    private val apiService: ApiService
) : ViewModel() {

    // State lưu bộ phối đồ gợi ý hôm nay
    private val _recommendedOutfit = MutableStateFlow<Outfit?>(null)
    val recommendedOutfit: StateFlow<Outfit?> = _recommendedOutfit.asStateFlow()

    // State lưu danh sách xu hướng (kho mẫu hệ thống)
    private val _trendingItems = MutableStateFlow<List<SystemClothing>>(emptyList())
    val trendingItems: StateFlow<List<SystemClothing>> = _trendingItems.asStateFlow()

    fun loadHomeData(userId: Int) {
        viewModelScope.launch {
            try {
                // 1. Lấy danh sách outfit của user -> Chọn ngẫu nhiên 1 bộ làm gợi ý
                val outfitRes = outfitRepository.getOutfitsByUser(userId)
                if (outfitRes.isSuccessful && outfitRes.body()?.success == true) {
                    val outfits = outfitRes.body()?.data ?: emptyList()
                    if (outfits.isNotEmpty()) {
                        _recommendedOutfit.value = outfits.random() // Lấy ngẫu nhiên 1 bộ
                    }
                }

                // 2. Lấy danh sách "Xu hướng" từ kho mẫu hệ thống (SystemClothes)
                val trendRes = apiService.getSystemClothesPaginated(page = 1, limit = 5, tags = null, categoryId = null)
                if (trendRes.isSuccessful) {
                    _trendingItems.value = trendRes.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Lỗi tải dữ liệu Home: ${e.message}")
            }
        }
    }
}