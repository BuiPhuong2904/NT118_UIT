package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo
val SelectionPrimary = Color(0xFF6200EE)

// Model giả lập Outfit để chọn
data class SelectableOutfit(
    val id: String,
    val name: String,
    val imageUrl: String,
    val itemsCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitSelectionScreen(
    onBackClick: () -> Unit = {},
    onConfirmClick: (List<String>) -> Unit = {} // Trả về danh sách ID các bộ đã chọn
) {
    // Dữ liệu giả lấy từ Studio
    val savedOutfits = remember {
        listOf(
            SelectableOutfit("1", "Set đi biển sáng", "https://i.postimg.cc/9MXZHYtp/3.jpg", 4),
            SelectableOutfit("2", "Tiệc tối sang trọng", "https://i.postimg.cc/9MXZHYtp/3.jpg", 3),
            SelectableOutfit("3", "Dạo phố thoải mái", "https://i.postimg.cc/9MXZHYtp/3.jpg", 2),
            SelectableOutfit("4", "Đi bar buổi tối", "https://i.postimg.cc/9MXZHYtp/3.jpg", 5),
            SelectableOutfit("5", "Ngủ (Pajamas)", "https://i.postimg.cc/9MXZHYtp/3.jpg", 2),
            SelectableOutfit("6", "Thể thao sáng sớm", "https://i.postimg.cc/9MXZHYtp/3.jpg", 3),
        )
    }

    // State lưu các bộ đang được tích chọn
    val selectedIds = remember { mutableStateListOf<String>() }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Chọn Outfit", fontWeight = FontWeight.Bold)
                        Text("Đã chọn ${selectedIds.size} bộ", fontSize = 12.sp, color = SelectionPrimary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Nút xác nhận to ở dưới
            Button(
                onClick = { onConfirmClick(selectedIds.toList()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SelectionPrimary),
                enabled = selectedIds.isNotEmpty() // Chỉ bấm được khi đã chọn ít nhất 1 bộ
            ) {
                Text("Thêm vào vali (${selectedIds.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Thanh tìm kiếm nhanh (Optional)
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Tìm theo tên (ví dụ: Biển, Tiệc...)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray.copy(0.5f),
                    focusedBorderColor = SelectionPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Grid hiển thị
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedOutfits) { outfit ->
                    val isSelected = selectedIds.contains(outfit.id)
                    SelectableOutfitCard(
                        outfit = outfit,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) selectedIds.remove(outfit.id)
                            else selectedIds.add(outfit.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableOutfitCard(
    outfit: SelectableOutfit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) SelectionPrimary else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box {
            // Ảnh Outfit
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f) // Ảnh vuông
                        .background(Color(0xFFF5F5F5))
                ) {
                    AsyncImage(
                        model = outfit.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Lớp phủ mờ khi được chọn
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SelectionPrimary.copy(alpha = 0.2f))
                        )
                    }
                }

                // Thông tin tên
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = outfit.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "${outfit.itemsCount} món đồ",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Checkbox Icon ở góc trên phải
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .background(
                        color = if (isSelected) SelectionPrimary else Color.White.copy(0.8f),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) SelectionPrimary else Color.Gray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun OutfitSelectionPreview() {
    OutfitSelectionScreen()
}