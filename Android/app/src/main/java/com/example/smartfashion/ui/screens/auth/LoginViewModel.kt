package com.example.smartfashion.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.smartfashion.model.LoginRequest
import com.example.smartfashion.data.api.RetrofitInstance
import com.example.smartfashion.data.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository(RetrofitInstance.api)

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = authRepository.loginUser(
                    LoginRequest(email, password)
                )

                if (response.isSuccessful) {
                    val token = response.body()?.token ?: ""
                    _loginState.value = LoginState.Success(token)
                } else {
                    _loginState.value = LoginState.Error("Sai email hoặc mật khẩu")
                }

            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Không kết nối được server")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}