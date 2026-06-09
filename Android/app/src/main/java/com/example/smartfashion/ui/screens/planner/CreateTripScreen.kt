package com.example.smartfashion.ui.screens.planner

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    onBackClick: () -> Unit = {},
    onCreateClick: (Int) -> Unit = {},
    viewModel: TravelViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var destination by remember { mutableStateOf("") }

    // State lưu trữ ngày tháng
    var apiStartDate by remember { mutableStateOf("") }
    var apiEndDate by remember { mutableStateOf("") }
    var displayDateRange by remember { mutableStateOf("") }

    // State hiển thị DateRangePicker
    var showDatePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    var selectedType by remember { mutableStateOf("Du lịch") }
    var customType by remember { mutableStateOf("") } // Biến lưu mục đích khi chọn "Khác"

    var selectedTransport by remember { mutableStateOf("Máy bay") }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tạo chuyến đi",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextDarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            val isEnabled = destination.isNotBlank() && apiStartDate.isNotEmpty() && apiEndDate.isNotEmpty() && !isLoading
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = {
                        // Nếu chọn "Khác" thì lấy chữ người dùng tự gõ, không thì lấy cái có sẵn
                        val finalTripType = if (selectedType == "Khác" && customType.isNotBlank()) customType else selectedType

                        viewModel.createTrip(
                            destination = destination,
                            startDate = apiStartDate,
                            endDate = apiEndDate,
                            tripType = finalTripType,
                            transport = selectedTransport,
                            onSuccess = { newId -> onCreateClick(newId) }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            brush = if (isEnabled) GradientText else Brush.horizontalGradient(listOf(Color.LightGray, Color.LightGray)),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    enabled = isEnabled
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Bắt đầu chuẩn bị", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            AiTipBanner()

            Spacer(modifier = Modifier.height(28.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = TextLightBlue.copy(alpha = 0.3f),
                focusedLabelColor = AccentBlue,
                unfocusedLabelColor = TextLightBlue,
                focusedTextColor = TextDarkBlue,
                unfocusedTextColor = TextDarkBlue,
                cursorColor = AccentBlue,
                focusedContainerColor = SecWhite,
                unfocusedContainerColor = SecWhite,
                disabledContainerColor = SecWhite,
                disabledTextColor = TextDarkBlue,
                disabledBorderColor = TextLightBlue.copy(alpha = 0.3f)
            )

            InputSectionTitle("Bạn dự định đi đâu?")
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                placeholder = { Text("Ví dụ: Đà Lạt, Bali, Paris...", color = TextLightBlue.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = AccentBlue) },
                trailingIcon = {
                    if (destination.isNotEmpty()) {
                        IconButton(onClick = { destination = "" }) {
                            Icon(Icons.Rounded.Clear, null, tint = TextLightBlue)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            InputSectionTitle("Lịch trình của bạn?")
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = displayDateRange,
                    onValueChange = { },
                    placeholder = { Text("Chọn ngày đi - ngày về", color = TextLightBlue.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.CalendarMonth, null, tint = AccentBlue) },
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    readOnly = true,
                    enabled = false
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ================= MỤC ĐÍCH (TRENDY) =================
            InputSectionTitle("Mục đích chuyến đi?")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val types = listOf(
                    "Du lịch" to Icons.Default.BeachAccess,
                    "Nghỉ dưỡng" to Icons.Default.Spa,
                    "Công tác" to Icons.Default.BusinessCenter,
                    "Sống ảo" to Icons.Default.CameraAlt,
                    "Leo núi" to Icons.Default.Hiking,
                    "Phượt" to Icons.Default.Motorcycle,
                    "Khác" to Icons.Default.MoreHoriz
                )
                items(types) { (name, icon) ->
                    SelectionCard(text = name, icon = icon, isSelected = selectedType == name, onClick = { selectedType = name })
                }
            }

            // Hiệu ứng vuốt xuống (Slide Down) khi chọn "Khác"
            AnimatedVisibility(
                visible = selectedType == "Khác",
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    OutlinedTextField(
                        value = customType,
                        onValueChange = { customType = it },
                        placeholder = { Text("Nhập mục đích (VD: Xem Concert, Đi quẩy...)", color = TextLightBlue.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Rounded.EditNote, null, tint = AccentBlue) },
                        shape = RoundedCornerShape(16.dp),
                        colors = textFieldColors,
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ================= PHƯƠNG TIỆN =================
            InputSectionTitle("Di chuyển bằng?")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val transports = listOf(
                    "Máy bay" to Icons.Default.Flight,
                    "Xe khách" to Icons.Default.DirectionsBus,
                    "Xe hơi" to Icons.Default.DirectionsCar,
                    "Tàu hỏa" to Icons.Default.Train,
                    "Xe máy" to Icons.Default.TwoWheeler,
                    "Du thuyền" to Icons.Default.DirectionsBoat
                )
                items(transports) { (name, icon) ->
                    SelectionCard(text = name, icon = icon, isSelected = selectedTransport == name, onClick = { selectedTransport = name })
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

        // ================= DATE RANGE PICKER DIALOG CUSTOMIZE =================
        if (showDatePicker) {
            val viLocale = Locale("vi", "VN")
            val configuration = Configuration(LocalConfiguration.current).apply {
                setLocale(viLocale)
            }

            CompositionLocalProvider(LocalConfiguration provides configuration) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    colors = DatePickerDefaults.colors(containerColor = SecWhite),
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                                val startMillis = dateRangePickerState.selectedStartDateMillis
                                val endMillis = dateRangePickerState.selectedEndDateMillis

                                if (startMillis != null && endMillis != null) {
                                    val formatterApi = SimpleDateFormat("yyyy-MM-dd", viLocale).apply { timeZone = TimeZone.getTimeZone("UTC") }
                                    val formatterDisplay = SimpleDateFormat("dd/MM/yyyy", viLocale).apply { timeZone = TimeZone.getTimeZone("UTC") }
                                    val formatShort = SimpleDateFormat("dd/MM", viLocale).apply { timeZone = TimeZone.getTimeZone("UTC") }

                                    apiStartDate = formatterApi.format(Date(startMillis))
                                    apiEndDate = formatterApi.format(Date(endMillis))

                                    displayDateRange = "${formatShort.format(Date(startMillis))} - ${formatterDisplay.format(Date(endMillis))}"
                                }
                            },
                            enabled = dateRangePickerState.selectedEndDateMillis != null
                        ) {
                            Text("LƯU", fontWeight = FontWeight.Bold, color = AccentBlue)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("HỦY", color = TextLightBlue)
                        }
                    }
                ) {
                    DateRangePicker(
                        state = dateRangePickerState,
                        title = {
                            Text(
                                text = "CHỌN LỊCH TRÌNH",
                                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
                                color = AccentBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        headline = {
                            DateRangePickerDefaults.DateRangePickerHeadline(
                                selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                                selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                                displayMode = dateRangePickerState.displayMode,
                                dateFormatter = DatePickerDefaults.dateFormatter(),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        },
                        showModeToggle = false,
                        modifier = Modifier.weight(1f),
                        colors = DatePickerDefaults.colors(
                            containerColor = SecWhite,
                            dayContentColor = TextDarkBlue,
                            weekdayContentColor = TextLightBlue,
                            selectedDayContainerColor = AccentBlue,
                            selectedDayContentColor = Color.White,
                            dayInSelectionRangeContainerColor = AccentBlue.copy(alpha = 0.15f),
                            dayInSelectionRangeContentColor = TextDarkBlue,
                            todayContentColor = TextPink,
                            todayDateBorderColor = TextPink,
                            yearContentColor = TextDarkBlue,
                            selectedYearContainerColor = AccentBlue,
                            selectedYearContentColor = Color.White,
                            currentYearContentColor = TextPink
                        )
                    )
                }
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun AiTipBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F0FF), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = TextPink, modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "AI Stylist sẽ phân tích thời tiết tại điểm đến để mix match hành lý hoàn hảo cho bạn!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextDarkBlue.copy(alpha = 0.8f),
            lineHeight = 20.sp
        )
    }
}

@Composable
fun InputSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextDarkBlue,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

@Composable
fun SelectionCard(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AccentBlue.copy(alpha = 0.1f) else SecWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp),
        modifier = Modifier
            .border(
                width = 1.5.dp,
                color = if (isSelected) AccentBlue else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .widthIn(min = 68.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AccentBlue else TextLightBlue,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AccentBlue else TextLightBlue
            )
        }
    }
}