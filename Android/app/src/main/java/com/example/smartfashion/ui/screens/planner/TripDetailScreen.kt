package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo
val TripDetailPrimary = Color(0xFF6200EE)

// Model cho Checklist
data class PackingItem(
    val id: String,
    val name: String,
    var isChecked: Boolean = false,
    val linkedOutfitUrl: String? = null // Có thể link tới ảnh bộ đồ đã phối
)

data class PackingCategory(
    val title: String,
    val items: List<PackingItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    onBackClick: () -> Unit = {}
) {
    // Dữ liệu giả lập
    val categories = remember {
        mutableStateListOf(
            PackingCategory("Trang phục (Outfits)", listOf(
                PackingItem("1", "Set đi biển ngày 1", false, "https://i.postimg.cc/9MXZHYtp/3.jpg"),
                PackingItem("2", "Váy dạ tiệc tối", false, "https://i.postimg.cc/9MXZHYtp/3.jpg"),
                PackingItem("3", "Đồ ngủ & Nội y", true)
            )),
            PackingCategory("Vệ sinh cá nhân", listOf(
                PackingItem("4", "Kem chống nắng", false),
                PackingItem("5", "Bàn chải & Kem đánh răng", true),
                PackingItem("6", "Skincare kit", false)
            )),
            PackingCategory("Giấy tờ & Công nghệ", listOf(
                PackingItem("7", "CCCD / Hộ chiếu", true),
                PackingItem("8", "Sạc dự phòng", false)
            ))
        )
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        // Không dùng TopBar chuẩn để làm Header tràn viền đẹp hơn
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Thêm món đồ mới */ },
                containerColor = TripDetailPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Thêm đồ") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // 1. HEADER TRÀN VIỀN (Ảnh địa điểm + Thông tin)
            item {
                TripHeader(onBackClick)
            }

            // 2. THỜI TIẾT (Weather Widget)
            item {
                WeatherWidget()
            }

            // 3. TIẾN ĐỘ CHUNG
            item {
                OverallProgress(categories)
            }

            // 4. DANH SÁCH CHECKLIST THEO DANH MỤC
            items(categories) { category ->
                CategorySection(category)
            }

            // Padding dưới cùng
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun TripHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        // Ảnh nền
        AsyncImage(
            model = "https://i.postimg.cc/9MXZHYtp/3.jpg", // Ảnh Đà Nẵng
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient tối dần ở dưới để hiện chữ
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(0.3f),
                            Color.Transparent,
                            Color.Black.copy(0.8f)
                        )
                    )
                )
        )

        // Nút Back
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(0.3f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        // Nút Edit
        IconButton(
            onClick = { },
            modifier = Modifier
                .padding(top = 40.dp, end = 16.dp)
                .align(Alignment.TopEnd)
                .background(Color.Black.copy(0.3f), CircleShape)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
        }

        // Thông tin chuyến đi
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Surface(
                color = TripDetailPrimary,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    "Sắp diễn ra",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Text(
                "Đà Nẵng & Hội An",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "15/04 - 18/04 • 4 Ngày 3 Đêm",
                color = Color.White.copy(0.9f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun WeatherWidget() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Dự báo thời tiết", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDayItem("T5", "15/04", Icons.Default.WbSunny, "28°C")
                WeatherDayItem("T6", "16/04", Icons.Default.Cloud, "26°C")
                WeatherDayItem("T7", "17/04", Icons.Default.WbSunny, "29°C")
                WeatherDayItem("CN", "18/04", Icons.Default.WbSunny, "30°C")
            }
        }
    }
}

@Composable
fun WeatherDayItem(day: String, date: String, icon: ImageVector, temp: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(day, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(date, fontSize = 10.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Icon(icon, null, tint = Color(0xFFFFB300), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(temp, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun OverallProgress(categories: List<PackingCategory>) {
    val totalItems = categories.sumOf { it.items.size }
    val packedItems = categories.sumOf { it.items.count { item -> item.isChecked } }
    val progress = if (totalItems > 0) packedItems.toFloat() / totalItems else 0f

    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Đã soạn xong", fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("$packedItems/$totalItems món", fontWeight = FontWeight.Bold, color = TripDetailPrimary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = TripDetailPrimary,
            trackColor = Color(0xFFE0E0E0)
        )
    }
}

@Composable
fun CategorySection(category: PackingCategory) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = category.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column {
                category.items.forEachIndexed { index, item ->
                    ChecklistItemView(item)
                    if (index < category.items.size - 1) {
                        HorizontalDivider(color = Color.LightGray.copy(0.2f), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistItemView(item: PackingItem) {
    // State cục bộ để checkbox hoạt động được trong demo
    var isChecked by remember { mutableStateOf(item.isChecked) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isChecked = !isChecked }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox Custom
        Icon(
            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isChecked) Color(0xFF4CAF50) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Nội dung
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 14.sp,
                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None,
                color = if (isChecked) Color.Gray else Color.Black
            )
        }

        // Nếu món đồ có liên kết với Outfit (Ảnh nhỏ)
        if (item.linkedOutfitUrl != null) {
            Card(
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.size(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
            ) {
                AsyncImage(
                    model = item.linkedOutfitUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripDetailPreview() {
    TripDetailScreen()
}