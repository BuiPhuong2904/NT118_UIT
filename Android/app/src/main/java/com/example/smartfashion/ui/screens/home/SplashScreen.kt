package com.example.smartfashion.ui.screens.home

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Màu Gradient cho nền (Tím đậm -> Tím nhạt)
val SplashGradient = listOf(
    Color(0xFF6200EE),
    Color(0xFF3700B3)
)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {} // Hàm callback khi chạy xong Splash
) {
    // 1. State cho Animation (Tỷ lệ phóng to)
    val scale = remember { Animatable(0f) }

    // 2. Chạy hiệu ứng khi màn hình được vẽ
    LaunchedEffect(key1 = true) {
        // Hiệu ứng nảy (Overshoot): Phóng to quá 1 chút rồi thu về 1
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(1.5f).getInterpolation(it)
                }
            )
        )
        // Giữ màn hình chờ thêm 1.5 giây để người dùng kịp nhìn Logo
        delay(1500L)

        // Chuyển sang màn hình chính
        onSplashFinished()
    }

    // 3. Giao diện
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(SplashGradient) // Nền Gradient sang trọng
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo App (Có Animation scale)
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale.value), // Áp dụng animation vào đây
                shadowElevation = 8.dp
            ) {
                Icon(
                    imageVector = Icons.Rounded.Checkroom, // Biểu tượng cái móc áo
                    contentDescription = "Logo",
                    tint = Color(0xFF6200EE),
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tên App
            Text(
                text = "SmartFashion",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.scale(scale.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Slogan
            Text(
                text = "Tủ đồ thông minh & AI Stylist",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.scale(scale.value)
            )
        }

        // Footer (Phiên bản / Copyright)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Powered by AI Technology",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
                Text(
                    text = "v1.0.0",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun SplashPreview() {
    SplashScreen()
}