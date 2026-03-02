package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

import com.example.smartfashion.ui.components.BottomNavigationBar
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@Composable
fun SavedOutfitsScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgLight)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 4.dp)) {
                    SavedOutfitsHeader()
                }

                Box(modifier = Modifier.padding(bottom = 12.dp)) {
                    OutfitFilterTabs(selectedFilter) { newFilter ->
                        selectedFilter = newFilter
                    }
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController, selectedItem = 2) },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 20.dp,
                start = 20.dp,
                end = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CreateNewOutfitCard(onClick = { navController.navigate("studio_screen") })
            }

            items(6) { index ->
                OutfitItemCard(
                    index = index,
                    onClick = {
                        // Bấm vào để xem chi tiết outfit đó
                        // navController.navigate("outfit_detail_screen/${index}")
                    }
                )
            }
        }
    }
}

// --- HEADER ---
@Composable
fun SavedOutfitsHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Phối đồ",
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = GradientText
                ),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bộ sưu tập & Ý tưởng",
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

// --- TABS BỘ LỌC NGANG ---
@Composable
fun OutfitFilterTabs(selectedFilter: String, onFilterSelect: (String) -> Unit) {
    val filters = listOf("Tất cả", "Yêu thích", "Mùa hè", "Đi làm", "Đi tiệc")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        items(filters.size) { index ->
            val title = filters[index]
            val isSelected = selectedFilter == title

            Surface(
                shape = CircleShape,
                color = if (isSelected) AccentBlue else Color.Transparent,
                border = if (isSelected) null else BorderStroke(1.dp, TextLightBlue.copy(alpha = 0.3f)),
                modifier = Modifier.clickable { onFilterSelect(title) }
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextBlue,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// --- THẺ (CARD) HIỂN THỊ OUTFIT ---
@Composable
fun OutfitItemCard(index: Int, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f)
                    .background(Color(0xFFEBF2FA))
            ) {
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SecWhite.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clickable { }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val isFav = index == 1
                        Text(if(isFav) "❤️" else "🤍", fontSize = 14.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Outfit #${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDarkBlue,
                    fontSize = 14.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mùa hè • Casual",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp,
                    color = TextBlue
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "12/02/2026",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 10.sp,
                    color = TextLightBlue.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// --- THẺ TẠO MỚI ---
@Composable
fun CreateNewOutfitCard(onClick: () -> Unit) {
    val stroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .clickable { onClick() }
            .drawBehind {
                drawRoundRect(
                    color = AccentBlue.copy(alpha = 0.5f),
                    style = stroke,
                    cornerRadius = CornerRadius(20.dp.toPx())
                )
            }
    ) {
        Column(modifier = Modifier.alpha(0f)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f)
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Outfit #",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mùa hè • Casual",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "12/02/2026",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 10.sp
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
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
                text = "Tạo bộ mới",
                style = MaterialTheme.typography.titleMedium,
                color = AccentBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedOutfitsPreview() {
    SavedOutfitsScreen(navController = rememberNavController())
}