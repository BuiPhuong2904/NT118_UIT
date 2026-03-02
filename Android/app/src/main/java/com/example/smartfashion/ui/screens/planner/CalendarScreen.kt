package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {
    var selectedDate by remember { mutableIntStateOf(15) }
    val plannedDays = listOf(5, 12, 15, 20, 24)

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
            MonthSelector()

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = SecWhite),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    CalendarGrid(
                        selectedDate = selectedDate,
                        plannedDays = plannedDays,
                        onDateSelected = { selectedDate = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutfitScheduleHeader(
                date = selectedDate,
                onAddClick = {
                    showBottomSheet = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutfitCard(
                        time = "08:00",
                        title = "Đi làm văn phòng",
                        tags = listOf("Lịch sự", "Thoải mái"),
                        imageUrl = "https://i.postimg.cc/9MXZHYtp/3.jpg"
                    )
                }

                item {
                    OutfitCard(
                        time = "19:00",
                        title = "Hẹn hò tối",
                        tags = listOf("Sang trọng", "Mát mẻ"),
                        imageUrl = "https://i.postimg.cc/9MXZHYtp/3.jpg"
                    )
                }
            }
        }
    }

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
                onSave = {
                    showBottomSheet = false
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
fun MonthSelector() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecWhite, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Prev", tint = TextBlue)
        }
        Text(
            text = "Tháng 2, 2026",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            color = TextDarkBlue
        )
        IconButton(onClick = {}, modifier = Modifier.size(24.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = TextBlue)
        }
    }
}

@Composable
fun CalendarGrid(
    selectedDate: Int,
    plannedDays: List<Int>,
    onDateSelected: (Int) -> Unit
) {
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
            modifier = Modifier.height(260.dp),
            userScrollEnabled = false
        ) {
            items(2) { Spacer(modifier = Modifier) }

            items(28) { index ->
                val day = index + 1
                val isSelected = day == selectedDate
                val hasPlan = plannedDays.contains(day)
                val isToday = day == 12

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
            text = "Lịch trình ngày $date/02",
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
fun OutfitCard(
    time: String,
    title: String,
    tags: List<String>,
    imageUrl: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
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
                    text = time,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 16.sp,
                    color = AccentBlue
                )
            }

            AsyncImage(
                model = imageUrl,
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = TextBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    tags.forEach { tag ->
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

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen(navController = rememberNavController())
}