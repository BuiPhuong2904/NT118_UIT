package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo
val CalendarPrimary = Color(0xFF6200EE)
val CalendarToday = Color(0xFF1A1A1A)

@Composable
fun CalendarScreen() {
    // State giả lập ngày đang chọn
    var selectedDate by remember { mutableIntStateOf(15) }
    // Giả lập dữ liệu
    val plannedDays = listOf(5, 12, 15, 20, 24)

    Scaffold(
        containerColor = Color(0xFFFDF7FF), // Màu nền đồng bộ

        // --- BOTTOM BAR (Tab Lịch) ---
        bottomBar = { CalendarBottomBar() },

        // --- FAB ---
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Mở Studio hoặc Closet */ },
                containerColor = CalendarPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Outfit")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Padding chuẩn 20dp
        ) {
            // 1. HEADER (Đồng bộ)
            CalendarHeader()

            Spacer(modifier = Modifier.height(10.dp))

            // 2. CHỌN THÁNG
            MonthSelector()

            Spacer(modifier = Modifier.height(10.dp))

            // 3. LƯỚI LỊCH
            CalendarGrid(
                selectedDate = selectedDate,
                plannedDays = plannedDays,
                onDateSelected = { selectedDate = it }
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = Color.LightGray.copy(alpha = 0.3f),
                thickness = 1.dp // Mảnh lại chút cho tinh tế
            )

            // 4. DANH SÁCH OUTFIT
            OutfitScheduleList(selectedDate)
        }
    }
}

// --- HEADER MỚI (ĐỒNG BỘ) ---
@Composable
fun CalendarHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Lịch trình",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = CalendarPrimary
            )
            Text(
                text = "Kế hoạch mặc đẹp mỗi ngày",
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = CalendarPrimary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Settings, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun MonthSelector() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(8.dp), // Bọc trong khung trắng cho nổi bật
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = Color.Gray)
        }
        Text(
            text = "Tháng 2, 2026", // Tiếng Việt
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = {}) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = Color.Gray)
        }
    }
}

@Composable
fun CalendarGrid(
    selectedDate: Int,
    plannedDays: List<Int>,
    onDateSelected: (Int) -> Unit
) {
    Column {
        // Hàng thứ
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(260.dp)
        ) {
            items(2) { Spacer(modifier = Modifier) } // Offset ngày đầu tháng

            items(28) { index ->
                val day = index + 1
                val isSelected = day == selectedDate
                val hasPlan = plannedDays.contains(day)
                val isToday = day == 12

                DayCell(
                    day = day,
                    isSelected = isSelected,
                    isToday = isToday,
                    hasPlan = hasPlan,
                    onClick = { onDateSelected(day) }
                )
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasPlan: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(if (isSelected) CalendarToday else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isToday && !isSelected) CalendarToday else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
        )

        if (hasPlan) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else CalendarPrimary)
            )
        }
    }
}

@Composable
fun OutfitScheduleList(date: Int) {
    Column {
        Text(
            text = "Lịch trình ngày $date/02",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutfitCard(
                    time = "08:00",
                    title = "Đi làm văn phòng",
                    tags = listOf("Lịch sự", "Thoải mái"),
                    imageUrl = "https://i.postimg.cc/9MXZHYtp/3.jpg"
                )
            }
            item {
                OutfitCard(
                    time = "19:00",
                    title = "Hẹn hò tối",
                    tags = listOf("Sang trọng", "Mát mẻ"),
                    imageUrl = "https://i.postimg.cc/9MXZHYtp/3.jpg"
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun OutfitCard(
    time: String,
    title: String,
    tags: List<String>,
    imageUrl: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.White, RoundedCornerShape(16.dp)) // Nền trắng cho nổi trên nền xám nhẹ
            .padding(8.dp)
    ) {
        // Cột giờ
        Column(
            modifier = Modifier
                .width(60.dp)
                .fillMaxHeight()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(time, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CalendarPrimary)
        }

        // Ảnh Outfit
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .width(80.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        // Thông tin
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, top = 4.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                tags.forEach { tag ->
                    Surface(
                        color = CalendarPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = tag,
                            color = CalendarPrimary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- BOTTOM BAR (Index 3 - Lịch) ---
@Composable
fun CalendarBottomBar() {
    val selectedItem = 3
    val items = listOf("Trang chủ", "Tủ đồ", "Phối đồ", "Lịch", "Tài khoản")
    val selectedIcons = listOf(Icons.Rounded.Home, Icons.Rounded.Checkroom, Icons.Rounded.AddCircle, Icons.Rounded.CalendarMonth, Icons.Rounded.Person)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Checkroom, Icons.Outlined.AddCircleOutline, Icons.Outlined.CalendarMonth, Icons.Outlined.Person)

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index
            NavigationBarItem(
                icon = { Icon(if (isSelected) selectedIcons[index] else unselectedIcons[index], item, tint = if (index == 2) CalendarPrimary else if (isSelected) CalendarPrimary else Color.Gray, modifier = if (index == 2) Modifier.size(32.dp) else Modifier.size(24.dp)) },
                label = { Text(item, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) CalendarPrimary else Color.Gray) },
                selected = isSelected, onClick = { }, colors = NavigationBarItemDefaults.colors(indicatorColor = CalendarPrimary.copy(alpha = 0.1f))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen()
}