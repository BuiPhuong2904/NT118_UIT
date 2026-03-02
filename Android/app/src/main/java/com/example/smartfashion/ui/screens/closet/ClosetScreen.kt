package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import com.example.smartfashion.ui.components.BottomNavigationBar
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.PrimaryCyan
import com.example.smartfashion.ui.theme.PrimaryPink
import com.example.smartfashion.ui.theme.SecDarkPink
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.SoftBlue
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

data class DummyItem(
    val id: Int, val name: String, val category: String, val color: Color, val heightDp: Dp
)

@Composable
fun ClosetScreen(navController: NavController) {
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
        containerColor = Color.Transparent,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgLight)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                ClosetHeader()
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController, selectedItem = 1) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight)
                .padding(top = paddingValues.calculateTopPadding() + 10.dp)
                .padding(horizontal = 20.dp)
        ) {
            ClosetSearchBar()
            Spacer(modifier = Modifier.height(20.dp))
            UtilityRow(navController = navController)
            Spacer(modifier = Modifier.height(15.dp))
            CategoryTabs(selected = selectedCategory, onSelect = { selectedCategory = it })
            Spacer(modifier = Modifier.height(15.dp))

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item { AddNewItemCard() }
                items(displayItems) { item -> StaggeredClosetItem(item) }
            }
        }
    }
}

// --- HEADER ---
@Composable
fun ClosetHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Tủ đồ",
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = GradientText
                ),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Quản lý & Sắp xếp gọn gàng",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextLightBlue
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = null, tint = TextPink) }
            IconButton(onClick = {}) { Icon(Icons.Outlined.Settings, contentDescription = null, tint = AccentBlue) }
        }
    }
}

// --- SEARCH BAR ---
@Composable
fun ClosetSearchBar() {
    Surface(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(16.dp),
        color = SecWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Search, "Search", tint = PrimaryCyan)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tìm nhanh: Áo sơ mi...",
                style = MaterialTheme.typography.bodyLarge,
                color = TextBlue.copy(alpha = 0.4f),
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Rounded.Tune, "Filter", tint = AccentBlue)
        }
    }
}

// --- TIỆN ÍCH ---
@Composable
fun UtilityRow(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        UtilityItem(
            title = "Thống kê",
            icon = Icons.Rounded.Insights,
            bgColor = AccentBlue.copy(alpha = 0.15f),
            iconColor = AccentBlue,
            onClick = { navController.navigate("insights_screen") }
        )
        UtilityItem(
            title = "Dọn tủ",
            icon = Icons.Rounded.CleaningServices,
            bgColor = SecDarkPink.copy(alpha = 0.1f),
            iconColor = SecDarkPink,
            onClick = { navController.navigate("declutter_screen") }
        )

        UtilityItem(
            title = "Yêu thích",
            icon = Icons.Rounded.Favorite,
            bgColor = PrimaryPink.copy(alpha = 0.15f),
            iconColor = TextPink,
            onClick = { navController.navigate("favorites_screen") }
        )

        UtilityItem(
            title = "Kho mẫu",
            icon = Icons.Rounded.Store,
            bgColor = PrimaryCyan.copy(alpha = 0.2f),
            iconColor = TextBlue,
            onClick = { navController.navigate("store_screen") }
        )
    }
}

@Composable
fun UtilityItem(title: String, icon: ImageVector, bgColor: Color, iconColor: Color, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = bgColor, modifier = Modifier.size(56.dp).clickable { onClick() }) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp)) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 12.sp,
            color = TextBlue
        )
    }
}

// --- THẺ THÊM ĐỒ MỚI ---
@Composable
fun AddNewItemCard() {
    val stroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .clickable { /* Xử lý sự kiện click thêm đồ ở đây */ }
            .drawBehind {
                drawRoundRect(
                    color = AccentBlue.copy(alpha = 0.5f),
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = AccentBlue.copy(alpha = 0.1f),
                modifier = Modifier.size(50.dp)
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Thêm đồ mới",
                style = MaterialTheme.typography.titleMedium,
                color = AccentBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StaggeredClosetItem(item: DummyItem) {
    var isFavorite by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().clickable { }) {
        Box(modifier = Modifier.fillMaxWidth().height(item.heightDp).clip(RoundedCornerShape(16.dp)).background(item.color)) {
            Text(
                text = item.name.take(1),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 40.sp,
                color = SecWhite.copy(0.5f)
            )
            IconButton(onClick = { isFavorite = !isFavorite }, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                Icon(if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, null, tint = if (isFavorite) PrimaryPink else SecWhite)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.name,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 13.sp,
            color = TextBlue
        )
        Text(
            text = item.category,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 11.sp,
            color = TextLightBlue
        )
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
                selected = isSelected, onClick = { onSelect(cat) },
                label = {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 13.sp
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentBlue,
                    selectedLabelColor = SecWhite,
                    containerColor = SecWhite,
                    labelColor = SoftBlue
                ),
                border = if(isSelected) null else FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = SoftBlue.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(50)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClosetScreenPreview() {
    ClosetScreen(navController = rememberNavController())
}