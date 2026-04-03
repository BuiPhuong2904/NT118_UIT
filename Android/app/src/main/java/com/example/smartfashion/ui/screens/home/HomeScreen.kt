package com.example.smartfashion.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip

import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.model.Outfit
import com.example.smartfashion.model.SystemClothing
import com.example.smartfashion.ui.components.BottomNavigationBar
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientPrimaryButton
import com.example.smartfashion.ui.theme.GradientSoft
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.PrimaryCyan
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.SecLightPink
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val username = tokenManager.getUsername()
    val userId = tokenManager.getUserId()

    // Lắng nghe dữ liệu động từ ViewModel
    val recommendedOutfit by viewModel.recommendedOutfit.collectAsState()
    val trendingItems by viewModel.trendingItems.collectAsState()

    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.loadHomeData(userId)
        }
    }

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
                HeaderSection(navController, username ?: "Bạn")
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController, selectedItem = 0) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 10.dp,
                bottom = paddingValues.calculateBottomPadding() + 20.dp,
                start = 20.dp,
                end = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { SearchBarSection() }
            // Truyền dữ liệu động vào Widget
            item { WeatherOotdWidget(navController, recommendedOutfit) }
            item { ControlCenterSection(navController) }
            item { AiAssistantSection(navController) }
            // Truyền mảng xu hướng vào Hub
            item { FashionHubSection(navController, trendingItems) }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// --- CÁC COMPONENT GIỮ NGUYÊN KHÔNG ĐỔI ---
@Composable
fun SearchBarSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { /* Mở màn hình tìm kiếm tổng */ },
        shape = RoundedCornerShape(16.dp),
        color = SecWhite,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Search, contentDescription = "Search", tint = PrimaryCyan)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Tìm nhanh: Áo thun, Váy...", style = MaterialTheme.typography.bodyLarge, color = TextBlue.copy(alpha = 0.4f), fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.Mic, contentDescription = "Voice", tint = AccentBlue)
        }
    }
}

@Composable
fun HeaderSection(navController: NavController, username: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Chào $username", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText), fontWeight = FontWeight.Bold)
            Text("Lên đồ xuống phố thôi!", style = MaterialTheme.typography.bodyLarge, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextLightBlue)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.navigate("notification_screen") }) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = TextPink)
            }
            IconButton(onClick = { navController.navigate("settings_screen") }) {
                Icon(Icons.Outlined.Settings, contentDescription = null, tint = AccentBlue)
            }
        }
    }
}

@Composable
fun ControlCenterSection(navController: NavController) {
    Column {
        Text("Tiện ích chính", style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlue)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigFeatureCard(Modifier.weight(1f), "Thêm đồ mới", "Chụp ảnh / Tải ảnh", Icons.Rounded.AddCircle, PrimaryCyan) {
                navController.navigate("add_item_screen")
            }
            BigFeatureCard(Modifier.weight(1f), "Phòng thử đồ", "Phối đồ & Mannequin", Icons.Rounded.Style, PrimaryCyan) {
                navController.navigate("studio_screen")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallFeatureCard(Modifier.weight(1f), "Catalog", Icons.AutoMirrored.Rounded.MenuBook) { navController.navigate("store_screen") }
            SmallFeatureCard(Modifier.weight(1f), "Yêu thích", Icons.Rounded.Favorite) { navController.navigate("favorites_screen") }
            SmallFeatureCard(Modifier.weight(1f), "Thống kê", Icons.Rounded.Insights) { navController.navigate("insights_screen") }
        }
    }
}

@Composable
fun BigFeatureCard(modifier: Modifier, title: String, subtitle: String, icon: ImageVector, iconTint: Color, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(100.dp).clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontSize = 15.sp, color = TextBlue)
                Text(text = subtitle, style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextBlue.copy(alpha = 0.8f))
            }
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.align(Alignment.TopEnd).size(32.dp))
        }
    }
}

@Composable
fun SmallFeatureCard(modifier: Modifier, title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(80.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = TextPink.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontSize = 12.sp, color = TextBlue)
        }
    }
}

