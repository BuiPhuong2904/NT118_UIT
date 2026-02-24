package com.example.smartfashion.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.graphicsLayer

import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientAccent3

import com.example.smartfashion.ui.theme.GradientPrimaryButton
import com.example.smartfashion.ui.theme.GradientSoft
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.PrimaryCyan
import com.example.smartfashion.ui.theme.PrimaryPink
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.SecLightPink
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@Composable
fun HomeScreen() {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight)
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { HeaderSection() }
            item { SearchBarSection() }
            item { WeatherOotdWidget() }
            item { ControlCenterSection() }
            item { AiAssistantSection() }
            item { FashionHubSection() }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// THANH ĐIỀU HƯỚNG
@Composable
fun BottomNavigationBar() {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Trang chủ", "Tủ đồ", "Phối đồ", "Lịch", "Tài khoản")
    val selectedIcons = listOf(Icons.Rounded.Home, Icons.Rounded.Checkroom, Icons.Rounded.Add, Icons.Rounded.CalendarMonth, Icons.Rounded.Person)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Checkroom, Icons.Outlined.Add, Icons.Outlined.CalendarMonth, Icons.Outlined.Person)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = 0.99f }, // Ép tạo một lớp ảnh riêng (Layer) để áp dụng thuật cắt hình
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. Thanh ngang màu trắng ở dưới cùng
        Surface(
            color = SecWhite,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .drawBehind {
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f), // Màu viền xám trong suốt
                        start = androidx.compose.ui.geometry.Offset(0f, 0f), // Kéo từ góc trái
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f), // Sang góc phải
                        strokeWidth = 2f // Độ dày của viền
                    )
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Các nút bên trái
                BottomNavItem(0, items[0], selectedIcons[0], unselectedIcons[0], selectedItem, Modifier.weight(1f)) { selectedItem = 0 }
                BottomNavItem(1, items[1], selectedIcons[1], unselectedIcons[1], selectedItem, Modifier.weight(1f)) { selectedItem = 1 }

                // Chỗ trống ở giữa (Chỉ chứa chữ "Phối đồ" để nó thẳng hàng với chữ khác)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { selectedItem = 2 }
                        )
                ) {
                    // Dùng khoảng trống bằng đúng kích thước Icon để đẩy chữ xuống bằng hàng
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(
                        text = items[2],
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 11.sp,
                        color = if (selectedItem == 2) PrimaryCyan else TextBlue.copy(alpha = 0.5f),
                        fontWeight = if (selectedItem == 2) FontWeight.Bold else FontWeight.Medium
                    )
                }

                // Các nút bên phải
                BottomNavItem(3, items[3], selectedIcons[3], unselectedIcons[3], selectedItem, Modifier.weight(1f)) { selectedItem = 3 }
                BottomNavItem(4, items[4], selectedIcons[4], unselectedIcons[4], selectedItem, Modifier.weight(1f)) { selectedItem = 4 }
            }
        }

        // 2. Vòng tròn tàng hình (ĐỤC LỖ thanh trắng)
        Spacer(
            modifier = Modifier
                .padding(bottom = 30.dp)
                .size(60.dp) // Kích thước lỗ khoét viền
                .drawWithCache {
                    onDrawBehind {
                        drawCircle(
                            color = Color.Black, // Chữ màu đen nhưng có tác dụng như cục gôm
                            blendMode = BlendMode.Clear // Thuật toán xuyên thấu xóa bay phần thanh trắng bên dưới
                        )
                    }
                }
        )

        // 3. Cục nổi ở giữa (FAB) nằm chính giữa lỗ khoét
        Box(
            modifier = Modifier
                .padding(bottom = 36.dp)
                .size(48.dp)
                // --- HIỆU ỨNG GLOW KHI ĐƯỢC CHỌN ---
                .then(
                    if (selectedItem == 2) {
                        Modifier.shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            spotColor = PrimaryPink,
                            ambientColor = PrimaryCyan
                        )
                    } else {
                        Modifier
                    }
                )
                // --- MÀU NỀN LUÔN LUÔN LÀ GRADIENT ---
                .background(
                    brush = GradientAccent3,
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { selectedItem = 2 }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = items[2],
                tint = SecWhite,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// Widget phụ hỗ trợ vẽ các nút bình thường
@Composable
fun BottomNavItem(
    index: Int,
    title: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    selectedItem: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isSelected = selectedItem == index
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = title,
            tint = if (isSelected) PrimaryCyan else TextBlue.copy(alpha = 0.5f), // Đổi màu icon đồng bộ với chữ
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 11.sp,
            color = if (isSelected) PrimaryCyan else TextBlue.copy(alpha = 0.5f), // Màu chữ nhạt khi không chọn
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

// --- CÁC COMPONENT THÊM ---
@Composable
fun SearchBarSection() {
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
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = PrimaryCyan
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Tìm nhanh: Áo thun, Váy...",
                style = MaterialTheme.typography.bodyLarge,
                color = TextBlue.copy(alpha = 0.4f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = "Voice",
                tint = PrimaryCyan
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Chào Jane",
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = GradientText
                ),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Lên đồ xuống phố thôi!",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextLightBlue
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = null, tint = TextPink) }
            IconButton(onClick = {}) { Icon(Icons.Outlined.Settings, contentDescription = null, tint = PrimaryCyan) }
        }
    }
}

