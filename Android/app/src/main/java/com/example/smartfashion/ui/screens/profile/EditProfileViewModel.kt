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

    fun updateMyProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _updateSuccess.value = false

            try {
                val response = repository.updateMyProfile(request)

                if (response.isSuccessful) {
                    _updateSuccess.value = true
                    getMyProfile()
                } else {
                    _errorMessage.value = "Cập nhật hồ sơ thất bại (${response.code()})"
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Cập nhật hồ sơ thất bại"
                _updateSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetUpdateState() {
        _updateSuccess.value = false
    }
}