@Composable
fun AiAssistantSection(navController: NavController) {
    Column {
        Text("Tiện ích thông minh", style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlue)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                AiCard("Stylist ảo", "Mặc gì giờ nhỉ?", Icons.Rounded.AutoAwesome, PrimaryCyan.copy(alpha = 0.15f), true) { navController.navigate("ai_chat_screen") }
            }
            item {
                AiCard("Vali vi vu", "Checklist đồ.", Icons.Rounded.Checklist, SecLightPink, false) { navController.navigate("travel_planner_screen") }
            }
            item {
                AiCard("Mua sắm", "Bổ sung tủ đồ", Icons.Rounded.ShoppingBag, PrimaryCyan.copy(alpha = 0.15f), false) { navController.navigate("store_screen") }
            }
        }
    }
}

@Composable
fun AiCard(title: String, desc: String, icon: ImageVector, bgColor: Color, isAi: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(width = 140.dp, height = 90.dp).clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            if (isAi) {
                Text(
                    text = "AI", style = MaterialTheme.typography.titleMedium, color = SecWhite, fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.TopEnd).background(GradientPrimaryButton, RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextBlue)
                Text(text = desc, style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextBlue.copy(alpha = 0.6f))
            }
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryCyan.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.BottomEnd).size(40.dp).offset(x = 10.dp, y = 10.dp))
        }
    }
}

// --- CÁC COMPONENT ĐƯỢC LÀM ĐỘNG CHỨA DATA THẬT ---

@Composable
fun WeatherOotdWidget(navController: NavController, outfit: Outfit?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GradientSoft)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.WbSunny, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("28°C • Trời nắng", style = MaterialTheme.typography.titleMedium, fontSize = 16.sp, color = SecWhite)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Outfit chuẩn gu hôm nay", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = SecWhite.copy(alpha = 0.9f))
                Spacer(modifier = Modifier.height(6.dp))

                // IN TÊN OUTFIT THẬT RA ĐÂY
                Text(
                    text = outfit?.name ?: "Đang gợi ý...",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = SecWhite,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        // BẤM VÀO ĐỂ NHẢY SANG TRANG CHI TIẾT CỦA BỘ ĐỒ ĐÓ
                        outfit?.outfitId?.let { id ->
                            navController.navigate("outfit_detail_screen/$id")
                        } ?: run {
                            navController.navigate("studio_screen") // Nêu ko có đồ thì nhẩy vào studio
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(SecWhite, shape = RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Thử ngay", style = MaterialTheme.typography.titleMedium.copy(brush = GradientText), fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    }
                }
            }

            // IN ẢNH BÌA OUTFIT THẬT VÀO GÓC NÀY
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(16.dp),
                color = SecWhite.copy(alpha = 0.2f)
            ) {
                if (outfit?.imagePreviewUrl != null) {
                    AsyncImage(
                        model = outfit.imagePreviewUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Rounded.Checkroom, contentDescription = null, tint = SecWhite, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun FashionHubSection(navController: NavController, trendingItems: List<SystemClothing>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Xu hướng thịnh hành", style = MaterialTheme.typography.titleLarge, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextBlue)
            Text("Khám phá ngay >", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextPink, modifier = Modifier.clickable { navController.navigate("fashion_hub_screen") })
        }
        Spacer(modifier = Modifier.height(12.dp))

        // NẾU CÓ DỮ LIỆU THẬT THÌ IN RA
        if (trendingItems.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(trendingItems) { item ->
                    TrendCard(
                        imageUrl = item.imageUrl ?: "",
                        title = item.name ?: "Trending"
                    ) {
                        navController.navigate("store_item_detail/${item.templateId}")
                    }
                }
            }
        } else {
            // NẾU CHƯA LOAD XONG THÌ IN PLACEHOLDER
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(4) { index ->
                    TrendCard(
                        imageUrl = "",
                        title = "Trend #${index + 1}"
                    ) {
                        navController.navigate("fashion_hub_screen")
                    }
                }
            }
        }
    }
}

@Composable
fun TrendCard(
    imageUrl: String,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 120.dp, height = 160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(SecLightPink)) {

            // LOAD ẢNH THẬT
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Center), fontSize = 12.sp, color = TextBlue)
            }

            Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().background(PrimaryCyan.copy(alpha = 0.8f)).padding(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 10.sp,
                    color = SecWhite,
                    maxLines = 1
                )
            }
        }
    }
}

fun Modifier.rotate(degrees: Float) = this.then(Modifier.graphicsLayer(rotationZ = degrees))

@Preview(showBackground = true)
@Composable
fun SmartFashionHomePreview() {
    HomeScreen(navController = rememberNavController())
}