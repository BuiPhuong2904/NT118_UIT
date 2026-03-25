package com.example.smartfashion.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smartfashion.data.api.RetrofitInstance
import com.example.smartfashion.data.repository.AuthRepository
import com.example.smartfashion.model.ForgotPasswordRequest
import kotlinx.coroutines.launch

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    object Success : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}

class ForgotPasswordViewModel : ViewModel() {
    private val authRepository = AuthRepository(RetrofitInstance.api)

    private val _state = mutableStateOf<ForgotPasswordState>(ForgotPasswordState.Idle)
    val state: State<ForgotPasswordState> = _state

    fun sendResetEmail(email: String) {
        viewModelScope.launch {
            _state.value = ForgotPasswordState.Loading

            try {
                val response = authRepository.forgotPassword(
                    ForgotPasswordRequest(email)
                )

                if (response.isSuccessful) {
                    _state.value = ForgotPasswordState.Success
                } else {
                    _state.value = ForgotPasswordState.Error("Email không tồn tại trong hệ thống")
                }

            } catch (e: Exception) {
                _state.value = ForgotPasswordState.Error("Không kết nối được server")
            }
        }
    }

    fun resetState() {
        _state.value = ForgotPasswordState.Idle
    }
}