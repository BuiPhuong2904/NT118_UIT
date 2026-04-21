package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.FlightTakeoff
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

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientAccent
import com.example.smartfashion.ui.theme.GradientAccent3
import com.example.smartfashion.ui.theme.GradientSoft
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

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
    VACATION("Du lịch", Icons.Rounded.BeachAccess),
    BUSINESS("Công tác", Icons.Rounded.BusinessCenter),
    OTHER("Khác", Icons.Rounded.FlightTakeoff)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelPlannerScreen(
    onBackClick: () -> Unit = {},
    onTripClick: (String) -> Unit = {},
    onCreateTripClick: () -> Unit = {}
) {
    val trips = listOf(
        Trip("1", "Đà Nẵng & Hội An", "15 Th04 - 18 Th04", "https://i.postimg.cc/9MXZHYtp/3.jpg", 20, 15, TripType.VACATION),
        Trip("2", "Hà Nội (Hội thảo)", "22 Th05 - 23 Th05", "https://i.postimg.cc/9MXZHYtp/3.jpg", 8, 8, TripType.BUSINESS),
        Trip("3", "Sapa (Leo núi)", "10 Th10 - 14 Th10", "https://i.postimg.cc/9MXZHYtp/3.jpg", 15, 2, TripType.OTHER)
    )

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Trợ lý Du lịch",
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
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(GradientAccent3)
                    .clickable { onCreateTripClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tạo chuyến đi",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. BANNER GỢI Ý
            item {
                SuggestionBanner()
            }

            // 2. TIÊU ĐỀ
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Chuyến đi sắp tới",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextBlue,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            // 3. DANH SÁCH CHUYẾN ĐI
            items(trips) { trip ->
                TripCardLookbook(trip = trip, onClick = { onTripClick(trip.id) })
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun SuggestionBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(GradientSoft)
            .clickable {  }
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Chưa biết mang gì?",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sử dụng mẫu danh sách có sẵn cho đi biển, leo núi hoặc công tác.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "Xem mẫu ngay",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPink,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.BeachAccess,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.25f),
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 16.dp, y = 16.dp)
            )
        }
    }
}

@Composable
fun TripCardLookbook(trip: Trip, onClick: () -> Unit) {
    val progress = if (trip.totalItems > 0) trip.packedItems.toFloat() / trip.totalItems else 0f
    val isDone = progress >= 1f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = trip.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(0.5f to Color.Transparent, 1f to Color.Black.copy(0.5f)))
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(trip.type.icon, null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(trip.type.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color.White, fontSize = 10.sp)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Text(
                    text = trip.destination,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextDarkBlue,
                    fontSize = 18.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DateRange, null, tint = TextLightBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = trip.date,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextLightBlue,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Đã chuẩn bị", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = TextLightBlue)
                    Text(
                        text = "${trip.packedItems}/${trip.totalItems}",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 13.sp,
                        color = if (isDone) Color(0xFF4CAF50) else AccentBlue
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(BgLight)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .then(
                                if (isDone) Modifier.background(Color(0xFF4CAF50))
                                else Modifier.background(GradientAccent)
                            )
                    )
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