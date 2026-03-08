package com.example.smartfashion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.RetrofitInstance
import com.example.smartfashion.data.model.ForgotPasswordRequest
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {

    fun sendResetEmail(email: String) {

        viewModelScope.launch {

            try {

                val response = RetrofitInstance
                    .api
                    .forgotPassword(ForgotPasswordRequest(email))

                if (response.isSuccessful) {
                    println("SUCCESS: ${response.body()}")
                } else {
                    println("ERROR CODE: ${response.code()}")
                    println("ERROR BODY: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
