package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo (Tím)
val SavedOutfitPrimary = Color(0xFF6200EE)

@Composable
fun SavedOutfitsScreen(
    onOutfitClick: () -> Unit = {}, // Bấm vào để xem chi tiết
    onAddNewClick: () -> Unit = {}  // Bấm để vào Studio tạo mới
) {
    Scaffold(
        containerColor = Color(0xFFFDF7FF), // Màu nền sáng ánh tím nhẹ (giống Closet)

        // --- BOTTOM BAR ---
        bottomBar = { StudioBottomBar() },

        // --- NÚT FAB (+) ---
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNewClick,
                containerColor = SavedOutfitPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, "Create") },
                text = { Text("Tạo bộ mới") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Padding 20dp cho thẳng hàng với Home/Closet
        ) {
            // 1. HEADER (Đã sửa giống ClosetScreen, không còn nút Back)
            SavedOutfitsHeader()

            // 2. TAB LỌC (Filter Tabs)
            Spacer(modifier = Modifier.height(20.dp))
            OutfitFilterTabs()

            Spacer(modifier = Modifier.height(16.dp))

            // 3. GRID HIỂN THỊ CÁC BỘ OUTFIT
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 cột
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Giả lập 6 bộ đồ đã lưu
                items(6) { index ->
                    OutfitItemCard(index, onClick = onOutfitClick)
                }
            }
        }
    }
}

// --- COMPONENT HEADER (GIỐNG CLOSET HEADER) ---
@Composable
fun SavedOutfitsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Phối đồ", // Tiêu đề Tab
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = SavedOutfitPrimary
            )
            Text(
                text = "Bộ sưu tập & Ý tưởng", // Slogan
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
        // 2 icon Notifications & Settings y hệt các màn khác
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = SavedOutfitPrimary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Settings, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun OutfitFilterTabs() {
    ScrollableTabRow(
        selectedTabIndex = 0,
        containerColor = Color.Transparent,
        edgePadding = 0.dp,
        divider = {},
        indicator = {}
    ) {
        listOf("Tất cả", "Yêu thích", "Mùa hè", "Đi làm", "Đi tiệc").forEachIndexed { index, title ->
            // Logic màu sắc: Tab đầu tiên (index 0) sẽ màu đen, còn lại trắng
            val isSelected = index == 0
            val containerColor = if (isSelected) Color.Black else Color.White
            val contentColor = if (isSelected) Color.White else Color.Black

            SuggestionChip(
                onClick = {},
                label = { Text(title) },
                modifier = Modifier.padding(end = 8.dp),
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = containerColor,
                    labelColor = contentColor
                ),
                // SỬA LỖI Ở ĐÂY: Dùng BorderStroke trực tiếp
                border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(50) // Bo tròn mềm mại
            )
        }
    }
}

@Composable
fun OutfitItemCard(index: Int, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            // Ảnh Outfit (Vuông hoặc tỷ lệ 4:3)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f)
                    .background(Color(0xFFEEEEEE))
            ) {
                // Thay bằng link ảnh thật của bộ đồ
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Nút tim (Yêu thích)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clickable { }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val isFav = index == 1
                        Text(if(isFav) "❤️" else "🤍", fontSize = 16.sp)
                    }
                }
            }

            // Thông tin bên dưới
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Outfit #${index + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mùa hè • Casual",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "12/02/2026",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}

// --- BOTTOM BAR ---
@Composable
fun StudioBottomBar() {
    val selectedItem = 2
    val items = listOf("Trang chủ", "Tủ đồ", "Phối đồ", "Lịch", "Tài khoản")
    val selectedIcons = listOf(Icons.Rounded.Home, Icons.Rounded.Checkroom, Icons.Rounded.AddCircle, Icons.Rounded.CalendarMonth, Icons.Rounded.Person)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Checkroom, Icons.Outlined.AddCircleOutline, Icons.Outlined.CalendarMonth, Icons.Outlined.Person)

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index
            NavigationBarItem(
                icon = { Icon(if (isSelected) selectedIcons[index] else unselectedIcons[index], item, tint = if (index == 2) SavedOutfitPrimary else if (isSelected) SavedOutfitPrimary else Color.Gray, modifier = if (index == 2) Modifier.size(32.dp) else Modifier.size(24.dp)) },
                label = { Text(item, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) SavedOutfitPrimary else Color.Gray) },
                selected = isSelected, onClick = { }, colors = NavigationBarItemDefaults.colors(indicatorColor = SavedOutfitPrimary.copy(alpha = 0.1f))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedOutfitsPreview() {
    SavedOutfitsScreen()
}