package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientAccent3
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

data class DayPlan(
    val dayNumber: Int,
    val dateStr: String,
    val location: String,
    val weatherTemp: String,
    val isSunny: Boolean,
    val outfitImageUrl: String? = null
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TripDetailScreen(
    onBackClick: () -> Unit = {},
    onAddOutfitClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Trang phục", "Checklist")

    val dayPlans = listOf(
        DayPlan(1, "15 Th04 • T5", "Đà Nẵng", "33 / 26°C", isSunny = true, outfitImageUrl = "https://i.postimg.cc/9MXZHYtp/3.jpg"),
        DayPlan(2, "16 Th04 • T6", "Hội An", "31 / 25°C", isSunny = false, outfitImageUrl = null),
        DayPlan(3, "17 Th04 • T7", "Đà Nẵng", "32 / 24°C", isSunny = true, outfitImageUrl = null)
    )

    Scaffold(
        containerColor = BgLight,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(GradientAccent3)
                    .clickable {  },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm mới",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                TripHeroHeader(onBackClick)
            }

            stickyHeader {
                Surface(
                    color = SecWhite,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = SecWhite,
                        contentColor = AccentBlue,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = AccentBlue,
                                height = 3.dp
                            )
                        },
                        divider = {
                            HorizontalDivider(color = BgLight, thickness = 2.dp)
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (selectedTab == index) TextPink else TextLightBlue.copy(alpha = 0.7f),
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                interactionSource = null
                            )
                        }
                    }
                }
            }

            when (selectedTab) {
                0 -> {
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    itemsIndexed(dayPlans) { index, plan ->
                        DayOutfitItem(
                            plan = plan,
                            isLastItem = index == dayPlans.size - 1,
                            onAddClick = onAddOutfitClick
                        )
                    }
                }
                1 -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("Checklist tiến độ vali", color = TextLightBlue, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun TripHeroHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        AsyncImage(
            model = "https://res.cloudinary.com/dna9qbejm/image/upload/v1772213478/xe-tam-ky-hoi-an-banner_bsoc2r.jpg",
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.3f to Color.Transparent,
                        1f to Color.Black.copy(0.9f)
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = CircleShape,
                color = SecWhite.copy(alpha = 0.8f),
                shadowElevation = 2.dp,
                modifier = Modifier.size(44.dp).clickable { onBackClick() }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDarkBlue, modifier = Modifier.padding(10.dp))
            }

            Surface(
                shape = CircleShape,
                color = SecWhite.copy(alpha = 0.8f),
                shadowElevation = 2.dp,
                modifier = Modifier.size(44.dp).clickable { /* Menu Options */ }
            ) {
                Icon(Icons.Default.MoreVert, "Menu", tint = TextDarkBlue, modifier = Modifier.padding(10.dp))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Đà Nẵng & Hội An",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CalendarMonth, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("15 Th04 - 18 Th04", color = Color.White.copy(0.9f), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Xuất phát từ Hồ Chí Minh", color = Color.White.copy(0.9f), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Outlined.NoteAdd, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thêm ghi chú...", color = Color.White.copy(0.6f), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun DayOutfitItem(plan: DayPlan, isLastItem: Boolean, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = AccentBlue.copy(alpha = 0.2f),
                modifier = Modifier.size(24.dp).padding(top = 4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AccentBlue))
                }
            }

            if (!isLastItem) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(AccentBlue.copy(alpha = 0.3f))
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, bottom = 32.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Ngày ${plan.dayNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPink,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("•", color = TextLightBlue, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(plan.dateStr, color = TextDarkBlue, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.LocationOn, null, tint = TextLightBlue.copy(0.8f), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(plan.location, color = TextDarkBlue, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.width(16.dp))

                val weatherIcon = if (plan.isSunny) Icons.Outlined.WbSunny else Icons.Outlined.CloudQueue
                Icon(weatherIcon, null, tint = if (plan.isSunny) Color(0xFFFFB300) else AccentBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(plan.weatherTemp, color = TextDarkBlue, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (plan.outfitImageUrl == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(SecWhite, RoundedCornerShape(16.dp))
                        .drawBehind {
                            drawRoundRect(
                                color = AccentBlue.copy(alpha = 0.5f),
                                style = Stroke(
                                    width = 3f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                ),
                                cornerRadius = CornerRadius(16.dp.toPx())
                            )
                        }
                        .clickable { onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = AccentBlue.copy(alpha = 0.1f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Thêm", tint = AccentBlue, modifier = Modifier.padding(4.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Thêm trang phục cho ngày ${plan.dayNumber}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SecWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = plan.outfitImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Set đi biển sáng", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("3 món đồ", style = MaterialTheme.typography.bodyLarge, color = TextLightBlue, fontSize = 12.sp)
                        }
                        Surface(
                            shape = CircleShape,
                            color = BgLight,
                            modifier = Modifier.size(36.dp).clickable { }
                        ) {
                            Icon(Icons.Default.Add, null, tint = TextLightBlue, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripDetailMinimalPreview() {
    TripDetailScreen()
}