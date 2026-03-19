package com.example.smartfashion.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun ProfileScreen(
    navController: NavController,
    onLogoutClick: () -> Unit
) {
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
                ProfileHeader()
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController, selectedItem = 4) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 10.dp,
                bottom = paddingValues.calculateBottomPadding() + 40.dp,
                start = 20.dp,
                end = 20.dp
            )
        ) {
            item {
                UserInfoCard()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                FashionStatsRow()
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                BodyMeasurementCard()
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SecWhite),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ProfileMenuItem(icon = Icons.Outlined.ShoppingBag, title = "Đơn hàng đã mua")
                        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))

                        ProfileMenuItem(icon = Icons.Outlined.FavoriteBorder, title = "Sản phẩm yêu thích")
                        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))

                        ProfileMenuItem(icon = Icons.Outlined.Backup, title = "Sao lưu & Đồng bộ")
                        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))

                        ProfileMenuItem(icon = Icons.AutoMirrored.Outlined.HelpOutline, title = "Trợ giúp & Phản hồi")
                        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))

                        ProfileMenuItem(icon = Icons.AutoMirrored.Outlined.Logout, title = "Đăng xuất", isDestructive = true, onClick = onLogoutClick
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Text(
                    text = "SmartFashion v1.0.0",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = TextLightBlue.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

// --- HEADER ---
@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Tài khoản",
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = GradientText
                ),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Quản lý & Cài đặt",
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

// --- THÔNG TIN NGƯỜI DÙNG ---
@Composable
fun UserInfoCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nguyễn Văn A",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDarkBlue,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = AccentBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Thành viên Vàng",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AccentBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Nút Edit
            IconButton(onClick = { }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = AccentBlue)
            }
        }
    }
}

// --- THỐNG KÊ ---
@Composable
fun FashionStatsRow() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(count = "124", label = "Món đồ")
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(TextLightBlue.copy(0.2f)))
            StatItem(count = "45", label = "Outfit")
            Box(modifier = Modifier.width(1.dp).height(24.dp).background(TextLightBlue.copy(0.2f)))
            StatItem(count = "12", label = "Sự kiện")
        }
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 20.sp,
            color = AccentBlue
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 12.sp,
            color = TextBlue
        )
    }
}

// --- SỐ ĐO CƠ THỂ ---
@Composable
fun BodyMeasurementCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Số đo cơ thể",
                style = MaterialTheme.typography.titleMedium,
                color = TextDarkBlue,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 8.dp)
            )

            HorizontalDivider(
                color = TextLightBlue.copy(alpha = 0.1f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Straighten, null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1m75 • 65kg",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextDarkBlue,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dáng: Tam giác ngược",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 13.sp,
                        color = TextBlue
                    )
                }

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue.copy(alpha = 0.1f)),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    modifier = Modifier.height(34.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Cập nhật", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = AccentBlue, fontSize = 12.sp)
                }
            }
        }
    }
}

// --- MENU CÀI ĐẶT ---
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) Color(0xFFFF4B4B) else AccentBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDestructive) Color(0xFFFF4B4B) else TextDarkBlue
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextLightBlue.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        navController = rememberNavController(),
        onLogoutClick = {}
    )
}