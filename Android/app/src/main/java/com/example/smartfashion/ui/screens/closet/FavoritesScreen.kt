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

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.SystemClothing
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@Composable
fun FavoritesScreen(
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tủ đồ của tôi", "Wishlist")

    // Dữ liệu Tủ đồ
    val closetItems by viewModel.favoriteClothes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val totalFavorites by viewModel.totalCount.collectAsState()
    val gridState = rememberLazyStaggeredGridState()

    // Dữ liệu Wishlist
    val wishlistItems by viewModel.wishlistClothes.collectAsState()
    val isWishlistLoading by viewModel.isWishlistLoading.collectAsState()
    val totalWishlist by viewModel.wishlistTotalCount.collectAsState()
    val wishlistGridState = rememberLazyStaggeredGridState()

    // Lắng nghe cuộn cho Tủ đồ
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= gridState.layoutInfo.totalItemsCount - 2 && !isLoading) {
                    viewModel.loadMore(userId = 1)
                }
            }
    }

    // Lắng nghe cuộn cho Wishlist
    LaunchedEffect(wishlistGridState) {
        snapshotFlow { wishlistGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= wishlistGridState.layoutInfo.totalItemsCount - 2 && !isWishlistLoading) {
                    viewModel.loadMoreWishlist()
                }
            }
    }

    // Load dữ liệu khi vào màn hình
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchFavoriteClothes(userId = 1, isRefresh = true)
                viewModel.fetchWishlistClothes(isRefresh = true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Đồ yêu thích", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText), fontWeight = FontWeight.Bold)
                    }
                    val currentCount = if (selectedTabIndex == 0) totalFavorites else totalWishlist
                    if (currentCount > 0) {
                        Text("$currentCount món", style = MaterialTheme.typography.titleMedium, color = TextLightBlue)
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = AccentBlue,
                    divider = { HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f)) },
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), color = AccentBlue, height = 3.dp)
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
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp)) {
            if (selectedTabIndex == 0) {
                // TAB 1: TỦ ĐỒ
                if (isLoading && closetItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentBlue) }
                } else if (closetItems.isEmpty()) {
                    EmptyFavoritesState("Tủ đồ trống trơn!", "Bạn chưa thả tim cho món đồ nào\ntrong Tủ đồ của mình cả.")
                } else {
                    LazyVerticalStaggeredGrid(
                        state = gridState,
                        columns = StaggeredGridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(closetItems, key = { "closet_${it.clothingId}" }) { item ->
                            FavoriteClosetCard(item = item, onRemove = { viewModel.removeFavorite(item) }, navController = navController)
                        }
                        if (isLoading && closetItems.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentBlue, modifier = Modifier.size(32.dp)) }
                            }
                        }
                    }
                }
            } else {
                // TAB 2: WISHLIST (KHO MẪU)
                if (isWishlistLoading && wishlistItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentBlue) }
                } else if (wishlistItems.isEmpty()) {
                    EmptyFavoritesState("Wishlist đang trống!", "Hãy dạo quanh Kho mẫu và lưu lại\nnhững món bạn muốn mua nhé.")
                } else {
                    LazyVerticalStaggeredGrid(
                        state = wishlistGridState,
                        columns = StaggeredGridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalItemSpacing = 12.dp,
                        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(wishlistItems, key = { "wishlist_${it.templateId}" }) { item ->
                            WishlistCard(item = item, onRemove = { viewModel.removeWishlistFavorite(item) }, navController = navController)
                        }
                        if (isWishlistLoading && wishlistItems.isNotEmpty()) {
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentBlue, modifier = Modifier.size(32.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- THẺ HIỂN THỊ: TỦ ĐỒ ---
@Composable
fun FavoriteClosetCard(item: Clothing, onRemove: () -> Unit, navController: NavController) {
    val itemHeight = remember { (160..240).random().dp }
    Column(modifier = Modifier.fillMaxWidth().clickable {
        item.clothingId?.let { id -> navController.navigate("item_detail/$id") }
    }) {
        Box(modifier = Modifier.fillMaxWidth().height(itemHeight).clip(RoundedCornerShape(16.dp)).background(SecWhite)) {
            AsyncImage(model = item.imageUrl, contentDescription = item.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            IconButton(onClick = onRemove, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                Icon(Icons.Rounded.Favorite, contentDescription = "Bỏ thích", tint = TextPink)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextDarkBlue, maxLines = 1)
        Text(text = item.brandName ?: "Chưa phân loại", style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextLightBlue)
    }
}

// --- THẺ HIỂN THỊ: WISHLIST ---
@Composable
fun WishlistCard(item: SystemClothing, onRemove: () -> Unit, navController: NavController) {
    val itemHeight = remember { (160..240).random().dp }
    Column(modifier = Modifier.fillMaxWidth().clickable {
        item.templateId?.let { id -> navController.navigate("store_item_detail/$id") }
    }) {
        Box(modifier = Modifier.fillMaxWidth().height(itemHeight).clip(RoundedCornerShape(16.dp)).background(Color(0xFFF3F6FA))) {
            AsyncImage(model = item.imageUrl, contentDescription = item.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

            // Nút Giỏ hàng trang trí
            Surface(shape = RoundedCornerShape(topEnd = 16.dp), color = AccentBlue.copy(alpha = 0.9f), modifier = Modifier.align(Alignment.BottomStart)) {
                Icon(Icons.Rounded.LocalMall, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp).size(16.dp))
            }
            // Nút Tim
            IconButton(onClick = onRemove, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                Icon(Icons.Rounded.Favorite, contentDescription = "Bỏ thích", tint = TextPink)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextDarkBlue, maxLines = 1)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = item.categoryName ?: "Danh mục", style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextLightBlue)
            Text(text = "Mẫu hệ thống", style = MaterialTheme.typography.titleMedium, fontSize = 10.sp, color = AccentBlue)
        }
    }
}

// --- TRẠNG THÁI TRỐNG ---
@Composable
fun EmptyFavoritesState(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Surface(shape = CircleShape, color = TextPink.copy(alpha = 0.1f), modifier = Modifier.size(100.dp)) {
            Icon(Icons.Rounded.HeartBroken, contentDescription = null, tint = TextPink, modifier = Modifier.padding(24.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = TextDarkBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = desc, style = MaterialTheme.typography.bodyLarge, color = TextLightBlue, textAlign = TextAlign.Center, fontSize = 14.sp)
    }
}