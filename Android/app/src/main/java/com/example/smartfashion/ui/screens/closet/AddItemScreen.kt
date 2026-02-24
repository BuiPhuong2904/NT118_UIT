package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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

// Sử dụng màu đã định nghĩa ở HomeScreen hoặc định nghĩa tên khác hẳn để tránh lỗi trùng lặp
val AddItemScreenPrimary = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen() {
    val scrollState = rememberScrollState()

    // State quản lý thông tin nhập liệu
    var selectedCategory by remember { mutableStateOf("Skirts > Midi Skirt") }
    var selectedColor by remember { mutableStateOf(Color.Black) }
    var selectedSeason by remember { mutableStateOf(setOf("Spring", "Summer", "Fall", "Winter")) }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { }) { Icon(Icons.Rounded.ArrowBackIosNew, null, modifier = Modifier.size(20.dp)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Nút Save cố định ở dưới cùng giống ảnh mẫu
            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Text("Save", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // 1. KHU VỰC HIỂN THỊ ẢNH ĐÃ TÁCH NỀN (Giống ảnh mẫu 2)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Giả lập ảnh váy đã tách nền
                Icon(
                    imageVector = Icons.Rounded.Checkroom,
                    contentDescription = null,
                    modifier = Modifier.size(150.dp),
                    tint = Color(0xFF1A1A1A)
                )

                // Nút "Original image OFF" và "Edit"
                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.LightGray.copy(0.5f)) {
                        Text("Original image OFF", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp)
                    }
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.LightGray.copy(0.5f)) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(14.dp))
                            Text(" Edit", fontSize = 12.sp)
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 8.dp)

            // 2. PHẦN CHỌN MÙA (Season)
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Season", fontWeight = FontWeight.SemiBold)
                    Text(selectedSeason.joinToString(", "), color = AddItemScreenPrimary, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val seasons = listOf("Spring", "Summer", "Fall", "Winter")
                    items(seasons) { season ->
                        val isSelected = selectedSeason.contains(season)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedSeason = if (isSelected) selectedSeason - season else selectedSeason + season
                            },
                            label = { Text(season) },
                            shape = RoundedCornerShape(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF5F5F5))

            // 3. DANH SÁCH THÔNG TIN CHI TIẾT (Dropdown logic)
            InfoRow("Occasion", "Daily, Work, Date, Formal")
            InfoRow("Category", selectedCategory, textColor = AddItemScreenPrimary)

            // Dòng chọn màu sắc
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Color", fontWeight = FontWeight.SemiBold)
                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color.Black).border(1.dp, Color.LightGray, CircleShape))
            }

            InfoRow("Brand", "Choose the brand", isPlaceholder = true)
            InfoRow("Material", "Other Materials", textColor = AddItemScreenPrimary)
            InfoRow("Pattern", "Solid", textColor = AddItemScreenPrimary)
            InfoRow("Size", "Enter the size", isPlaceholder = true)

            Spacer(modifier = Modifier.height(100.dp)) // Chừa chỗ cho nút Save
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, textColor: Color = Color.Gray, isPlaceholder: Boolean = false) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(
                text = value,
                color = if (isPlaceholder) Color.LightGray else textColor,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(Icons.Rounded.KeyboardArrowDown, null, tint = Color.LightGray)
        }
        HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemScreenPreview() {
    AddItemScreen()
}