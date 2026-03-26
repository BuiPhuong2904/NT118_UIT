package com.example.smartfashion.ui.screens.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// THÊM DÒNG NÀY ĐỂ GỌI LOGIN STATE HÀNG THẬT
import com.example.smartfashion.model.LoginState

import com.example.smartfashion.model.LoginRequest
import com.example.smartfashion.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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
                    val body = response.body()
                    val token = body?.token ?: ""

                    val returnedUserId = body?.user?.userId ?: -1
                    val returnedUsername = body?.user?.username ?: ""

                    _loginState.value = LoginState.Success(token, returnedUserId, returnedUsername)
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