package com.example.smartfashion.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Màu chủ đạo
val NotiPrimary = Color(0xFF6200EE)

// Model thông báo
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotiType,
    val isRead: Boolean = false
)

enum class NotiType { OUTFIT, PROMO, SYSTEM, WEATHER }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit = {}
) {
    // Dữ liệu giả
    val notifications = remember {
        listOf(
            NotificationItem("1", "Gợi ý mặc đẹp", "Hôm nay trời nắng 32°C, hãy thử set váy hoa nhé!", "08:00 AM", NotiType.WEATHER, false),
            NotificationItem("2", "Giảm giá 50%", "Zara đang sale lớn tại Vincom Đồng Khởi.", "09:30 AM", NotiType.PROMO, false),
            NotificationItem("3", "Cập nhật tủ đồ", "Bạn vừa thêm 3 món mới vào tủ đồ.", "Hôm qua", NotiType.SYSTEM, true),
            NotificationItem("4", "Nhắc nhở du lịch", "Đừng quên soạn đồ cho chuyến đi Đà Nẵng!", "2 ngày trước", NotiType.OUTFIT, true)
        )
    }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thông báo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = { /* Đánh dấu đã đọc hết */ }) {
                        Text("Đã đọc tất cả", fontSize = 12.sp, color = NotiPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Section Hôm nay
            item {
                Text("Mới nhất", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(notifications.take(2)) { noti ->
                NotificationRow(noti)
            }

            // Section Cũ hơn
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Trước đó", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            }
            items(notifications.drop(2)) { noti ->
                NotificationRow(noti)
            }
        }
    }
}

@Composable
fun NotificationRow(noti: NotificationItem) {
    val icon = when (noti.type) {
        NotiType.OUTFIT -> Icons.Default.Checkroom
        NotiType.PROMO -> Icons.Default.Discount
        NotiType.WEATHER -> Icons.Default.WbSunny
        NotiType.SYSTEM -> Icons.Default.Notifications
    }

    val iconColor = when (noti.type) {
        NotiType.OUTFIT -> Color(0xFFE91E63) // Hồng
        NotiType.PROMO -> Color(0xFFFF9800)  // Cam
        NotiType.WEATHER -> Color(0xFF2196F3) // Xanh
        NotiType.SYSTEM -> Color(0xFF9E9E9E) // Xám
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = if (noti.isRead) Color.White else Color(0xFFF3E5F5)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            // ĐÃ SỬA: Dùng Alignment.Top thay vì Start (vì đây là verticalAlignment của Row)
            verticalAlignment = Alignment.Top
        ) {
            // Icon tròn
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        noti.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (noti.isRead) Color.Black else NotiPrimary
                    )
                    Text(noti.time, fontSize = 10.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    noti.message,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 16.sp
                )
            }

            // Chấm đỏ nếu chưa đọc
            if (!noti.isRead) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NotiPrimary)
                        // Chỉnh lại căn giữa cho chấm đỏ
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotiPreview() {
    NotificationScreen()
}