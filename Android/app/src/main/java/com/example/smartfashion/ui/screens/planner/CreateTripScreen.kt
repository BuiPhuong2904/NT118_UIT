package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Màu chủ đạo
val CreateTripPrimary = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {}
) {
    // State nhập liệu
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Du lịch") }
    var selectedTransport by remember { mutableStateOf("Máy bay") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tạo chuyến đi mới", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Nút Tạo chuyến đi
            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CreateTripPrimary),
                // Chỉ hiện active khi đã nhập nơi đến
                enabled = destination.isNotEmpty()
            ) {
                Text("Tạo & Gợi ý đồ mang theo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text("Hãy nhập thông tin để SmartFashion gợi ý hành lý chuẩn xác nhất nhé!", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // 1. NHẬP ĐỊA ĐIỂM (Destination)
            InputSectionTitle("Bạn đi đâu?")
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                placeholder = { Text("Ví dụ: Đà Lạt, Paris, Tokyo...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CreateTripPrimary,
                    focusedLabelColor = CreateTripPrimary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. CHỌN NGÀY (Date Range)
            InputSectionTitle("Thời gian?")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Ngày đi
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Ngày đi") },
                    placeholder = { Text("dd/mm") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                    shape = RoundedCornerShape(12.dp)
                )
                // Ngày về
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Ngày về") },
                    placeholder = { Text("dd/mm") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. MỤC ĐÍCH CHUYẾN ĐI (Trip Type) - Quan trọng để gợi ý đồ
            InputSectionTitle("Mục đích chuyến đi?")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val types = listOf(
                    "Du lịch" to Icons.Default.BeachAccess,
                    "Công tác" to Icons.Default.BusinessCenter,
                    "Leo núi" to Icons.Default.Hiking,
                    "Khác" to Icons.Default.LocationOn
                )
                items(types) { (name, icon) ->
                    SelectionCard(
                        text = name,
                        icon = icon,
                        isSelected = selectedType == name,
                        onClick = { selectedType = name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. PHƯƠNG TIỆN (Transport) - Quan trọng để biết giới hạn hành lý
            InputSectionTitle("Di chuyển bằng?")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val transports = listOf(
                    "Máy bay" to Icons.Default.Flight,
                    "Xe hơi" to Icons.Default.DirectionsCar,
                    "Tàu hỏa" to Icons.Default.Train
                )
                items(transports) { (name, icon) ->
                    SelectionCard(
                        text = name,
                        icon = icon,
                        isSelected = selectedTransport == name,
                        onClick = { selectedTransport = name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // Padding đáy
        }
    }
}

@Composable
fun InputSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 12.dp)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) CreateTripPrimary.copy(alpha = 0.1f) else Color(0xFFF5F5F5)
        ),
        modifier = Modifier
            .clickable { onClick() }
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) CreateTripPrimary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(min = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) CreateTripPrimary else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) CreateTripPrimary else Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTripPreview() {
    CreateTripScreen()
}