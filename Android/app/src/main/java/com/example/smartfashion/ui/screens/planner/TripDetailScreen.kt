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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartfashion.ui.theme.*
import com.example.smartfashion.ui.viewmodel.TripDetailViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// ======================= DATA MODEL FOR UI =======================
data class DayPlan(
    val dayNumber: Int,
    val date: LocalDate,
    val location: String,
    val weatherTemp: String,
    val isSunny: Boolean,
    val outfitImageUrl: String? = null
)

// ======================= MAIN SCREEN =======================
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TripDetailScreen(
    tripId: String, // Nhận tripId là String từ Navigation
    onBackClick: () -> Unit = {},
    onAddOutfitClick: (DayPlan) -> Unit = {},
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    // 1. Khai báo các biến State ở cấp độ cao nhất của hàm
    var selectedTab by remember { mutableIntStateOf(0) }
    val trip = viewModel.trip
    val isLoading = viewModel.isLoading
    val tabs = listOf("Trang phục", "Checklist")

    // Gọi API từ Backend
    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId.toIntOrNull() ?: 0)
    }

    // 2. LOGIC TỰ ĐỘNG TÍNH TOÁN NGÀY (Nằm ngoài mọi khối if/else)
    val dayPlans = viewModel.dayPlans

    // 3. Xử lý hiển thị UI
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentBlue)
        }
    } else {
        Scaffold(
            containerColor = BgLight,
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(GradientAccent3)
                        .clickable { /* Action thêm mới */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                item {
                    TripHeroHeader(
                        title = trip?.destination ?: "Chuyến đi",
                        startDate = trip?.start_date ?: "",
                        endDate = trip?.end_date ?: "",
                        onBack = onBackClick
                    )
                }

                stickyHeader {
                    Surface(color = SecWhite, shadowElevation = 2.dp) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = SecWhite,
                            contentColor = AccentBlue,
                            indicator = { tabPositions ->
                                SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = AccentBlue,
                                    height = 3.dp
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(title, color = if (selectedTab == index) TextPink else TextLightBlue)
                                    }
                                )
                            }
                        }
                    }
                }

                if (selectedTab == 0) {
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    itemsIndexed(dayPlans) { index, plan ->
                        DayOutfitItem(
                            plan = plan,
                            isLastItem = index == dayPlans.size - 1,
                            onAddClick = { onAddOutfitClick(plan) }
                        )
                    }
                } else {
                    item {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("Checklist đang chuẩn bị...", color = TextLightBlue)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

// ======================= SUB-COMPONENTS =======================

@Composable
fun DayOutfitItem(plan: DayPlan, isLastItem: Boolean, onAddClick: () -> Unit) {
    val displayDate = remember(plan.date) {
        val formatter = DateTimeFormatter.ofPattern("dd 'Th' MM")
        plan.date.format(formatter)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(IntrinsicSize.Min),
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
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(AccentBlue.copy(alpha = 0.3f)))
            }
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp, bottom = 32.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ngày ${plan.dayNumber}", color = TextPink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("  •  ", color = TextLightBlue, fontSize = 16.sp)
                Text(displayDate, color = TextDarkBlue, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = TextLightBlue.copy(0.8f), modifier = Modifier.size(16.dp))
                Text(" ${plan.location}   ", color = TextDarkBlue, fontSize = 14.sp)
                
                val weatherIcon = if (plan.isSunny) Icons.Outlined.WbSunny else Icons.Outlined.CloudQueue
                Icon(weatherIcon, null, tint = if (plan.isSunny) Color(0xFFFFB300) else AccentBlue, modifier = Modifier.size(16.dp))
                Text(" ${plan.weatherTemp}", color = TextDarkBlue, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (plan.outfitImageUrl == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(SecWhite, RoundedCornerShape(16.dp))
                        .drawBehind {
                            drawRoundRect(
                                color = AccentBlue.copy(alpha = 0.4f),
                                style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)),
                                cornerRadius = CornerRadius(16.dp.toPx())
                            )
                        }
                        .clickable { onAddClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                        Text(" Thêm trang phục ngày ${plan.dayNumber}", color = AccentBlue, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SecWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = plan.outfitImageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                            Text("Set trang phục đã chọn", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 14.sp)
                            Text("Phù hợp thời tiết", color = TextLightBlue, fontSize = 12.sp)
                        }
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.MoreVert, null, tint = TextLightBlue)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripHeroHeader(title: String, startDate: String, endDate: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        AsyncImage(
            model = "https://res.cloudinary.com/dna9qbejm/image/upload/v1772213478/xe-tam-ky-hoi-an-banner_bsoc2r.jpg",
            contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f)))))
        
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CalendarMonth, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(16.dp))
                Text(" $startDate - $endDate", color = Color.White.copy(0.9f))
            }
        }
        
        IconButton(
            onClick = onBack, 
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TripDetailMinimalPreview() {
    TripDetailScreen(tripId = "1")
}