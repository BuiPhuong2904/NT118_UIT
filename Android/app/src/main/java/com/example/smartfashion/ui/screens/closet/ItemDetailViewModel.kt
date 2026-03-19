package com.example.smartfashion.ui.screens.closet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.CategoryRepository
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.model.Clothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _clothingItem = MutableStateFlow<Clothing?>(null)
    val clothingItem: StateFlow<Clothing?> = _clothingItem.asStateFlow()

    private val _categoryName = MutableStateFlow("Đang tải...")
    val categoryName: StateFlow<String> = _categoryName.asStateFlow()

    fun fetchClothingDetail(id: Int) {
        viewModelScope.launch {
            try {
                val response = clothingRepository.fetchClothingById(id)
                if (response.isSuccessful) {
                    val item = response.body()
                    _clothingItem.value = item

                    item?.categoryId?.let { catId ->
                        fetchCategoryHierarchy(catId)
                    }
                } else {
                    println("LỖI SERVER: HTTP ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println("LỖI MẠNG HOẶC APP: ${e.message}")
            }
        }
    }

    private suspend fun fetchCategoryHierarchy(categoryId: Int) {
        try {
            val childResponse = categoryRepository.getCategoryById(categoryId)

            if (childResponse.isSuccessful) {
                val childCat = childResponse.body()

                if (childCat != null) {
                    if (childCat.parentId != null) {
                        val parentResponse = categoryRepository.getCategoryById(childCat.parentId)

                        if (parentResponse.isSuccessful) {
                            val parentCat = parentResponse.body()
                            _categoryName.value = "${parentCat?.name ?: "Không rõ"} > ${childCat.name}"
                        } else {
                            _categoryName.value = childCat.name
                        }
                    } else {
                        _categoryName.value = childCat.name
                    }
                } else {
                    _categoryName.value = "Chưa phân loại"
                }
            }
        } catch (e: Exception) {
            _categoryName.value = "Lỗi tải danh mục"
        }
    }

    fun updateClothingStatus(newStatus: String) {
        val currentItem = _clothingItem.value ?: return
        val id = currentItem.clothingId ?: return

        val updatedItem = currentItem.copy(status = newStatus)

        viewModelScope.launch {
            try {
                _clothingItem.value = updatedItem
                clothingRepository.updateClothing(id, updatedItem)
            } catch (e: Exception) {
                println("Lỗi khi cập nhật trạng thái: ${e.message}")
            }
        }
    }

    fun deleteClothing(id: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = clothingRepository.deleteClothing(id)
                if (response.isSuccessful) {
                    onSuccess()
                }
            } catch (e: Exception) {
                println("Lỗi khi xóa: ${e.message}")
            }
        }
    }

    fun updateClothingDetails(updatedItem: Clothing) {
        viewModelScope.launch {
            try {
                _clothingItem.value = updatedItem
                val id = updatedItem.clothingId ?: return@launch
                clothingRepository.updateClothing(id, updatedItem)
            } catch (e: Exception) {
                println("Lỗi khi cập nhật thông tin: ${e.message}")
            }
        }
    }
}