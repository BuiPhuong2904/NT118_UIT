package com.example.smartfashion.ui.screens.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.model.ProfileResponse
import com.example.smartfashion.model.UpdateProfileRequest
import com.example.smartfashion.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _profileResponse = mutableStateOf<ProfileResponse?>(null)
    val profileResponse: State<ProfileResponse?> = _profileResponse

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun getMyProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.getMyProfile()

                if (response.isSuccessful) {
                    _profileResponse.value = response.body()
                } else {
                    _errorMessage.value = "Lỗi tải hồ sơ (${response.code()})"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Lỗi tải hồ sơ"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMyProfile(
        request: UpdateProfileRequest,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = repository.updateMyProfile(request)

                if (response.isSuccessful) {
                    getMyProfile()
                    onSuccess()
                } else {
                    val msg = "Cập nhật thất bại (${response.code()})"
                    _errorMessage.value = msg
                    onError(msg)
                }

            } catch (e: Exception) {
                val msg = e.message ?: "Cập nhật thất bại"
                _errorMessage.value = msg
                onError(msg)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
