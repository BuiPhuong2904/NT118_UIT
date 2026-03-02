package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

data class SystemClothesItem(
    val templateId: Int,
    val categoryName: String,
    val description: String,
    val imageUrl: String,
    val colorHex: Color,
    val isWished: Boolean = false
)

@Composable
fun StoreScreen(navController: NavController) {
    val storeItems = remember {
        mutableStateListOf(
            SystemClothesItem(1, "ÁO KHOÁC", "Áo khoác Tweed mỏng nhẹ nhàng cho mùa thu", "https://i.postimg.cc/9MXZHYtp/3.jpg", Color(0xFFE0E0E0), false),
            SystemClothesItem(2, "ĐẦM", "Đầm Maxi lụa dạo biển xếp ly tầng", "https://i.postimg.cc/9MXZHYtp/3.jpg", Color(0xFFFFCDD2), true),
            SystemClothesItem(3, "PHỤ KIỆN", "Túi xách Crossbody da PU cao cấp", "https://i.postimg.cc/9MXZHYtp/3.jpg", Color(0xFFD7CCC8), false),
            SystemClothesItem(4, "ÁO", "Áo sơ mi lụa tơ tằm thanh lịch", "https://i.postimg.cc/9MXZHYtp/3.jpg", Color(0xFFFFFFFF), false),
            SystemClothesItem(5, "CHÂN VÁY", "Chân váy xếp ly cạp cao hack dáng", "https://i.postimg.cc/9MXZHYtp/3.jpg", Color(0xFFB3E5FC), false),
            SystemClothesItem(6, "GIÀY", "Giày Loafer mũi vuông đính viền kim loại", "https://i.postimg.cc/9MXZHYtp/3.jpg", Color(0xFF424242), false),
        )
    }

    var selectedTag by remember { mutableStateOf("Tất cả") }
    val tags = listOf("Tất cả", "Công sở", "Hẹn hò", "Dạo phố", "Mùa đông", "Thể thao")

    Scaffold(
        containerColor = BgLight,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .background(BgLight)
            ) {
                // HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Kho mẫu",
                                style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                                fontWeight = FontWeight.Bold
                            )
                            Text("Khám phá ý tưởng phối đồ", fontSize = 12.sp, color = TextLightBlue)
                        }
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Rounded.Search, contentDescription = "Tìm kiếm", tint = AccentBlue)
                    }
                }

                // THANH FILTER
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    items(tags.size) { index ->
                        val tag = tags[index]
                        val isSelected = tag == selectedTag
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) AccentBlue else Color.Transparent,
                            border = if (isSelected) null else BorderStroke(1.dp, TextLightBlue.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable { selectedTag = tag }
                        ) {
                            Text(
                                text = tag,
                                color = if (isSelected) Color.White else TextBlue,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 8.dp,
                bottom = 40.dp,
                start = 20.dp,
                end = 20.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(storeItems) { item ->
                SystemClothesCard(
                    item = item,
                    onToggleWishlist = {
                        val index = storeItems.indexOf(item)
                        if (index != -1) {
                            storeItems[index] = item.copy(isWished = !item.isWished)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SystemClothesCard(item: SystemClothesItem, onToggleWishlist: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth().clickable { }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF3F6FA))
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.categoryName,
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentScale = ContentScale.Fit
                )

                // Nút Thả tim
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (item.isWished) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (item.isWished) TextPink else TextLightBlue.copy(alpha = 0.7f)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.categoryName,
                        color = AccentBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(item.colorHex)
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.description,
                    color = TextDarkBlue,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StoreScreenPreview() {
    StoreScreen(navController = rememberNavController())
}