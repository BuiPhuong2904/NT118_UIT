package com.example.smartfashion.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo
val ProfilePrimary = Color(0xFF6200EE)

@Composable
fun ProfileScreen() {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        // Bỏ topBar mặc định đi để dùng Header tùy chỉnh bên dưới
        bottomBar = { ProfileBottomBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp) // Padding 20dp cho thẳng hàng với các Tab khác
                .verticalScroll(scrollState)
        ) {
            // 1. HEADER (Đã sửa giống các Tab khác)
            ProfileHeader()

            Spacer(modifier = Modifier.height(20.dp))

            // 2. THẺ THÔNG TIN CÁ NHÂN (User Info Card)
            UserInfoCard()

            Spacer(modifier = Modifier.height(16.dp))

            // 3. THỐNG KÊ NHANH (Stats)
            FashionStatsRow()

            Spacer(modifier = Modifier.height(16.dp))

            // 4. SỐ ĐO CƠ THỂ
            BodyMeasurementCard()

            Spacer(modifier = Modifier.height(24.dp))

            // 5. MENU TÙY CHỌN
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)) // Bo góc cho nguyên khối menu đẹp hơn
                    .background(Color.White)
            ) {
                ProfileMenuItem(icon = Icons.Outlined.ShoppingBag, title = "Đơn hàng đã mua")
                ProfileMenuItem(icon = Icons.Outlined.FavoriteBorder, title = "Sản phẩm yêu thích")
                HorizontalDivider(color = Color(0xFFF0F0F0))
                ProfileMenuItem(icon = Icons.Outlined.Backup, title = "Sao lưu & Đồng bộ")
                ProfileMenuItem(icon = Icons.Outlined.HelpOutline, title = "Trợ giúp & Phản hồi")
                HorizontalDivider(color = Color(0xFFF0F0F0))
                ProfileMenuItem(icon = Icons.Outlined.Logout, title = "Đăng xuất", isDestructive = true)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Version Info
            Text(
                text = "SmartFashion v1.0.0",
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- HEADER MỚI (ĐỒNG BỘ STYLE) ---
@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Tài khoản",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = ProfilePrimary
            )
            Text(
                text = "Quản lý & Cài đặt", // Slogan
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
        // Icon bên phải (Notification & Settings)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = ProfilePrimary)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Settings, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

// Tách phần Info ra thành Card riêng cho đẹp
@Composable
fun UserInfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0E0E0))
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
            Text("Nguyễn Văn A", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = ProfilePrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Thành viên Vàng",
                    color = ProfilePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // Nút Edit nhỏ
        IconButton(onClick = { }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.Gray)
        }
    }
}

@Composable
fun FashionStatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(count = "124", label = "Món đồ")
        // Divider dọc nhỏ
        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray.copy(0.5f)))
        StatItem(count = "45", label = "Outfit")
        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray.copy(0.5f)))
        StatItem(count = "12", label = "Sự kiện")
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ProfilePrimary)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun BodyMeasurementCard() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Số đo cơ thể", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(0.dp) // Bỏ bóng để phẳng lì theo style mới
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Straighten, null, tint = ProfilePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("1m75 • 65kg", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Dáng: Tam giác ngược", fontSize = 13.sp, color = Color.Gray)
                }

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5), contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(32.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Cập nhật", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) Color.Red else Color.Gray,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDestructive) Color.Red else Color.Black
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

// --- BOTTOM BAR (Index 4) ---
@Composable
fun ProfileBottomBar() {
    val selectedItem = 4
    val items = listOf("Trang chủ", "Tủ đồ", "Phối đồ", "Lịch", "Tài khoản")
    val selectedIcons = listOf(Icons.Rounded.Home, Icons.Rounded.Checkroom, Icons.Rounded.AddCircle, Icons.Rounded.CalendarMonth, Icons.Rounded.Person)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Checkroom, Icons.Outlined.AddCircleOutline, Icons.Outlined.CalendarMonth, Icons.Outlined.Person)

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index
            NavigationBarItem(
                icon = { Icon(if (isSelected) selectedIcons[index] else unselectedIcons[index], item, tint = if (index == 2) ProfilePrimary else if (isSelected) ProfilePrimary else Color.Gray, modifier = if (index == 2) Modifier.size(32.dp) else Modifier.size(24.dp)) },
                label = { Text(item, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) ProfilePrimary else Color.Gray) },
                selected = isSelected, onClick = { }, colors = NavigationBarItemDefaults.colors(indicatorColor = ProfilePrimary.copy(alpha = 0.1f))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}