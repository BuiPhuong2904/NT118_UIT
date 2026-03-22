package com.example.smartfashion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.ui.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import com.example.smartfashion.ui.theme.SmartFashionTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Giúp app tràn viền đẹp hơn

        val tokenManager = TokenManager(this)
        val token = tokenManager.getToken()

        setContent {
            SmartFashionTheme {
                AppNavigation(
                     startDestination ="splash_screen"
                )
            }
        }
    }
}