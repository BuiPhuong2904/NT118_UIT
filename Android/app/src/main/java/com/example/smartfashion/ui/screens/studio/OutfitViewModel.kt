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
class OutfitViewModel @Inject constructor(
    private val repository: OutfitRepository
) : ViewModel() {

    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Hàm lấy danh sách phối đồ
    fun fetchOutfits(
        userId: Int,
        isFavorite: Boolean? = null,
        tags: List<String>? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getOutfitsByUser(userId, isFavorite, tags)

                if (response.isSuccessful && response.body()?.success == true) {
                    _outfits.value = response.body()?.data ?: emptyList()
                } else {
                    _outfits.value = emptyList()
                }
            } catch (e: Exception) {
                _outfits.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---> THÊM HÀM MỚI: Xử lý thả tim <---
    fun toggleFavorite(outfitId: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            // 1. OPTIMISTIC UI (Đổi màu ngay lập tức)
            val currentOutfits = _outfits.value.toMutableList()
            val index = currentOutfits.indexOfFirst { it.outfitId == outfitId }

            if (index != -1) {
                currentOutfits[index] = currentOutfits[index].copy(isFavorite = isFavorite)
                _outfits.value = currentOutfits
            }

            // 2. GỌI API NGẦM XUỐNG SERVER
            try {
                // Đã mở khóa gọi Repository
                val response = repository.updateFavoriteStatus(outfitId, isFavorite)

                if (!response.isSuccessful) {
                    // Nếu server báo lỗi (chưa đổi đc DB), trả lại màu tim cũ
                    if (index != -1) {
                        currentOutfits[index] = currentOutfits[index].copy(isFavorite = !isFavorite)
                        _outfits.value = currentOutfits
                    }
                }
            } catch (e: Exception) {
                // Nếu rớt mạng, trả lại màu tim cũ
                if (index != -1) {
                    currentOutfits[index] = currentOutfits[index].copy(isFavorite = !isFavorite)
                    _outfits.value = currentOutfits
                }
            }
        }
    }
}