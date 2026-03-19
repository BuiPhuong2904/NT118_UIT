package com.example.smartfashion.ui.screens.closet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.StoreRepository
import com.example.smartfashion.model.SystemClothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreItemDetailViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _systemItem = MutableStateFlow<SystemClothing?>(null)
    val systemItem: StateFlow<SystemClothing?> = _systemItem.asStateFlow()

    fun fetchSystemClothingDetail(templateId: Int) {
        viewModelScope.launch {
            try {
                val response = storeRepository.getSystemClothingById(templateId)
                if (response.isSuccessful) {
                    _systemItem.value = response.body()
                } else {
                    println("Lỗi API chi tiết: ${response.code()}")
                }
            } catch (e: Exception) {
                println("Lỗi mạng: ${e.message}")
            }
        }
    }

    // Hàm gọi khi bấm tim trong màn hình chi tiết
    fun updateFavoriteStatus(item: SystemClothing, isFavorite: Boolean) {
        val updatedItem = item.copy(isFavorite = isFavorite)
        _systemItem.value = updatedItem

        viewModelScope.launch {
            try {
                storeRepository.updateSystemClothing(item.templateId!!, updatedItem)
            } catch (e: Exception) {
                // Rollback nếu lỗi
                _systemItem.value = item
            }
        }
    }
}