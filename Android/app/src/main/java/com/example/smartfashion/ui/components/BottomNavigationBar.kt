package com.example.smartfashion.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.GradientAccent3
import com.example.smartfashion.ui.theme.PrimaryCyan
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.SoftBlue

@Composable
fun BottomNavigationBar(
    navController: NavController,
    selectedItem: Int             // Nhận biết tab nào đang được chọn (0: Home, 1: Tủ đồ...)
) {
    val items = listOf("Trang chủ", "Tủ đồ", "Phối đồ", "Lịch", "Tài khoản")
    val selectedIcons = listOf(Icons.Rounded.Home, Icons.Rounded.Checkroom, Icons.Rounded.Add, Icons.Rounded.CalendarMonth, Icons.Rounded.Person)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.Checkroom, Icons.Outlined.Add, Icons.Outlined.CalendarMonth, Icons.Outlined.Person)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = 0.99f },
        contentAlignment = Alignment.BottomCenter
    ) {
        // 1. Thanh ngang
        Surface(
            color = SecWhite,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .drawBehind {
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 2f
                    )
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Trang chủ
                BottomNavItem(0, items[0], selectedIcons[0], unselectedIcons[0], selectedItem, Modifier.weight(1f)) {
                    navController.navigate("home_screen") {
                        popUpTo("home_screen")
                        launchSingleTop = true
                    }
                }

                // Tủ đồ
                BottomNavItem(1, items[1], selectedIcons[1], unselectedIcons[1], selectedItem, Modifier.weight(1f)) {
                    navController.navigate("closet_screen") {
                        popUpTo("home_screen")
                        launchSingleTop = true
                    }
                }

                // Cột Phối đồ
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                navController.navigate("saved_outfits_screen") {
                                    popUpTo("home_screen")
                                    launchSingleTop = true
                                }
                            }
                        )
                ) {
                    Spacer(modifier = Modifier.size(24.dp))
                    Text(
                        text = items[2],
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 11.sp,
                        color = if (selectedItem == 2) PrimaryCyan else SoftBlue,
                        fontWeight = if (selectedItem == 2) FontWeight.Bold else FontWeight.Medium
                    )
                }

                BottomNavItem(3, items[3], selectedIcons[3], unselectedIcons[3], selectedItem, Modifier.weight(1f)) {
                    navController.navigate("calendar_screen") {
                        popUpTo("home_screen")
                        launchSingleTop = true
                    }
                }

                BottomNavItem(4, items[4], selectedIcons[4], unselectedIcons[4], selectedItem, Modifier.weight(1f)) {
                    navController.navigate("profile_screen") {
                        popUpTo("home_screen")
                        launchSingleTop = true
                    }
                }
            }
        }

        // 2. Lỗ khoét
        Spacer(
            modifier = Modifier
                .padding(bottom = 40.dp)
                .size(60.dp)
                .drawWithCache {
                    onDrawBehind {
                        drawCircle(color = Color.Black, blendMode = BlendMode.Clear)
                    }
                }
        )

        // 3. Nút FAB (+)
        Box(
            modifier = Modifier
                .padding(bottom = 46.dp)
                .size(48.dp)
                .background(brush = GradientAccent3, shape = CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        // ĐÃ THÊM LỆNH CHUYỂN TRANG Ở ĐÂY
                        navController.navigate("saved_outfits_screen") {
                            popUpTo("home_screen")
                            launchSingleTop = true
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Rounded.Add, contentDescription = items[2], tint = SecWhite, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun BottomNavItem(
    index: Int, title: String, selectedIcon: ImageVector, unselectedIcon: ImageVector,
    selectedItem: Int, modifier: Modifier = Modifier, onClick: () -> Unit
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
            tint = if (isSelected) AccentBlue else SoftBlue,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 11.sp,
            color = if (isSelected) AccentBlue else SoftBlue,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}