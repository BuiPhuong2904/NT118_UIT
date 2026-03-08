package com.example.smartfashion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.RetrofitInstance
import com.example.smartfashion.data.model.ResetPasswordRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    private val _resetState = MutableStateFlow<String?>(null)
    val resetState: StateFlow<String?> = _resetState

    fun resetPassword(token: String, newPassword: String) {

        viewModelScope.launch {

            try {

                val response = RetrofitInstance
                    .api
                    .resetPassword(
                        ResetPasswordRequest(
                            token = token,
                            newPassword = newPassword
                        )
                    )

                if (response.isSuccessful) {
                    _resetState.value = "success"
                } else {
                    _resetState.value = "failed"
                }

            } catch (e: Exception) {
                _resetState.value = "error"
                e.printStackTrace()
            }
        }
    }
}
