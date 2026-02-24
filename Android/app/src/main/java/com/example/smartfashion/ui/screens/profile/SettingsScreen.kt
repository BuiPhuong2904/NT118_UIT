package com.example.smartfashion.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {}
) {
    var notiEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cài đặt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Phần 1: Chung
            item { SectionTitle("Chung") }
            item {
                SettingSwitchItem(
                    title = "Thông báo",
                    subtitle = "Nhắc nhở lịch mặc đồ & tin tức",
                    checked = notiEnabled,
                    onCheckedChange = { notiEnabled = it }
                )
            }
            item {
                SettingSwitchItem(
                    title = "Dịch vụ vị trí",
                    subtitle = "Để gợi ý thời tiết chính xác",
                    checked = locationEnabled,
                    onCheckedChange = { locationEnabled = it }
                )
            }

            // Phần 2: Tài khoản
            item { SectionTitle("Tài khoản") }
            item { SettingArrowItem(title = "Chỉnh sửa hồ sơ") }
            item { SettingArrowItem(title = "Đổi mật khẩu") }
            item { SettingArrowItem(title = "Ngôn ngữ", value = "Tiếng Việt") }

            // Phần 3: Khác
            item { SectionTitle("Khác") }
            item { SettingArrowItem(title = "Điều khoản sử dụng") }
            item { SettingArrowItem(title = "Chính sách bảo mật") }
            item {
                Text(
                    "Phiên bản 1.0.0",
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6200EE),
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, fontSize = 12.sp, color = Color.Gray) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        },
        colors = ListItemDefaults.colors(containerColor = Color.White)
    )
    HorizontalDivider(color = Color.LightGray.copy(0.2f))
}

@Composable
fun SettingArrowItem(title: String, value: String? = null) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    Text(value, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
            }
        },
        modifier = Modifier.clickable { },
        colors = ListItemDefaults.colors(containerColor = Color.White)
    )
    HorizontalDivider(color = Color.LightGray.copy(0.2f))
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}