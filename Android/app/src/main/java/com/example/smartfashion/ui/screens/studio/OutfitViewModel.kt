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

    // Danh sách outfit
    private val _outfits = MutableStateFlow<List<Outfit>>(emptyList())
    val outfits: StateFlow<List<Outfit>> = _outfits.asStateFlow()

    // Trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchOutfits(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getOutfitsByUser(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Lấy cục data gán vào danh sách
                    _outfits.value = response.body()?.data ?: emptyList()
                } else {
                    // Xử lý khi API lỗi (có thể gán 1 biến _errorMessage)
                }
            } catch (e: Exception) {
                // Xử lý lỗi sập mạng, timeout...
            } finally {
                _isLoading.value = false
            }
        }
    }
}