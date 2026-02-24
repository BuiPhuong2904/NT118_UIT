package com.example.smartfashion.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfashion.ui.screens.profile.ProfileScreen

// Hàm giả lập tạo màu phối (Trong thực tế sẽ dùng thuật toán HSL)
fun getHarmonies(baseColor: Color): List<Color> {
    // Giả lập trả về 4 màu phối ngẫu nhiên dựa trên màu gốc
    return listOf(
        baseColor.copy(alpha = 0.6f),
        Color(0xFF6200EE), // Tím
        Color(0xFFFFB74D), // Cam (Đối lập tím)
        Color(0xFF03DAC5)  // Teal
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPaletteScreen(
    onBackClick: () -> Unit = {}
) {
    // List màu cơ bản để chọn
    val baseColors = listOf(
        Color(0xFFF44336), // Đỏ
        Color(0xFFE91E63), // Hồng
        Color(0xFF9C27B0), // Tím
        Color(0xFF2196F3), // Xanh dương
        Color(0xFF4CAF50), // Xanh lá
        Color(0xFFFFEB3B), // Vàng
        Color(0xFFFF9800), // Cam
        Color(0xFF795548), // Nâu
        Color(0xFF000000), // Đen
        Color(0xFFFFFFFF)  // Trắng
    )

    var selectedColor by remember { mutableStateOf(baseColors[0]) }
    val harmonyColors = remember(selectedColor) { getHarmonies(selectedColor) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bánh xe màu sắc", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Màu đang chọn (Preview lớn)
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.size(120.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(selectedColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedColor == Color.White) {
                        Text("Màu chủ đạo", color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Chọn màu (Palette Picker)
            Text("Chọn màu trang phục chính:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(baseColors) { color ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (selectedColor == color) 3.dp else 1.dp,
                                color = if (selectedColor == color) Color.Black else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = color }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Kết quả phối màu (Harmonies)
            Text("Gợi ý phối màu (Harmonies):", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Màu tương phản & Bổ trợ", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                harmonyColors.forEach { color ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(color)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("#HEX", fontSize = 10.sp, color = Color.Gray)
                            Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(10.dp), tint = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Gợi ý Outfit text
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("💡 Lời khuyên Stylist:", fontWeight = FontWeight.Bold)
                    Text(
                        "Nếu bạn mặc áo màu này, hãy thử kết hợp với quần hoặc phụ kiện có các màu gợi ý bên trên để tạo độ tương phản nổi bật.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorPaletteScreenPreview() {
    ColorPaletteScreen()
}