@Composable
fun WeatherOotdWidget() {
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
                    Text(
                        text = "28°C • Trời nắng",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 16.sp,
                        color = SecWhite // Nổi bật trên nền Gradient Tím-Cyan
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Outfit chuẩn gu hôm nay",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp,
                    color = SecWhite.copy(alpha = 0.9f) // Trắng hơi trong suốt
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sơ mi & Quần Short",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = SecWhite // Nổi bật trên nền
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    modifier = Modifier.height(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(SecWhite, shape = RoundedCornerShape(16.dp)) // Nút trắng trên nền tối sẽ hút mắt hơn
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Thử ngay",
                            style = MaterialTheme.typography.titleMedium.copy(
                                brush = GradientText
                            ),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(16.dp),
                color = SecWhite.copy(alpha = 0.2f)
            ) {
                Icon(Icons.Rounded.Checkroom, contentDescription = null, tint = SecWhite, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun ControlCenterSection() {
    Column {
        Text(
            text = "Tiện ích chính",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlue
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigFeatureCard(Modifier.weight(1f), "Thêm đồ mới", "Chụp ảnh / Tải ảnh", Icons.Rounded.AddCircle, PrimaryCyan)
            BigFeatureCard(Modifier.weight(1f), "Phòng thử đồ", "Phối đồ & Mannequin", Icons.Rounded.Style, PrimaryCyan)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallFeatureCard(Modifier.weight(1f), "Catalog", Icons.AutoMirrored.Rounded.MenuBook)
            SmallFeatureCard(Modifier.weight(1f), "Yêu thích", Icons.Rounded.Favorite)
            SmallFeatureCard(Modifier.weight(1f), "Thống kê", Icons.Rounded.Insights)
        }
    }
}

@Composable
fun BigFeatureCard(modifier: Modifier, title: String, subtitle: String, icon: ImageVector, iconTint: Color) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            Column(modifier = Modifier.align(Alignment.BottomStart)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = TextBlue
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 11.sp,
                    color = TextBlue.copy(alpha = 0.8f)
                )
            }
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.align(Alignment.TopEnd).size(32.dp))
        }
    }
}

@Composable
fun SmallFeatureCard(modifier: Modifier, title: String, icon: ImageVector) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = TextPink.copy(alpha = 0.8f), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 12.sp,
                color = TextBlue
            )
        }
    }
}

@Composable
fun AiAssistantSection() {
    Column {
        Text(
            text = "Tiện ích thông minh",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextBlue
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                AiCard(
                    title = "Stylist ảo",
                    desc = "Mặc gì giờ nhỉ?",
                    icon = Icons.Rounded.AutoAwesome,
                    bgColor = PrimaryCyan.copy(alpha = 0.15f),
                    isAi = true
                )
            }
            item {
                AiCard(
                    title = "Vali vi vu",
                    desc = "Checklist đồ.",
                    icon = Icons.Rounded.Checklist,
                    bgColor = SecLightPink, // Dùng đúng màu hồng nhạt trong bảng màu phụ
                    isAi = false
                )
            }
            item {
                AiCard(
                    title = "Mua sắm",
                    desc = "Bổ sung tủ đồ",
                    icon = Icons.Rounded.ShoppingBag,
                    bgColor = SecWhite, // Dùng màu trắng thay cho màu cam nhạt cũ
                    isAi = false
                )
            }
        }
    }
}

@Composable
fun AiCard(title: String, desc: String, icon: ImageVector, bgColor: Color, isAi: Boolean) {
    Card(
        modifier = Modifier.size(width = 140.dp, height = 90.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            if (isAi) {
                Text(
                    text = "AI",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 10.sp,
                    color = SecWhite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(GradientPrimaryButton, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 13.sp,
                    color = if(isAi) PrimaryCyan else TextBlue
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 11.sp,
                    color = TextBlue.copy(alpha = 0.6f)
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if(isAi) PrimaryCyan.copy(alpha = 0.3f) else TextBlue.copy(alpha = 0.05f),
                modifier = Modifier.align(Alignment.BottomEnd).size(40.dp).offset(x = 10.dp, y = 10.dp)
            )
        }
    }
}

@Composable
fun FashionHubSection() {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Xu hướng thịnh hành",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextBlue
            )
            Text(
                text = "Khám phá ngay >",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 13.sp,
                color = TextPink // Màu Accent cực chuẩn cho các dòng Link/Action
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(4) { index -> TrendCard(index) }
        }
    }
}

@Composable
fun TrendCard(index: Int) {
    Card(
        modifier = Modifier.size(width = 120.dp, height = 160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        // --- THAY ĐỔI MÀU NỀN TREND CARD TỪ XÁM THÀNH HỒNG NHẠT ---
        Box(modifier = Modifier.fillMaxSize().background(SecLightPink)) {
            Text(
                text = "Trend #${index + 1}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 12.sp,
                color = TextBlue
            )
            Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().background(PrimaryCyan.copy(alpha = 0.8f)).padding(8.dp)) {
                Text(
                    text = "Hè 2026",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 10.sp,
                    color = SecWhite
                )
            }
        }
    }
}

fun Modifier.rotate(degrees: Float) = this.then(Modifier.graphicsLayer(rotationZ = degrees))

@Preview(showBackground = true, heightDp = 1050)
@Composable
fun SmartFashionHomePreview() {
    HomeScreen()
}