package com.example.smartfashion.ui.screens.closet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.CategoryRepository
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.data.repository.TagRepository
import com.example.smartfashion.model.Category
import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiAnalyzedData = MutableStateFlow<com.example.smartfashion.data.api.AiClothingData?>(null)
    val aiAnalyzedData: StateFlow<com.example.smartfashion.data.api.AiClothingData?> = _aiAnalyzedData.asStateFlow()

    fun analyzeImage(imageUrl: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            try {
                val response = clothingRepository.analyzeClothingWithAi(imageUrl)
                if (response.isSuccessful && response.body()?.success == true) {
                    _aiAnalyzedData.value = response.body()?.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun resetAiData() {
        _aiAnalyzedData.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    init {
        fetchCategoriesAndTags()
    }

    private fun fetchCategoriesAndTags() {
        viewModelScope.launch {
            try {
                val catResponse = categoryRepository.fetchCategories()
                if (catResponse.isSuccessful) {
                    _categories.value = catResponse.body() ?: emptyList()
                }

                val tagResponse = tagRepository.getTags()
                if (tagResponse.isSuccessful) {
                    _tags.value = tagResponse.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveNewItem(
        userId: Int,
        imageId: Int,
        categoryId: Int,
        name: String,
        colorHex: String,
        colorFamily: String,
        brand: String,
        size: String,
        material: String,
        imageUrl: String,
        seasons: Set<String>,
        weathers: Set<String>,
        occasions: Set<String>,
        styles: Set<String>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allTags = mutableListOf<String>()
                allTags.addAll(seasons)
                allTags.addAll(weathers)
                allTags.addAll(occasions)
                allTags.addAll(styles)

                val newItem = Clothing(
                    userId = userId,
                    imageId = imageId,
                    categoryId = categoryId,
                    name = name,
                    colorHex = colorHex,
                    colorFamily = colorFamily,
                    brandName = brand,
                    size = size,
                    material = material,
                    imageUrl = imageUrl,
                    isFavorite = false,
                    status = "active",
                    tags = if (allTags.isNotEmpty()) allTags else null
                )

                Log.d("API_SAVE", "Đang gửi dữ liệu lên Server: $newItem")

                val response = clothingRepository.addClothing(newItem)
                if (response.isSuccessful) {
                    Log.d("API_SAVE", "Lưu thành công!")
                    _saveSuccess.value = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("API_SAVE", "Server từ chối: $errorBody")
                    _errorMessage.value = "Lỗi Server: $errorBody"
                }
            } catch (e: Exception) {
                Log.e("API_SAVE", "Sập mạng/Lỗi code: ${e.message}")
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _saveSuccess.value = false
    }
}