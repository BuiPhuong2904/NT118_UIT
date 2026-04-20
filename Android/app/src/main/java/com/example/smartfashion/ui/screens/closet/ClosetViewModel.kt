package com.example.smartfashion.ui.screens.closet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.CategoryRepository
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.model.Category
import com.example.smartfashion.model.Clothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@HiltViewModel
class ClosetViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _clothingList = MutableStateFlow<List<Clothing>>(emptyList())
    val clothingList: StateFlow<List<Clothing>> = _clothingList.asStateFlow()

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(0)
    val selectedCategoryId: StateFlow<Int> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null
    private var currentPage = 1
    private val pageSize = 7
    private var isLastPage = false
    private var isFetching = false

    init {
        fetchCategories()
    }

    // Hàm xóa lỗi sau khi màn hình đã hiển thị Toast xong
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun onSearchQueryChanged(query: String, userId: Int) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            fetchClothesForUser(userId = userId, isRefresh = true)
        }
    }

    fun onCategorySelected(categoryId: Int, userId: Int) {
        if (_selectedCategoryId.value == categoryId) return
        _selectedCategoryId.value = categoryId
        fetchClothesForUser(userId = userId, isRefresh = true)
    }

    fun fetchClothesForUser(userId: Int, isRefresh: Boolean = false) {
        if (isFetching) return

        if (isRefresh) {
            currentPage = 1
            isLastPage = false
        }

        if (isLastPage) return

        viewModelScope.launch {
            isFetching = true
            _isLoading.value = true
            try {
                val currentCategory = _selectedCategoryId.value
                val currentSearch = _searchQuery.value

                val response = clothingRepository.fetchClothesByUserId(
                    userId = userId,
                    categoryId = currentCategory,
                    page = currentPage,
                    limit = pageSize,
                    search = if (currentSearch.isNotBlank()) currentSearch else null
                )

                if (response.isSuccessful) {
                    response.body()?.let { newList ->
                        if (newList.size < pageSize) {
                            isLastPage = true
                        }

                        if (isRefresh) {
                            _clothingList.value = newList
                        } else {
                            val currentList = _clothingList.value.toMutableList()
                            currentList.addAll(newList)
                            _clothingList.value = currentList
                        }
                        currentPage++
                    }
                } else {
                    // Lỗi từ phía Server
                    _errorMessage.value = "Hệ thống đang bận. Không thể tải tủ đồ lúc này."
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi ở ClosetViewModel - fetchClothes: ", e)

                _errorMessage.value = "Mất kết nối mạng. Vui lòng kiểm tra Wifi/4G của bạn."
            } finally {
                isFetching = false
                _isLoading.value = false
            }
        }
    }

    fun loadMore(userId: Int) {
        fetchClothesForUser(userId, isRefresh = false)
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = categoryRepository.fetchCategories()
                if (response.isSuccessful) {
                    response.body()?.let { serverCategories ->
                        val allCategory = Category(categoryId = 0, name = "Tất cả", parentId = null)
                        val fullList = mutableListOf(allCategory)
                        fullList.addAll(serverCategories)
                        _categoryList.value = fullList
                    }
                } else {
                    _errorMessage.value = "Không thể tải danh mục phân loại đồ."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi mạng khi tải danh mục. Vui lòng thử lại."
            }
        }
    }

    fun updateFavoriteStatus(item: Clothing, newFavoriteStatus: Boolean) {
        viewModelScope.launch {
            try {
                val id = item.clothingId ?: return@launch
                val updatedItem = item.copy(isFavorite = newFavoriteStatus)
                val response = clothingRepository.updateClothing(id, updatedItem)

                if (response.isSuccessful) {
                    val currentList = _clothingList.value.toMutableList()
                    val index = currentList.indexOfFirst { it.clothingId == id }
                    if (index != -1) {
                        currentList[index] = updatedItem
                        _clothingList.value = currentList
                    }
                } else {
                    _errorMessage.value = "Không thể cập nhật mục Yêu thích lúc này."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Mất mạng. Không thể thả tim cho món đồ."
            }
        }
    }
}