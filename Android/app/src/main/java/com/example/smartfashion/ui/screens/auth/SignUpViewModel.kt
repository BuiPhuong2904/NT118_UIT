package com.example.smartfashion.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smartfashion.data.repository.AuthRepository
import com.example.smartfashion.model.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val token: String) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = mutableStateOf<RegisterState>(RegisterState.Idle)
    val registerState: State<RegisterState> = _registerState

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
                    _registerState.value = RegisterState.Error("Email đã tồn tại")
                }

            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Không kết nối được server")
            }
        }
    }

    fun resetState() {
        _registerState.value = RegisterState.Idle
    }
}