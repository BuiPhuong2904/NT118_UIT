package com.example.smartfashion.ui.screens.closet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.data.repository.StoreRepository
import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.SystemClothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository,
    private val storeRepository: StoreRepository // <-- Thêm repository của Kho mẫu
) : ViewModel() {

    // 1. STATE CHO "TỦ ĐỒ CỦA TÔI" (Đồ cá nhân)
    private val _favoriteClothes = MutableStateFlow<List<Clothing>>(emptyList())
    val favoriteClothes: StateFlow<List<Clothing>> = _favoriteClothes.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _totalCount = MutableStateFlow(0)
    val totalCount: StateFlow<Int> = _totalCount.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false
    private val limit = 7
    private var isFetching = false

    // 2. STATE CHO "WISHLIST" (Kho mẫu)
    private val _wishlistClothes = MutableStateFlow<List<SystemClothing>>(emptyList())
    val wishlistClothes: StateFlow<List<SystemClothing>> = _wishlistClothes.asStateFlow()

    private val _isWishlistLoading = MutableStateFlow(true)
    val isWishlistLoading: StateFlow<Boolean> = _isWishlistLoading.asStateFlow()

    private val _wishlistTotalCount = MutableStateFlow(0)
    val wishlistTotalCount: StateFlow<Int> = _wishlistTotalCount.asStateFlow()

    private var wishlistCurrentPage = 1
    private var isWishlistLastPage = false
    private var isWishlistFetching = false

    // TỦ ĐỒ CÁ NHÂN
    fun fetchFavoriteClothes(userId: Int, isRefresh: Boolean = false) {
        if (isFetching) return
        if (isRefresh) { currentPage = 1; isLastPage = false }
        if (isLastPage) return

        viewModelScope.launch {
            isFetching = true
            if (isRefresh && _favoriteClothes.value.isEmpty()) _isLoading.value = true

            try {
                val response = clothingRepository.getFavoriteClothesByUser(userId, currentPage, limit)
                if (response.isSuccessful) {
                    val total = response.headers()["X-Total-Count"]?.toIntOrNull() ?: 0
                    if (isRefresh) _totalCount.value = total

                    val newItems = response.body() ?: emptyList()
                    if (newItems.size < limit) isLastPage = true

                    _favoriteClothes.value = if (isRefresh) newItems else _favoriteClothes.value + newItems
                    currentPage++
                }
            } catch (e: Exception) { e.printStackTrace() }
            finally { _isLoading.value = false; isFetching = false }
        }
    }

    fun loadMore(userId: Int) { fetchFavoriteClothes(userId, isRefresh = false) }

    fun removeFavorite(item: Clothing) {
        _favoriteClothes.value = _favoriteClothes.value.filter { it.clothingId != item.clothingId }
        _totalCount.value = if (_totalCount.value > 0) _totalCount.value - 1 else 0

        item.clothingId?.let { id ->
            viewModelScope.launch {
                try { clothingRepository.updateClothing(id, item.copy(isFavorite = false)) }
                catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    // WISHLIST
    fun fetchWishlistClothes(isRefresh: Boolean = false) {
        if (isWishlistFetching) return
        if (isRefresh) { wishlistCurrentPage = 1; isWishlistLastPage = false }
        if (isWishlistLastPage) return

        viewModelScope.launch {
            isWishlistFetching = true
            if (isRefresh && _wishlistClothes.value.isEmpty()) _isWishlistLoading.value = true

            try {
                val response = storeRepository.getFavoriteSystemClothes(wishlistCurrentPage, limit)
                if (response.isSuccessful) {
                    val total = response.headers()["X-Total-Count"]?.toIntOrNull() ?: 0
                    if (isRefresh) _wishlistTotalCount.value = total

                    val newItems = response.body() ?: emptyList()
                    if (newItems.size < limit) isWishlistLastPage = true

                    _wishlistClothes.value = if (isRefresh) newItems else _wishlistClothes.value + newItems
                    wishlistCurrentPage++
                }
            } catch (e: Exception) { e.printStackTrace() }
            finally { _isWishlistLoading.value = false; isWishlistFetching = false }
        }
    }

    fun loadMoreWishlist() { fetchWishlistClothes(isRefresh = false) }

    fun removeWishlistFavorite(item: SystemClothing) {
        _wishlistClothes.value = _wishlistClothes.value.filter { it.templateId != item.templateId }
        _wishlistTotalCount.value = if (_wishlistTotalCount.value > 0) _wishlistTotalCount.value - 1 else 0

        item.templateId?.let { id ->
            viewModelScope.launch {
                try { storeRepository.updateSystemClothing(id, item.copy(isFavorite = false)) }
                catch (e: Exception) { e.printStackTrace() }
            }
        }
    }
}