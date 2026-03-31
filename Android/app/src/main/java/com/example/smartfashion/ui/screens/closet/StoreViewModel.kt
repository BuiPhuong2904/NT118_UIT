package com.example.smartfashion.ui.screens.closet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.CategoryRepository
import com.example.smartfashion.data.repository.StoreRepository
import com.example.smartfashion.model.Category
import com.example.smartfashion.model.SystemClothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _storeItems = MutableStateFlow<List<SystemClothing>>(emptyList())
    val storeItems: StateFlow<List<SystemClothing>> = _storeItems.asStateFlow()

    private val _parentCategories = MutableStateFlow<List<Category>>(emptyList())
    val parentCategories: StateFlow<List<Category>> = _parentCategories.asStateFlow()

    private val _selectedCategories = MutableStateFlow<List<Category>>(emptyList())
    val selectedCategories: StateFlow<List<Category>> = _selectedCategories.asStateFlow()

    private val _filterGroups = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val filterGroups: StateFlow<Map<String, List<String>>> = _filterGroups.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val selectedFilters: StateFlow<Map<String, List<String>>> = _selectedFilters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- STATE TÌM KIẾM ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    private var currentPage = 1
    private val pageSize = 7
    private var isLastPage = false
    private var isFetching = false

    init {
        fetchStoreData()
        fetchSystemClothes(isRefresh = true)
    }

    // --- HÀM XỬ LÝ GÕ TÌM KIẾM (DEBOUNCE 300ms) ---
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            fetchSystemClothes(isRefresh = true)
        }
    }

    fun updateCategoryFilter(category: Category) {
        val currentList = _selectedCategories.value.toMutableList()
        if (currentList.any { it.categoryId == category.categoryId }) {
            currentList.removeAll { it.categoryId == category.categoryId }
        } else {
            currentList.add(category)
        }
        _selectedCategories.value = currentList
        fetchSystemClothes(isRefresh = true)
    }

    fun updateFilter(groupName: String, option: String) {
        val currentFilters = _selectedFilters.value.toMutableMap()
        val currentOptionsInGroup = currentFilters[groupName]?.toMutableList() ?: mutableListOf()

        if (currentOptionsInGroup.contains(option)) {
            currentOptionsInGroup.remove(option)
        } else {
            currentOptionsInGroup.add(option)
        }

        if (currentOptionsInGroup.isEmpty()) {
            currentFilters.remove(groupName)
        } else {
            currentFilters[groupName] = currentOptionsInGroup
        }

        _selectedFilters.value = currentFilters
        fetchSystemClothes(isRefresh = true)
    }

    fun clearAllFilters() {
        if (_selectedFilters.value.isEmpty() && _selectedCategories.value.isEmpty()) return
        _selectedFilters.value = emptyMap()
        _selectedCategories.value = emptyList()
        fetchSystemClothes(isRefresh = true)
    }

    fun fetchSystemClothes(isRefresh: Boolean = false) {
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
                val tagsToFilter = _selectedFilters.value.values.flatten()
                val categoryIdsToFilter = if (_selectedCategories.value.isNotEmpty()) {
                    _selectedCategories.value.mapNotNull { it.categoryId }
                } else null

                val currentSearch = _searchQuery.value

                val response = storeRepository.fetchSystemClothesPaginated(
                    page = currentPage,
                    limit = pageSize,
                    tags = tagsToFilter,
                    categoryId = categoryIdsToFilter,
                    search = if (currentSearch.isNotBlank()) currentSearch else null // <--- Truyền Search
                )

                if (response.isSuccessful) {
                    val newList = response.body() ?: emptyList()

                    if (newList.size < pageSize) {
                        isLastPage = true
                    }

                    if (isRefresh) {
                        _storeItems.value = newList
                    } else {
                        val currentList = _storeItems.value.toMutableList()
                        currentList.addAll(newList)
                        _storeItems.value = currentList
                    }

                    currentPage++
                }
            } catch (e: Exception) {
                println("Lỗi tải System Clothes: ${e.message}")
            } finally {
                isFetching = false
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        fetchSystemClothes(isRefresh = false)
    }

    private fun fetchStoreData() {
        viewModelScope.launch {
            try {
                val catRes = categoryRepository.fetchCategories()
                if (catRes.isSuccessful) {
                    val allCats = catRes.body() ?: emptyList()
                    val parentCats = allCats.filter { it.parentId == null || it.parentId == 0 }
                    _parentCategories.value = parentCats
                }

                val tagRes = storeRepository.fetchTags()
                if (tagRes.isSuccessful) {
                    val tags = tagRes.body() ?: emptyList()
                    val groupedMap = linkedMapOf<String, MutableList<String>>(
                        "Mùa" to mutableListOf(),
                        "Thời tiết" to mutableListOf(),
                        "Dịp" to mutableListOf(),
                        "Phong cách" to mutableListOf()
                    )
                    tags.forEach { tag ->
                        val groupName = when (tag.tagGroup) {
                            "Season" -> "Mùa"
                            "Weather" -> "Thời tiết"
                            "Occasion" -> "Dịp"
                            "Style" -> "Phong cách"
                            else -> tag.tagGroup
                        }
                        if (groupedMap.containsKey(groupName)) {
                            groupedMap[groupName]?.add(tag.tagName)
                        } else {
                            groupedMap[groupName] = mutableListOf(tag.tagName)
                        }
                    }
                    _filterGroups.value = groupedMap
                }
            } catch (e: Exception) {
                println("Lỗi tải dữ liệu cơ bản: ${e.message}")
            }
        }
    }

    fun updateFavoriteStatus(item: SystemClothing, isFavorite: Boolean) {
        val templateId = item.templateId ?: return
        val updatedItem = item.copy(isFavorite = isFavorite)
        val currentList = _storeItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.templateId == templateId }

        if (index != -1) {
            currentList[index] = updatedItem
            _storeItems.value = currentList
        }

        viewModelScope.launch {
            try {
                val response = storeRepository.updateSystemClothing(templateId, updatedItem)
                if (!response.isSuccessful) {
                    rollbackFavoriteStatus(templateId, !isFavorite)
                }
            } catch (e: Exception) {
                rollbackFavoriteStatus(templateId, !isFavorite)
            }
        }
    }

    private fun rollbackFavoriteStatus(templateId: Int, oldFavoriteStatus: Boolean) {
        val currentList = _storeItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.templateId == templateId }
        if (index != -1) {
            val item = currentList[index]
            currentList[index] = item.copy(isFavorite = oldFavoriteStatus)
            _storeItems.value = currentList
        }
    }

    fun updateItemFavoriteLocal(templateId: Int, isFavorite: Boolean) {
        val currentList = _storeItems.value.toMutableList()
        val index = currentList.indexOfFirst { it.templateId == templateId }

        if (index != -1) {
            currentList[index] = currentList[index].copy(isFavorite = isFavorite)
            _storeItems.value = currentList
        }
    }
}