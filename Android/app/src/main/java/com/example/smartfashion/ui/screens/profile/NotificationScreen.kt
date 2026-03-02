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

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

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
    val notifications = remember {
        listOf(
            NotificationItem("1", "Gợi ý mặc đẹp", "Hôm nay trời nắng 32°C, hãy thử set váy hoa nhé!", "08:00 AM", NotiType.WEATHER, false),
            NotificationItem("2", "Giảm giá 50%", "Zara đang sale lớn tại Vincom Đồng Khởi.", "09:30 AM", NotiType.PROMO, false),
            NotificationItem("3", "Cập nhật tủ đồ", "Bạn vừa thêm 3 món mới vào tủ đồ.", "Hôm qua", NotiType.SYSTEM, true),
            NotificationItem("4", "Nhắc nhở du lịch", "Đừng quên soạn đồ cho chuyến đi Đà Nẵng!", "2 ngày trước", NotiType.OUTFIT, true)
        )
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Thông báo",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                },
                actions = {
                    TextButton(onClick = { /* Đánh dấu đã đọc hết */ }) {
                        Text(
                            text = "Đã đọc tất cả",
                            style = MaterialTheme.typography.titleMedium,
                            fontSize = 13.sp,
                            color = AccentBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Mới nhất",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDarkBlue,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(notifications.take(2)) { noti ->
                NotificationRow(noti)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Trước đó",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDarkBlue,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            items(notifications.drop(2)) { noti ->
                NotificationRow(noti)
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }
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
        NotiType.OUTFIT -> TextPink
        NotiType.PROMO -> Color(0xFFFF9800)
        NotiType.WEATHER -> AccentBlue
        NotiType.SYSTEM -> TextLightBlue
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (noti.isRead) SecWhite else AccentBlue.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (noti.isRead) 1.dp else 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.15f),
                modifier = Modifier.size(44.dp)
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.padding(10.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = noti.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 15.sp,
                        color = if (noti.isRead) TextDarkBlue.copy(alpha = 0.7f) else TextDarkBlue
                    )
                    Text(
                        text = noti.time,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 11.sp,
                        color = TextLightBlue
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = noti.message,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 13.sp,
                    color = if (noti.isRead) TextLightBlue else TextDarkBlue.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }

            // Chấm thông báo nổi bật nếu chưa đọc
            if (!noti.isRead) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AccentBlue)
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