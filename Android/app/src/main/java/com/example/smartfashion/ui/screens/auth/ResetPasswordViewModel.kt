package com.example.smartfashion.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smartfashion.data.api.RetrofitInstance
import com.example.smartfashion.data.repository.AuthRepository
import com.example.smartfashion.model.ResetPasswordRequest
import kotlinx.coroutines.launch

sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object Loading : ResetPasswordState()
    object Success : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}

class ResetPasswordViewModel : ViewModel() {
    private val authRepository = AuthRepository(RetrofitInstance.api)

    private val _state = mutableStateOf<ResetPasswordState>(ResetPasswordState.Idle)
    val state: State<ResetPasswordState> = _state

    fun resetPassword(email: String, otp: String, newPassword: String) {
        viewModelScope.launch {
            _state.value = ResetPasswordState.Loading

            try {
                val response = authRepository.resetPassword(
                    ResetPasswordRequest(
                        email = email,
                        otp = otp,
                        newPassword = newPassword
                    )
                )

                if (response.isSuccessful) {
                    _state.value = ResetPasswordState.Success
                } else {
                    _state.value = ResetPasswordState.Error("Mã OTP không hợp lệ hoặc đã hết hạn")
                }

            } catch (e: Exception) {
                _state.value = ResetPasswordState.Error("Không kết nối được server")
                e.printStackTrace()
            }
        }
    }

    fun resetState() {
        _state.value = ResetPasswordState.Idle
    }
}