package com.example.smartfashion.ui.screens.studio

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.OutfitRepository
import com.example.smartfashion.data.repository.TagRepository
import com.example.smartfashion.model.Outfit
import com.example.smartfashion.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OutfitDetailViewModel @Inject constructor(
    private val repository: OutfitRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _outfit = MutableStateFlow<Outfit?>(null)
    val outfit: StateFlow<Outfit?> = _outfit.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())
    val allTags: StateFlow<List<Tag>> = _allTags.asStateFlow()

    init {
        fetchAllTags()
    }

    private fun fetchAllTags() {
        viewModelScope.launch {
            try {
                val response = tagRepository.getTags()
                if (response.isSuccessful) {
                    _allTags.value = response.body() ?: emptyList()
                } else {
                    Log.e("OutfitViewModel", "Lỗi lấy Tags từ Server: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("OutfitViewModel", "Lỗi mạng khi lấy Tags: ${e.message}")
            }
        }
    }

    fun fetchOutfitDetail(outfitId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getOutfitById(outfitId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _outfit.value = response.body()?.data
                } else {
                    Log.e("OutfitViewModel", "Lỗi lấy chi tiết Outfit: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("OutfitViewModel", "Lỗi mạng khi lấy Outfit: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateOutfit(outfitId: Int, newName: String, newDesc: String, newTags: List<String>) {
        viewModelScope.launch {
            try {
                val requestBody = com.example.smartfashion.data.api.UpdateOutfitRequest(
                    name = newName, description = newDesc, tags = newTags
                )
                val response = repository.updateOutfit(outfitId, requestBody)

                if (response.isSuccessful) {
                    Log.d("OutfitViewModel", "🎉 Cập nhật thành công!")
                    _outfit.value = _outfit.value?.copy(
                        name = newName,
                        description = newDesc,
                        tagNames = newTags
                    )
                } else {
                    Log.e("OutfitViewModel", "Lỗi Server khi Cập nhật: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("OutfitViewModel", "Lỗi App khi Cập nhật: ${e.message}")
            }
        }
    }

    fun deleteOutfit(outfitId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteOutfit(outfitId)
                if (response.isSuccessful) {
                    Log.d("OutfitViewModel", "Xóa thành công!")
                } else {
                    Log.e("OutfitViewModel", "Lỗi Server khi Xóa: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("OutfitViewModel", "Lỗi App khi Xóa: ${e.message}")
            }
        }
    }
}