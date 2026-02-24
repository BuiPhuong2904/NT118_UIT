package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo
val TravelPrimary = Color(0xFF6200EE)

// Model giả lập chuyến đi
data class Trip(
    val id: String,
    val destination: String,
    val date: String,
    val imageUrl: String,
    val totalItems: Int,
    val packedItems: Int,
    val type: TripType
)

enum class TripType(val title: String, val icon: ImageVector) {
    VACATION("Du lịch", Icons.Default.BeachAccess),
    BUSINESS("Công tác", Icons.Default.BusinessCenter),
    OTHER("Khác", Icons.Default.FlightTakeoff)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPlannerScreen(
    onBackClick: () -> Unit = {},
    onTripClick: (String) -> Unit = {} // Bấm vào để xem checklist chi tiết
) {
    // Dữ liệu giả
    val trips = listOf(
        Trip("1", "Đà Nẵng & Hội An", "15/04 - 18/04", "https://i.postimg.cc/9MXZHYtp/3.jpg", 20, 15, TripType.VACATION),
        Trip("2", "Hà Nội (Hội thảo)", "22/05 - 23/05", "https://i.postimg.cc/9MXZHYtp/3.jpg", 8, 0, TripType.BUSINESS)
    )

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Trợ lý Du lịch", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Mở dialog tạo chuyến đi mới */ },
                containerColor = TravelPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Tạo chuyến đi") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. BANNER GỢI Ý (Template)
            item {
                SuggestionBanner()
            }

            // 2. TIÊU ĐỀ
            item {
                Text(
                    "Chuyến đi sắp tới",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // 3. DANH SÁCH CHUYẾN ĐI
            items(trips) { trip ->
                TripCard(trip = trip, onClick = { onTripClick(trip.id) })
            }

            // Padding dưới cùng để không bị FAB che
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SuggestionBanner() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TravelPrimary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Chưa biết mang gì?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Sử dụng mẫu danh sách có sẵn cho đi biển, leo núi hoặc công tác.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = TravelPrimary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Xem mẫu", fontSize = 12.sp)
                }
            }
            Icon(
                imageVector = Icons.Default.BeachAccess,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun TripCard(trip: Trip, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            // Ảnh bìa chuyến đi
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = trip.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Lớp phủ Gradient đen mờ để chữ nổi
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Loại chuyến đi (Badge)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(trip.type.icon, null, modifier = Modifier.size(12.dp), tint = TravelPrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(trip.type.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TravelPrimary)
                    }
                }

                // Địa điểm & Ngày (Nằm đè lên ảnh)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        trip.destination,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.DateRange, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(trip.date, color = Color.White.copy(0.9f), fontSize = 12.sp)
                    }
                }
            }

            // Phần tiến độ (Progress)
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tiến độ chuẩn bị", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "${trip.packedItems}/${trip.totalItems} món",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (trip.packedItems == trip.totalItems) Color(0xFF4CAF50) else Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Thanh Progress Bar
                val progress = if (trip.totalItems > 0) trip.packedItems.toFloat() / trip.totalItems else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progress >= 1f) Color(0xFF4CAF50) else TravelPrimary,
                    trackColor = Color(0xFFEEEEEE),
                )

                if (progress >= 1f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Đã sẵn sàng!", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TravelPlannerPreview() {
    TravelPlannerScreen()
}