package com.example.smartfashion.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.model.ProfileResponse
import com.example.smartfashion.model.UpdateProfileRequest
import com.example.smartfashion.repository.ProfileRepository
import com.example.smartfashion.ui.screens.profile.FileUtil.toPart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profileResponse = mutableStateOf<ProfileResponse?>(null)
    val profileResponse: State<ProfileResponse?> = _profileResponse

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    private val _updateSuccess = mutableStateOf(false)
    val updateSuccess: State<Boolean> = _updateSuccess

    /**
     * Tải thông tin hồ sơ hiện tại từ Server
     */
    fun getMyProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.getMyProfile()

                if (response.isSuccessful) {
                    _profileResponse.value = response.body()
                } else {
                    _errorMessage.value = "Không thể tải thông tin hồ sơ (${response.code()})"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Không thể tải thông tin hồ sơ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Cập nhật hồ sơ người dùng
     */
    fun updateMyProfile(request: UpdateProfileRequest, imageUri: Uri?, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false

            try {
                // 1. Chuẩn bị Part ảnh nếu có chọn ảnh mới
                val imagePart = imageUri?.let { uri ->
                    FileUtil.prepareImagePart(context, uri, "avatar")
                }

                // 2. Gọi Repository cập nhật
                val response = repository.updateMyProfile(
                    username = request.username?.toPart(),
                    phone_number = request.phone_number?.toPart(),
                    gender = request.gender?.toPart(),
                    height = request.height?.toString()?.toPart(),
                    weight = request.weight?.toString()?.toPart(),
                    body_shape = request.body_shape?.toPart(),
                    skin_tone = request.skin_tone?.toPart(),
                    style_favourite = request.style_favourite?.toPart(),
                    colors_favourite = request.colors_favourite?.toPart(),
                    avatar = imagePart
                )

                if (response.isSuccessful) {
                    // Cập nhật lại data local từ response trả về để UI đồng bộ ngay lập tức
                    _profileResponse.value = response.body()
                    _updateSuccess.value = true
                } else {
                    _errorMessage.value = "Cập nhật thất bại: ${response.code()}"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Đã có lỗi xảy ra"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetUpdateState() {
        _updateSuccess.value = false
    }

    // Extension function hỗ trợ chuyển đổi dữ liệu sang RequestBody
    private fun String.toPart(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }
}