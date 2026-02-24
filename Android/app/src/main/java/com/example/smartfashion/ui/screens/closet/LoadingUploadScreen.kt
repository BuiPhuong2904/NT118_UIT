package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LoadingUploadScreen(onFinished: () -> Unit) {
    // State để chạy phần trăm từ 0 đến 100
    var progress by remember { mutableIntStateOf(0) }

    // Giả lập tiến trình tăng dần
    LaunchedEffect(Unit) {
        while (progress < 100) {
            delay(20) // Tốc độ chạy (20ms mỗi 1%)
            progress += 1
        }
        delay(500) // Đợi một chút khi đạt 100% cho mượt
        onFinished() // Chuyển sang màn hình Edit đồ
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Nút X thoát ở góc trên trái (Giống ảnh mẫu)
        IconButton(
            onClick = { /* Hủy quá trình */ },
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(Icons.Rounded.Close, contentDescription = null, modifier = Modifier.size(28.dp))
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Biểu tượng chiếc áo trong khung xanh (Giống ảnh mẫu)
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2196F3))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Các góc xanh (Corner borders) giả lập khung quét
                    Icon(
                        imageVector = Icons.Rounded.Checkroom,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Dòng chữ hiển thị % (Uploading items: 100% complete)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Uploading items: ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "$progress% complete",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dòng chữ phụ
            Text(
                text = "Please stay on this page",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingUploadPreview() {
    LoadingUploadScreen(onFinished = {})
}