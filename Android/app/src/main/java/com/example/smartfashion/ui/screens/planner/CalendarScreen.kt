package com.example.smartfashion.ui.screens.planner

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.runtime.saveable.rememberSaveable

import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.model.Schedule
import com.example.smartfashion.ui.components.BottomNavigationBar
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientSoft
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.SoftPink
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userId = tokenManager.getUserId()

    // State lắng nghe dữ liệu từ ViewModel
    val selectedDate by viewModel.selectedDate.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val plannedDays by viewModel.plannedDays.collectAsState()
    val dailySchedules by viewModel.dailySchedules.collectAsState()

    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var selectedScheduleToView by remember { mutableStateOf<Schedule?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Khởi tạo load dữ liệu khi vào màn hình
    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.initData(userId)
        }
    }

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
                CalendarHeader()
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController, selectedItem = 3) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight)
                .padding(
                    top = paddingValues.calculateTopPadding() + 10.dp,
                    start = 20.dp,
                    end = 20.dp
                )
        ) {
            MonthSelector(
                currentMonth = currentMonth,
                onPrev = { viewModel.changeMonth(-1) },
                onNext = { viewModel.changeMonth(1) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = SecWhite),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    CalendarGrid(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        plannedDays = plannedDays,
                        onDateSelected = { day -> viewModel.selectDate(day) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutfitScheduleHeader(
                date = selectedDate.dayOfMonth,
                onAddClick = {
                    showBottomSheet = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (dailySchedules.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Hôm nay chưa có kế hoạch lên đồ nào 😊", color = TextLightBlue)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(dailySchedules) { schedule ->
                        OutfitCard(
                            schedule = schedule,
                            onClick = { selectedScheduleToView = schedule }
                        )
                    }
                }
            }
        }
    }

    // Bottom Sheet: THÊM LỊCH MỚI
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            ScheduleBottomSheet(
                navController = navController,
                onDismiss = { showBottomSheet = false },
                onSaveSuccess = {
                    showBottomSheet = false
                    viewModel.initData(userId)
                }
            )
        }
    }

    if (selectedScheduleToView != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedScheduleToView = null },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            dragHandle = null
        ) {
            ViewScheduleBottomSheet(
                schedule = selectedScheduleToView!!,
                viewModel = viewModel,
                onDismiss = { selectedScheduleToView = null },
                onDeleteSuccess = {
                    selectedScheduleToView = null
                    viewModel.initData(userId)
                }
            )
        }
    }
}

@Composable
fun CalendarHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Lịch trình",
                style = MaterialTheme.typography.titleLarge.copy(
                    brush = GradientText
                ),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Kế hoạch mặc đẹp mỗi ngày",
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

@Composable
fun MonthSelector(currentMonth: YearMonth, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecWhite, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrev, modifier = Modifier.size(24.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = TextBlue)
        }
        Text(
            text = "Tháng ${currentMonth.monthValue}, ${currentMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            color = TextDarkBlue
        )
        IconButton(onClick = onNext, modifier = Modifier.size(24.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = TextBlue)
        }
    }
}

@Composable
fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    plannedDays: List<Int>,
    onDateSelected: (Int) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value
    val startOffset = if (firstDayOfWeek == 7) 0 else firstDayOfWeek
    val today = LocalDate.now()

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextLightBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(280.dp),
            userScrollEnabled = false
        ) {
            items(startOffset) { Spacer(modifier = Modifier) }

            items(daysInMonth) { index ->
                val day = index + 1
                val isSelected = (selectedDate.monthValue == currentMonth.monthValue) && (selectedDate.dayOfMonth == day)
                val isToday = (today.year == currentMonth.year) && (today.monthValue == currentMonth.monthValue) && (today.dayOfMonth == day)
                val hasPlan = plannedDays.contains(day)

                DayCell(
                    day = day,
                    isSelected = isSelected,
                    isToday = isToday,
                    hasPlan = hasPlan,
                    onClick = { onDateSelected(day) }
                )
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    hasPlan: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .then(
                if (isSelected) Modifier.background(brush = GradientSoft)
                else Modifier.background(Color.Transparent)
            )
            .border(
                width = 1.5.dp,
                color = if (isToday && !isSelected) SoftPink else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = if (isSelected) SecWhite else TextDarkBlue,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium
        )

        if (hasPlan) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SecWhite else AccentBlue)
            )
        }
    }
}

@Composable
fun OutfitScheduleHeader(date: Int, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Lịch trình ngày $date",
            style = MaterialTheme.typography.titleMedium,
            color = TextDarkBlue,
            fontSize = 16.sp
        )

        Surface(
            color = AccentBlue.copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onAddClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Thêm lịch",
                    tint = AccentBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Thêm",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AccentBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OutfitCard(schedule: Schedule, onClick: () -> Unit) {
    val timeFormatted = try {
        val dateStr = schedule.date
        if (!dateStr.isNullOrEmpty() && dateStr.length >= 16) {
            dateStr.substring(11, 16)
        } else {
            "08:00"
        }
    } catch (e: Exception) {
        "08:00"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(60.dp)
                    .fillMaxHeight()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    color = AccentBlue
                )
            }

            AsyncImage(
                model = schedule.outfitInfo?.image_preview_url ?: "https://i.postimg.cc/9MXZHYtp/3.jpg",
                contentDescription = null,
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, top = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = schedule.eventName?.takeIf { it.isNotBlank() }
                        ?: schedule.outfitInfo?.name
                        ?: "Lịch trình cá nhân",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = TextBlue,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    schedule.outfitInfo?.tagNames?.take(2)?.forEach { tag ->
                        Surface(
                            color = AccentBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AccentBlue,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}