package com.example.smartfashion.ui.screens.closet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.AddToWishlistRequest
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

    private val _wishlistId = MutableStateFlow<Int?>(null)
    val wishlistId: StateFlow<Int?> = _wishlistId.asStateFlow()

    fun fetchSystemClothingDetail(templateId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                val response = storeRepository.getSystemClothingById(templateId)
                if (response.isSuccessful) {
                    _systemItem.value = response.body()
                } else {
                    println("Lỗi API chi tiết: ${response.code()}")
                }

                checkFavoriteStatus(templateId, userId)

            } catch (e: Exception) {
                println("Lỗi mạng: ${e.message}")
            }
        }
    }

    private suspend fun checkFavoriteStatus(templateId: Int, userId: Int) {
        try {
            val response = storeRepository.getUserWishlist(userId, 1, 200, null)
            if (response.isSuccessful) {
                val wishlists = response.body()?.data ?: emptyList()
                val foundWishlistItem = wishlists.find { it.templateId == templateId }
                _wishlistId.value = foundWishlistItem?.wishlistId
            }
        } catch (e: Exception) {
            println("Lỗi kiểm tra Wishlist: ${e.message}")
        }
    }

    fun toggleWishlist(userId: Int) {
        val item = _systemItem.value ?: return
        val templateId = item.templateId ?: return
        val currentWishlistId = _wishlistId.value

        viewModelScope.launch {
            try {
                if (currentWishlistId != null) {
                    val previousWishlistId = currentWishlistId
                    _wishlistId.value = null

                    val response = storeRepository.removeFromWishlist(previousWishlistId, userId)
                    if (!response.isSuccessful) {
                        _wishlistId.value = previousWishlistId
                    }
                } else {
                    _wishlistId.value = -1

                    val request = AddToWishlistRequest(
                        user_id = userId,
                        template_id = templateId,
                        item_name = item.name,
                        image_url = item.imageUrl
                    )

                    val response = storeRepository.addToWishlist(request)
                    if (response.isSuccessful) {
                        _wishlistId.value = response.body()?.wishlistId
                    } else {
                        _wishlistId.value = null
                    }
                }
            } catch (e: Exception) {
                if (currentWishlistId != null) {
                    _wishlistId.value = currentWishlistId
                } else {
                    _wishlistId.value = null
                }
                println("Lỗi thao tác Wishlist: ${e.message}")
            }
        }
    }
}