package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- MODEL & MÀU SẮC ---
data class DummyItem(
    val id: Int, val name: String, val category: String, val color: Color, val heightDp: Dp
)
val GzPrimary = Color(0xFF6200EE)
val GzBg = Color(0xFFFDF7FF) // Màu nền đồng bộ Home

@Composable
fun ClosetScreen() {
    // Dữ liệu giả
    val allItems = remember {
        listOf(
            DummyItem(1, "Sơ mi trắng", "Áo", Color(0xFFEEEEEE), 180.dp),
            DummyItem(2, "Quần Jean", "Quần", Color(0xFFBBDEFB), 220.dp),
            DummyItem(3, "Váy hoa nhí", "Váy", Color(0xFFFFCCBC), 200.dp),
            DummyItem(4, "Áo Thun Đen", "Áo", Color(0xFF333333), 160.dp),
            DummyItem(5, "Giày Sneaker", "Giày", Color(0xFFE0E0E0), 140.dp),
            DummyItem(6, "Kính râm", "Phụ kiện", Color(0xFFFFF59D), 120.dp),
            DummyItem(7, "Cardigan", "Áo", Color(0xFFFFAB91), 190.dp),
        )
    }
    var selectedCategory by remember { mutableStateOf("Tất cả") }
    val displayItems = if (selectedCategory == "Tất cả") allItems else allItems.filter { it.category == selectedCategory }

    Scaffold(
        containerColor = GzBg, // Nền trắng ánh tím giống Home
        bottomBar = { ClosetBottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { }, containerColor = GzPrimary, contentColor = Color.White, shape = CircleShape
            ) { Icon(Icons.Rounded.Add, "Add") }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Padding 20dp cho thẳng hàng với Home
        ) {
            // 1. HEADER (Giống hệt Home)
            ClosetHeader()

            // 2. SEARCH BAR (Đưa bộ lọc/tìm kiếm xuống đây)
            Spacer(modifier = Modifier.height(20.dp))
            ClosetSearchBar()

            // 3. TIỆN ÍCH (Utility Row)
            Spacer(modifier = Modifier.height(20.dp))
            UtilityRow()

            // 4. TABS & GRID
            Spacer(modifier = Modifier.height(20.dp))
            CategoryTabs(selected = selectedCategory, onSelect = { selectedCategory = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Lưới quần áo so le
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Ô Thêm đồ ở đầu
                item { AddNewItemCard() }
                // Các món đồ
                items(displayItems) { item -> StaggeredClosetItem(item) }
            }
        }
    }
}

// --- HEADER GIỐNG HỆT HOME ---
@Composable
fun ClosetHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Tủ đồ", // Đổi tên
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = GzPrimary
            )
            // Đổi Slogan
            Text(text = "Quản lý & Sắp xếp gọn gàng", fontSize = 15.sp, color = Color.Gray)
        }
        // Giữ nguyên 2 icon Notifications & Settings như Home
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = null, tint = GzPrimary) }
            IconButton(onClick = {}) { Icon(Icons.Outlined.Settings, contentDescription = null, tint = Color.Gray) }
        }
    }
}

// --- SEARCH BAR (Chứa bộ lọc bạn muốn tách ra) ---
@Composable
fun ClosetSearchBar() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Search, "Search", tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Tìm nhanh: Áo sơ mi...", color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.weight(1f))
            // Icon bộ lọc nằm ở đây cho gọn
            Icon(Icons.Rounded.Tune, "Filter", tint = GzPrimary)
        }
    }
}

// --- CÁC COMPONENT KHÁC GIỮ NGUYÊN ---
@Composable
fun UtilityRow() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        UtilityItem("Thống kê", Icons.Rounded.Insights, Color(0xFFE3F2FD), Color(0xFF1565C0))
        UtilityItem("Dọn tủ", Icons.Rounded.CleaningServices, Color(0xFFFFEBEE), Color(0xFFC62828))
        UtilityItem("Yêu thích", Icons.Rounded.Favorite, Color(0xFFF3E5F5), Color(0xFF7B1FA2))
        UtilityItem("Kho mẫu", Icons.Rounded.Store, Color(0xFFFFF3E0), Color(0xFFEF6C00))
    }
}

@Composable
fun UtilityItem(title: String, icon: ImageVector, bgColor: Color, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = bgColor, modifier = Modifier.size(56.dp).clickable { }) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp)) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF333333))
    }
}

@Composable
fun AddNewItemCard() {
    Column(
        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp))
            .background(Color.White).border(1.dp, Color.LightGray.copy(0.5f), RoundedCornerShape(16.dp))
            .clickable { },
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(shape = CircleShape, color = GzPrimary.copy(0.1f), modifier = Modifier.size(48.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Add, null, tint = GzPrimary) }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Thêm đồ mới", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GzPrimary)
    }
}

@Composable
fun StaggeredClosetItem(item: DummyItem) {
    var isFavorite by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().clickable { }) {
        Box(modifier = Modifier.fillMaxWidth().height(item.heightDp).clip(RoundedCornerShape(16.dp)).background(item.color)) {
            Text(item.name.take(1), Modifier.align(Alignment.Center), fontSize = 40.sp, color = Color.White.copy(0.5f))
            IconButton(onClick = { isFavorite = !isFavorite }, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                Icon(if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, null, tint = if (isFavorite) Color.Red else Color.White)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1A1A1A))
        Text(item.category, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
fun CategoryTabs(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf("Tất cả", "Áo", "Quần", "Váy", "Giày", "Phụ kiện")
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories.size) { index ->
            val cat = categories[index]
            val isSelected = cat == selected
            FilterChip(
                selected = isSelected, onClick = { onSelect(cat) }, label = { Text(cat) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color.Black, selectedLabelColor = Color.White, containerColor = Color.White, labelColor = Color.Gray),
                border = if(isSelected) null else FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = Color.LightGray),
                shape = RoundedCornerShape(50)
            )
        }
    }
}

@Composable
fun ClosetBottomBar() {
    val selectedItem = 1
    val items = listOf("Trang chủ", "Tủ đồ", "Phối đồ", "Lịch", "Tài khoản")
    val selectedIcons = listOf(Icons.Rounded.Home, Icons.Rounded.Checkroom, Icons.Rounded.AddCircle, Icons.Rounded.CalendarMonth, Icons.Rounded.Person)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Checkroom, Icons.Outlined.AddCircleOutline, Icons.Outlined.CalendarMonth, Icons.Outlined.Person)
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(if (selectedItem == index) selectedIcons[index] else unselectedIcons[index], item, tint = if (index == 2) GzPrimary else if (selectedItem == index) GzPrimary else Color.Gray, modifier = if (index == 2) Modifier.size(32.dp) else Modifier.size(24.dp)) },
                label = { Text(item, fontSize = 10.sp, fontWeight = if (selectedItem == index) FontWeight.Bold else FontWeight.Normal, color = if (selectedItem == index) GzPrimary else Color.Gray) },
                selected = selectedItem == index, onClick = { }, colors = NavigationBarItemDefaults.colors(indicatorColor = GzPrimary.copy(alpha = 0.1f))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClosetScreenPreview() {
    ClosetScreen()
}