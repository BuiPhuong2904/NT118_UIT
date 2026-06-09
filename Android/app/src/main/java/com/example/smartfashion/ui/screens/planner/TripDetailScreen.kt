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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartfashion.ui.theme.*
import com.example.smartfashion.ui.viewmodel.TripDetailViewModel
import com.example.smartfashion.model.PackingItem
import java.time.format.DateTimeFormatter
import androidx.navigation.NavController
import androidx.compose.material3.Checkbox
import com.example.smartfashion.data.api.DayPlan

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TripDetailScreen(
    navController: NavController,
    tripId: String,
    onBackClick: () -> Unit = {},
    onAddOutfitClick: (DayPlan) -> Unit = {},
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val trip = viewModel.trip
    val isLoading = viewModel.isLoading
    val tabs = listOf("Trang phục", "Checklist")

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    val selectedOutfitId = savedStateHandle?.get<Int>("selectedOutfitId")
    val selectedOutfitImage = savedStateHandle?.get<String>("selectedOutfitImage")
    val selectedDay = savedStateHandle?.get<Int>("selectedDay")

    LaunchedEffect(selectedOutfitId, selectedDay, selectedOutfitImage){
        if (selectedOutfitId != null && selectedDay != null && selectedOutfitImage != null) {
            viewModel.assignOutfitToDay(
                selectedDay,
                selectedOutfitId,
                selectedOutfitImage
            )

            savedStateHandle?.remove<Int>("selectedOutfitId")
            savedStateHandle?.remove<String>("selectedOutfitImage")
            savedStateHandle?.remove<Int>("selectedDay")
        }
    }

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId.toIntOrNull() ?: 0)
    }

    val dayPlans = viewModel.dayPlans

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentBlue)
        }
    } else {
        Scaffold(
            containerColor = BgLight
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                item {
                    // Hàm convert chuỗi ISO từ Server thành định dạng dd/MM/yyyy ngắn gọn
                    val formatDisplayDate = { isoString: String ->
                        try {
                            if (isoString.isNotBlank()) {
                                val datePart = isoString.substringBefore("T")
                                val parts = datePart.split("-")
                                if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else isoString
                            } else ""
                        } catch (e: Exception) {
                            isoString
                        }
                    }

                    TripHeroHeader(
                        title = trip?.destination ?: "Chuyến đi",
                        startDate = formatDisplayDate(trip?.start_date ?: ""),
                        endDate = formatDisplayDate(trip?.end_date ?: ""),
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
                            onAddClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("selectedDay", plan.dayNumber)
                                onAddOutfitClick(plan)
                            }
                        )
                    }
                } else {
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    itemsIndexed(viewModel.packingItems) { _, item ->
                        PackingChecklistItem(
                            item = item,
                            onToggle = { viewModel.togglePacked(item.id) }
                        )
                    }

                    if (viewModel.packingItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Đang tạo checklist AI...", color = TextLightBlue)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

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

            if (plan.outfitImageUrl.isNullOrEmpty()) {
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
                            model = plan.outfitImageUrl ?: "",
                            contentDescription = null,
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                            Text("Set trang phục đã chọn", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 14.sp)
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

@Composable
fun PackingChecklistItem(
    item: PackingItem,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isPacked,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = item.name,
                    color = TextDarkBlue,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.category,
                    color = TextLightBlue,
                    fontSize = 12.sp
                )
            }
        }
    }
}