package com.example.smartfashion.ui.screens.closet

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ProgressRequestBody
import com.example.smartfashion.data.repository.ClothingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val clothingRepository: ClothingRepository
) : ViewModel() {

    // Biến lưu link ảnh ĐÃ XÓA NỀN
    private val _uploadedUrl = MutableStateFlow<String?>(null)
    val uploadedUrl: StateFlow<String?> = _uploadedUrl.asStateFlow()

    // Biến lưu link ẢNH GỐC
    private val _originalUrl = MutableStateFlow<String?>(null)
    val originalUrl: StateFlow<String?> = _originalUrl.asStateFlow()

    private val _imageId = MutableStateFlow<Int?>(null)
    val imageId: StateFlow<Int?> = _imageId.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    // Biến lưu phần trăm upload thật
    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    fun uploadImageToAI(context: Context, uri: Uri, userId: Int) {
        viewModelScope.launch {
            try {
                // Reset tiến trình và kết quả về 0/null khi bắt đầu upload file mới
                _uploadProgress.value = 0f
                _uploadedUrl.value = null
                _originalUrl.value = null
                _imageId.value = null

                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "upload_temp.jpg")
                tempFile.outputStream().use { output ->
                    inputStream?.copyTo(output)
                }

                val requestFile = ProgressRequestBody(tempFile, "image/jpeg".toMediaTypeOrNull()) { progress ->
                    _uploadProgress.value = progress.toFloat()
                }

                val body = MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
                val userIdBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val response = clothingRepository.uploadImage(body, userIdBody)

                if (response.isSuccessful && response.body()?.success == true) {
                    val noBgUrl = response.body()?.data?.url_no_bg
                    val originalImgUrl = response.body()?.data?.url_original
                    val returnedImageId = response.body()?.data?.image_id

                    _uploadedUrl.value = noBgUrl
                    _originalUrl.value = originalImgUrl
                    _imageId.value = returnedImageId
                } else {
                    _isError.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isError.value = true
            }
        }
    }
}