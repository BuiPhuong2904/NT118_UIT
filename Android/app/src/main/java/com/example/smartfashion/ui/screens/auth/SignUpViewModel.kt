package com.example.smartfashion.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.smartfashion.data.api.RetrofitInstance
import com.example.smartfashion.data.model.RegisterRequest


class SignUpViewModel : ViewModel() {

    private val _registerState =
        MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> =
        _registerState

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            try {
                val response = RetrofitInstance.api.register(
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
                    _registerState.value =
                        RegisterState.Error("Email đã tồn tại")
                }

            } catch (e: Exception) {
                _registerState.value =
                    RegisterState.Error("Không kết nối được server")
            }
        }
    }
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}
