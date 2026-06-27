package com.example.smartfashion.ui.screens.closet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.InsightsData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _insightsData = MutableStateFlow<InsightsData?>(null)
    val insightsData: StateFlow<InsightsData?> = _insightsData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchInsights(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getUserInsights(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    _insightsData.value = response.body()?.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}