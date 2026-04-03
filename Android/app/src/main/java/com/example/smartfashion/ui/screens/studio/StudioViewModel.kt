package com.example.smartfashion.ui.screens.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.CreateOutfitRequest
import com.example.smartfashion.data.api.OutfitItemRequest
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.data.repository.OutfitRepository
import com.example.smartfashion.model.Clothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SaveOutfitState {
    object Idle : SaveOutfitState()
    object Loading : SaveOutfitState()
    data class Success(val message: String, val outfitId: Int) : SaveOutfitState()
    data class Error(val message: String) : SaveOutfitState()
}

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository,
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    private val _userClothes = MutableStateFlow<List<Clothing>>(emptyList())
    val userClothes: StateFlow<List<Clothing>> = _userClothes.asStateFlow()

    private val _canvasItems = MutableStateFlow<List<CanvasItem>>(emptyList())
    val canvasItems: StateFlow<List<CanvasItem>> = _canvasItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State lắng nghe việc lưu phối đồ
    private val _saveState = MutableStateFlow<SaveOutfitState>(SaveOutfitState.Idle)
    val saveState: StateFlow<SaveOutfitState> = _saveState.asStateFlow()

    fun fetchUserClothes(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = clothingRepository.fetchClothesByUserId(userId, categoryId = 0, page = 1, limit = 50)
                if (response.isSuccessful) {
                    _userClothes.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Xử lý lỗi
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addItemToCanvas(clothing: Clothing) {
        val currentItems = _canvasItems.value.toMutableList()
        val newItem = CanvasItem(
            // Dùng dấu "_" để tách ID thật của quần áo ra
            id = clothing.clothingId.toString() + "_" + System.currentTimeMillis(),
            imageUrl = clothing.imageUrl,
            offsetX = 0f,
            offsetY = 0f,
            scale = 1f,
            rotation = 0f,
            type = if (clothing.categoryId == 2) "BOTTOM" else "TOP"
        )
        currentItems.add(newItem)
        _canvasItems.value = currentItems
    }

    fun updateItemTransform(id: String, newOffsetX: Float, newOffsetY: Float, newScale: Float, newRotation: Float) {
        val currentItems = _canvasItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldItem = currentItems[index]
            currentItems[index] = oldItem.copy(
                offsetX = newOffsetX,
                offsetY = newOffsetY,
                scale = newScale,
                rotation = newRotation
            )
            _canvasItems.value = currentItems
        }
    }

    fun removeItemFromCanvas(id: String) {
        val currentItems = _canvasItems.value.toMutableList()
        currentItems.removeAll { it.id == id }
        _canvasItems.value = currentItems
    }

    fun saveOutfit(userId: Int, outfitName: String, imageUrl: String = "") {
        if (_canvasItems.value.isEmpty()) {
            _saveState.value = SaveOutfitState.Error("Chưa có món đồ nào trên Canvas!")
            return
        }

        viewModelScope.launch {
            _saveState.value = SaveOutfitState.Loading
            try {
                // 1. Bóc tách tọa độ từ list đồ trên Canvas
                val itemsToSave = _canvasItems.value.mapIndexed { index, canvasItem ->
                    // Lấy ID gốc (vì lúc add mình ghép ID + "_" + Thời gian)
                    val originalClothingId = canvasItem.id.split("_")[0].toInt()

                    OutfitItemRequest(
                        clothing_id = originalClothingId,
                        position_x = canvasItem.offsetX,
                        position_y = canvasItem.offsetY,
                        scale = canvasItem.scale,
                        rotation = canvasItem.rotation,
                        z_index = index + 1
                    )
                }

                // 2. Gói vào Request
                val request = CreateOutfitRequest(
                    user_id = userId,
                    name = outfitName,
                    description = "Tạo từ phòng thử đồ SmartFashion",
                    image_preview_url = imageUrl.ifEmpty { "https://res.cloudinary.com/dna9qbejm/image/upload/v1772213478/xe-tam-ky-hoi-an-banner_bsoc2r.jpg" }, // Nếu chưa có ảnh chụp thì xài ảnh mặc định
                    items = itemsToSave
                )

                // 3. Gọi API
                val response = outfitRepository.createOutfit(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val newOutfitId = response.body()?.data?.outfitId ?: 0

                    _saveState.value = SaveOutfitState.Success("Lưu bộ phối đồ thành công!", newOutfitId)
                    _canvasItems.value = emptyList()
                } else {
                    _saveState.value = SaveOutfitState.Error("Lỗi khi lưu lên Server!")
                }
            } catch (e: Exception) {
                _saveState.value = SaveOutfitState.Error("Lỗi kết nối mạng: ${e.message}")
            }
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveOutfitState.Idle
    }
}