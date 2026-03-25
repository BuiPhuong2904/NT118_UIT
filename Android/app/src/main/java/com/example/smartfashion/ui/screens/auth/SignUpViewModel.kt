package com.example.smartfashion.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.AuthRepository
import com.example.smartfashion.model.RegisterRequest
import com.example.smartfashion.model.RegisterState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                val response = authRepository.registerUser(
                    RegisterRequest(
                        username = username,
                        email = email,
                        password = password
                    )
                )

                if (response.isSuccessful) {
                    val token = response.body()?.token ?: ""
                    _registerState.value = RegisterState.Success(token)
                } else {
                    _registerState.value = RegisterState.Error("Đăng ký thất bại: Email đã tồn tại")
                }

            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}