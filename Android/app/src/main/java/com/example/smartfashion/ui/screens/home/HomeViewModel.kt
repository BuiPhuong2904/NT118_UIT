package com.example.smartfashion.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ClothingRepository
) : ViewModel() {

    fun loadClothes() {
        viewModelScope.launch {
            try {
                val response = repository.fetchAllClothes()
                if (response.isSuccessful) {
                    val clothesList = response.body()
                    Log.d("API_SUCCESS", "Lấy thành công: ${clothesList?.size} món đồ")
                    // Cập nhật lên StateFlow để giao diện (Compose) đọc được
                } else {
                    Log.e("API_ERROR", "Lỗi: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Lỗi mạng: ${e.message}")
            }
        }
    }
}