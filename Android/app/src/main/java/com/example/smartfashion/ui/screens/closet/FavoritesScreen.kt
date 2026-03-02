package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HeartBroken
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

data class FavoriteClosetItem(val id: Int, val name: String, val category: String, val imageUrl: String, val heightDp: Dp)
data class WishlistItem(val id: Int, val name: String, val brand: String, val price: String, val imageUrl: String, val heightDp: Dp)

@Composable
fun FavoritesScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tủ đồ của tôi", "Wishlist")

    val closetItems = remember {
        mutableStateListOf(
            FavoriteClosetItem(1, "Váy hoa nhí", "Váy", "https://i.postimg.cc/9MXZHYtp/3.jpg", 200.dp),
            FavoriteClosetItem(2, "Áo khoác Blazer", "Áo", "https://i.postimg.cc/9MXZHYtp/3.jpg", 240.dp)
        )
    }

    val wishlistItems = remember {
        mutableStateListOf(
            WishlistItem(1, "Túi xách Mini Chanel", "Chanel", "1.200.000đ", "https://i.postimg.cc/9MXZHYtp/3.jpg", 180.dp),
            WishlistItem(2, "Giày Sneaker Nike", "Nike", "2.500.000đ", "https://i.postimg.cc/9MXZHYtp/3.jpg", 160.dp),
            WishlistItem(3, "Đầm dạ hội đỏ", "Zara", "890.000đ", "https://i.postimg.cc/9MXZHYtp/3.jpg", 250.dp)
        )
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgLight)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // 1. HEADER
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
                        Text(
                            text = "Đồ yêu thích",
                            style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    val currentCount = if (selectedTabIndex == 0) closetItems.size else wishlistItems.size
                    if (currentCount > 0) {
                        Text(
                            text = "$currentCount món",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextLightBlue // Đổi màu nhẹ nhàng lại
                        )
                    }
                }

                // 2. TAB ROW CỐ ĐỊNH
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = AccentBlue,
                    divider = { HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f)) },
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AccentBlue,
                            height = 3.dp
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTabIndex == index) AccentBlue else TextLightBlue.copy(alpha = 0.8f)
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            if (selectedTabIndex == 0) {
                if (closetItems.isEmpty()) {
                    EmptyFavoritesState("Tủ đồ trống trơn!", "Bạn chưa thả tim cho món đồ nào\ntrong Tủ đồ của mình cả.")
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(closetItems) { item ->
                            FavoriteClosetCard(item = item, onRemove = { closetItems.remove(item) })
                        }
                    }
                }
            } else {
                if (wishlistItems.isEmpty()) {
                    EmptyFavoritesState("Wishlist đang trống!", "Hãy dạo quanh Kho mẫu và lưu lại\nnhững món bạn muốn mua nhé.")
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(wishlistItems) { item ->
                            WishlistCard(item = item, onRemove = { wishlistItems.remove(item) })
                        }
                    }
                }
            }
        }
    }
}

// --- THẺ HIỂN THỊ: TỦ ĐỒ ---
@Composable
fun FavoriteClosetCard(item: FavoriteClosetItem, onRemove: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable { }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(item.heightDp)
                .clip(RoundedCornerShape(16.dp))
                .background(SecWhite)
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
            ) {
                Icon(Icons.Rounded.Favorite, contentDescription = "Bỏ thích", tint = TextPink)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextDarkBlue)
        Text(text = item.category, style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextLightBlue)
    }
}

// --- THẺ HIỂN THỊ: WISHLIST ---
@Composable
fun WishlistCard(item: WishlistItem, onRemove: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable { }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(item.heightDp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF3F6FA))
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Surface(
                shape = RoundedCornerShape(topEnd = 16.dp),
                color = AccentBlue.copy(alpha = 0.9f),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Icon(Icons.Rounded.LocalMall, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp).size(16.dp))
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
            ) {
                Icon(Icons.Rounded.Favorite, contentDescription = "Bỏ thích", tint = TextPink)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextDarkBlue, maxLines = 1)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = item.brand, style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextLightBlue)
            Text(text = item.price, style = MaterialTheme.typography.titleMedium, fontSize = 12.sp, color = TextPink)
        }
    }
}

// --- TRẠNG THÁI TRỐNG ---
@Composable
fun EmptyFavoritesState(title: String, desc: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = TextPink.copy(alpha = 0.1f),
            modifier = Modifier.size(100.dp)
        ) {
            Icon(Icons.Rounded.HeartBroken, contentDescription = null, tint = TextPink, modifier = Modifier.padding(24.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextDarkBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodyLarge,
            color = TextLightBlue,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    FavoritesScreen(navController = rememberNavController())
}