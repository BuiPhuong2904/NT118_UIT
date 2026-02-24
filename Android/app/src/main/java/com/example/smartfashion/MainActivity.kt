package com.example.smartfashion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.smartfashion.ui.screens.home.HomeScreen
import com.example.smartfashion.ui.theme.SmartFashionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Giúp app tràn viền đẹp hơn
        setContent {
            SmartFashionTheme {
                // Gọi màn hình Home của bạn ra đây
                HomeScreen()
            }
        }
    }
}