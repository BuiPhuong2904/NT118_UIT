package com.example.smartfashion.ui.screens.studio

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.CreateOutfitRequest
import com.example.smartfashion.data.api.OutfitItemRequest
import com.example.smartfashion.data.repository.CategoryRepository
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.data.repository.OutfitRepository
import com.example.smartfashion.model.Category
import com.example.smartfashion.model.Clothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
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
    private val outfitRepository: OutfitRepository,
    private val categoryRepository: CategoryRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _userClothes = MutableStateFlow<List<Clothing>>(emptyList())
    val userClothes: StateFlow<List<Clothing>> = _userClothes.asStateFlow()

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(0)
    val selectedCategoryId: StateFlow<Int> = _selectedCategoryId.asStateFlow()

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = categoryRepository.fetchCategories()
                if (response.isSuccessful) {
                    val apiCategories = response.body() ?: emptyList()
                    val allCategory = Category(categoryId = 0, name = "Tất cả")
                    _categoryList.value = listOf(allCategory) + apiCategories
                }
            } catch (_: Exception) { }
        }
    }

    fun onCategorySelected(categoryId: Int, userId: Int) {
        _selectedCategoryId.value = categoryId
        fetchUserClothes(userId, categoryId)
    }

    fun fetchUserClothes(userId: Int, categoryId: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = clothingRepository.fetchClothesByUserId(userId, categoryId = categoryId, page = 1, limit = 50)
                if (response.isSuccessful) {
                    _userClothes.value = response.body() ?: emptyList()
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _canvasItems = MutableStateFlow<List<CanvasItem>>(emptyList())
    val canvasItems: StateFlow<List<CanvasItem>> = _canvasItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveState = MutableStateFlow<SaveOutfitState>(SaveOutfitState.Idle)
    val saveState: StateFlow<SaveOutfitState> = _saveState.asStateFlow()

    fun addItemToCanvas(clothing: Clothing) {
        val currentItems = _canvasItems.value.toMutableList()
        val newItem = CanvasItem(
            id = clothing.clothingId.toString() + "_" + System.currentTimeMillis(),
            imageUrl = clothing.imageUrl,
            offsetX = 0f, offsetY = 0f, scale = 1f, rotation = 0f,
            type = ItemType.IMAGE
        )
        currentItems.add(newItem)
        _canvasItems.value = currentItems
    }

    fun updateItemTransform(id: String, newOffsetX: Float, newOffsetY: Float, newScale: Float, newRotation: Float) {
        val currentItems = _canvasItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldItem = currentItems[index]
            currentItems[index] = oldItem.copy(offsetX = newOffsetX, offsetY = newOffsetY, scale = newScale, rotation = newRotation)
            _canvasItems.value = currentItems
        }
    }

    fun removeItemFromCanvas(id: String) {
        val currentItems = _canvasItems.value.toMutableList()
        currentItems.removeAll { it.id == id }
        _canvasItems.value = currentItems
    }

    fun saveOutfitWithImage(userId: Int, outfitName: String, bitmap: Bitmap, context: Context) {
        if (_canvasItems.value.isEmpty()) {
            _saveState.value = SaveOutfitState.Error("Chưa có món đồ nào trên Canvas!")
            return
        }

        viewModelScope.launch {
            _saveState.value = SaveOutfitState.Loading
            try {
                // 1. Chuyển Bitmap thành File tạm trong cache để upload
                val file = File(context.cacheDir, "outfit_preview_${System.currentTimeMillis()}.png")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // 2. Tạo RequestBody cho file ảnh và userId
                val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val userIdPart = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                // 3. Gọi API upload ảnh lên Cloudinary
                val uploadResponse = apiService.uploadImage(imagePart, userIdPart)

                if (uploadResponse.isSuccessful && uploadResponse.body()?.success == true) {
                    val uploadedUrl = uploadResponse.body()?.data?.url_original ?: ""

                    if (file.exists()) file.delete()

                    // 4. Nếu upload thành công, gọi hàm lưu bộ phối đồ với link ảnh thật
                    saveOutfit(userId, outfitName, uploadedUrl)
                } else {
                    _saveState.value = SaveOutfitState.Error("Lỗi upload ảnh nền bộ phối!")
                    if (file.exists()) file.delete()
                }

            } catch (e: Exception) {
                _saveState.value = SaveOutfitState.Error("Lỗi hệ thống: ${e.message}")
            }
        }
    }

    fun saveOutfit(userId: Int, outfitName: String, imageUrl: String = "") {
        viewModelScope.launch {
            try {
                val imageItems = _canvasItems.value.filter { it.type == ItemType.IMAGE }

                if (imageItems.isEmpty()) {
                    _saveState.value = SaveOutfitState.Error("Bạn phải chọn ít nhất 1 món quần áo để lưu!")
                    return@launch
                }

                val itemsToSave = imageItems.mapIndexed { index, canvasItem ->
                    val clothingId = canvasItem.id.split("_")[0].toInt()

                    OutfitItemRequest(
                        clothing_id = clothingId,
                        position_x = canvasItem.offsetX,
                        position_y = canvasItem.offsetY,
                        scale = canvasItem.scale,
                        rotation = canvasItem.rotation,
                        z_index = index + 1
                    )
                }

                val finalImageUrl = imageUrl.ifEmpty { "https://res.cloudinary.com/dna9qbejm/image/upload/v1772213478/xe-tam-ky-hoi-an-banner_bsoc2r.jpg" }

                val request = CreateOutfitRequest(
                    user_id = userId,
                    name = outfitName,
                    description = "Tạo từ phòng thử đồ SmartFashion",
                    image_preview_url = finalImageUrl,
                    items = itemsToSave
                )

                val response = outfitRepository.createOutfit(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val newOutfitId = response.body()?.data?.outfitId ?: 0
                    _saveState.value = SaveOutfitState.Success("Lưu bộ phối đồ thành công!", newOutfitId)
                    _canvasItems.value = emptyList()
                } else {
                    _saveState.value = SaveOutfitState.Error("Lỗi từ Server: Code ${response.code()}")
                }
            } catch (e: Exception) {
                _saveState.value = SaveOutfitState.Error("Lỗi hệ thống: ${e.message}")
            }
        }
    }

    fun resetSaveState() { _saveState.value = SaveOutfitState.Idle }

    fun addTextItem(textContent: String, color: Color = Color.Black) {
        val newItem = CanvasItem(
            id = UUID.randomUUID().toString(),
            type = ItemType.TEXT,
            text = textContent,
            textColor = color,
            offsetX = 200f,
            offsetY = 300f
        )
        _canvasItems.value += newItem
    }

    fun updateTextItem(id: String, newText: String, newColor: Color) {
        val currentItems = _canvasItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.id == id }
        if (index != -1) {
            val oldItem = currentItems[index]
            currentItems[index] = oldItem.copy(text = newText, textColor = newColor)
            _canvasItems.value = currentItems
        }
    }
}