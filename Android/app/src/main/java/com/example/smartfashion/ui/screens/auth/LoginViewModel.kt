package com.example.smartfashion.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.RetrofitInstance
import com.example.smartfashion.data.model.LoginRequest
import kotlinx.coroutines.launch


sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: State<LoginState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val response = RetrofitInstance.api.login(
                    LoginRequest(email, password)
                )

                if (response.isSuccessful) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value =
                        LoginState.Error("Sai email hoặc mật khẩu")
                }

            } catch (e: Exception) {
                _loginState.value =
                    LoginState.Error("Không kết nối được server")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
