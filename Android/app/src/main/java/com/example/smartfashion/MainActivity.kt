package com.example.smartfashion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.example.smartfashion.ui.navigation.AppNavigation
import com.example.smartfashion.ui.theme.SmartFashionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Giúp app tràn viền đẹp hơn
        setContent {
            SmartFashionTheme {
                AppNavigation()
            }
        }
    }
}