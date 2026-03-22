package com.example.smartfashion.ui.screens.closet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.model.Clothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeclutterViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository
) : ViewModel() {

    private val _declutterItems = MutableStateFlow<List<Clothing>>(emptyList())
    val declutterItems: StateFlow<List<Clothing>> = _declutterItems.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchDeclutterClothes(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = clothingRepository.getDeclutterClothesByUser(userId)
                if (response.isSuccessful) {
                    _declutterItems.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Nút "Giữ lại"
    fun keepItem(item: Clothing) {
        _declutterItems.value = _declutterItems.value.filter { it.clothingId != item.clothingId }
    }

    // Nút "Thanh lý"
    fun deleteItem(item: Clothing) {
        _declutterItems.value = _declutterItems.value.filter { it.clothingId != item.clothingId }

        item.clothingId?.let { id ->
            viewModelScope.launch {
                try {
                    clothingRepository.deleteClothing(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Xử lý lỗi
                }
            }
        }
    }
}