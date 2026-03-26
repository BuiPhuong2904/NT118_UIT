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

    fun loadClothes(userId: Int) {
        viewModelScope.launch {
            try {
                // Tạm thời gọi API lấy đồ (Sau này nếu API Repository của bạn cần userId thì truyền vào hàm fetchAllClothes(userId))
                val response = repository.fetchAllClothes()
                if (response.isSuccessful) {
                    val clothesList = response.body()
                    Log.d("API_SUCCESS", "User ID $userId lấy thành công: ${clothesList?.size} món đồ")
                    // TODO: Cập nhật lên StateFlow để giao diện (Compose) đọc được
                } else {
                    Log.e("API_ERROR", "Lỗi: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_EXCEPTION", "Lỗi mạng: ${e.message}")
            }
        }
    }
}