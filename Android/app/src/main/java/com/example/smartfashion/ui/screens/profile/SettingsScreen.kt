package com.example.smartfashion.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {}
) {
    var notiEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cài đặt",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            item { SectionTitle("Chung") }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SecWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingSwitchItem(
                            title = "Thông báo",
                            subtitle = "Nhắc nhở lịch mặc đồ & tin tức",
                            checked = notiEnabled,
                            onCheckedChange = { notiEnabled = it }
                        )
                        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingSwitchItem(
                            title = "Dịch vụ vị trí",
                            subtitle = "Để gợi ý thời tiết chính xác",
                            checked = locationEnabled,
                            onCheckedChange = { locationEnabled = it }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { SectionTitle("Tài khoản") }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SecWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingArrowItem(title = "Chỉnh sửa hồ sơ")
                        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingArrowItem(title = "Đổi mật khẩu")
                        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingArrowItem(title = "Ngôn ngữ", value = "Tiếng Việt")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { SectionTitle("Khác") }
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SecWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        SettingArrowItem(title = "Điều khoản sử dụng")
                        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 16.dp))
                        SettingArrowItem(title = "Chính sách bảo mật")
                    }
                }
            }

            item {
                Text(
                    text = "Phiên bản 1.0.0\nSmartFashion JSC",
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 16.dp),
                    color = TextLightBlue.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextDarkBlue,
        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 8.dp)
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
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextDarkBlue,
                fontSize = 15.sp
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 12.sp,
                color = TextLightBlue
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = TextLightBlue.copy(alpha = 0.3f),
                    uncheckedBorderColor = Color.Transparent
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun SettingArrowItem(title: String, value: String? = null) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextDarkBlue,
                fontSize = 15.sp
            )
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value != null) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 13.sp,
                        color = TextLightBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextLightBlue.copy(alpha = 0.5f))
            }
        },
        modifier = Modifier.clickable { },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}