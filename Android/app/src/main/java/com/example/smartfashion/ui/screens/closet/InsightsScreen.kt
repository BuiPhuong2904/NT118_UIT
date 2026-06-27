package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext

import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.data.api.InsightsData
import com.example.smartfashion.data.api.EventData
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.PrimaryCyan
import com.example.smartfashion.ui.theme.PrimaryPink
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@Composable
fun InsightsScreen(
    navController: NavController,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userId = remember { TokenManager(context).getUserId() }

    val insightsData by viewModel.insightsData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.fetchInsights(userId)
        }
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Phân tích tủ đồ",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else if (insightsData != null) {
            val data = insightsData!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                UtilizationCard(data.utilization.percent, data.utilization.active, data.utilization.inactive)
                OutfitRatingCard(data.ratings.manual.avg, data.ratings.manual.count, data.ratings.ai.avg, data.ratings.ai.count)
                EventBreakdownCard(data.events)
                WardrobeDnaCard(data.dna.materials, data.dna.brands)

                Spacer(modifier = Modifier.height(40.dp))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Chưa có đủ dữ liệu để phân tích.", color = TextLightBlue)
            }
        }
    }
}

// --- 1. ĐỘ NĂNG ĐỘNG CỦA TỦ ĐỒ ---
@Composable
fun UtilizationCard(percent: Float, activeCount: Int, inactiveCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.DataUsage, null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Độ năng động tủ đồ", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 16.sp)
                }
                Text("30 ngày qua", style = MaterialTheme.typography.bodyLarge, color = TextLightBlue, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { percent },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(50)),
                color = AccentBlue,
                trackColor = BgLight,
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${(percent * 100).toInt()}%", style = MaterialTheme.typography.titleLarge, fontSize = 24.sp, color = AccentBlue)
                    Text("$activeCount món đang mặc", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = TextBlue)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${((1 - percent) * 100).toInt()}%", style = MaterialTheme.typography.titleLarge, fontSize = 24.sp, color = TextLightBlue.copy(alpha = 0.5f))
                    Text("$inactiveCount món bám bụi", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = TextLightBlue)
                }
            }
        }
    }
}

// --- 2. AI STYLIST VS BẠN ---
@Composable
fun OutfitRatingCard(manualAvg: String, manualCount: Int, aiAvg: String, aiCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Điểm phối đồ (Outfits)", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ScoreColumn(title = "Bạn tự phối", score = manualAvg, subtext = "$manualCount bộ", icon = Icons.Rounded.Person, tint = PrimaryCyan)
                Box(modifier = Modifier.width(1.dp).height(50.dp).background(BgLight))
                ScoreColumn(title = "AI Gợi ý", score = aiAvg, subtext = "$aiCount bộ", icon = Icons.Rounded.AutoAwesome, tint = PrimaryPink)
            }
        }
    }
}

@Composable
fun ScoreColumn(title: String, score: String, subtext: String, icon: ImageVector, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = tint.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
            Icon(icon, null, tint = tint, modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(score, style = MaterialTheme.typography.titleLarge, fontSize = 28.sp, color = TextDarkBlue)
            Icon(Icons.Rounded.Star, null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
        }
        Text(title, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextBlue)
        Text(subtext, style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextLightBlue)
    }
}

// --- 3. SỰ KIỆN PHỔ BIẾN ---
@Composable
fun EventBreakdownCard(events: List<EventData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Rounded.EventNote, null, tint = TextPink, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Mặc theo sự kiện", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (events.isEmpty()) {
                Text("Chưa có lịch trình sự kiện nào.", color = TextLightBlue, fontSize = 13.sp)
            } else {
                val colors = listOf(AccentBlue, PrimaryPink, PrimaryCyan, TextPink, TextDarkBlue)
                events.take(5).forEachIndexed { index, event ->
                    EventBar(label = event.label, percent = event.percent, color = colors[index % colors.size])
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun EventBar(label: String, percent: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyLarge, fontSize = 13.sp, color = TextBlue)
            Text("${(percent * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextDarkBlue)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
            color = color,
            trackColor = BgLight,
            strokeCap = StrokeCap.Round
        )
    }
}

// --- 4. ĐẶC ĐIỂM TỦ ĐỒ (BRAND & MATERIAL) ---
@Composable
fun WardrobeDnaCard(materials: List<com.example.smartfashion.data.api.DnaItemData>, brands: List<com.example.smartfashion.data.api.DnaItemData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocalOffer, null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đặc điểm Tủ đồ", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Top Chất liệu", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextLightBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (materials.isEmpty()) Text("Chưa có", color = TextLightBlue, fontSize = 12.sp)
                    materials.forEach { DnaItem(it.name, "${it.count} món") }
                }

                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Top Thương hiệu", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextLightBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (brands.isEmpty()) Text("Chưa có", color = TextLightBlue, fontSize = 12.sp)
                    brands.forEach { DnaItem(it.name, "${it.count} món", isRightAlign = true) }
                }
            }
        }
    }
}

@Composable
fun DnaItem(title: String, count: String, isRightAlign: Boolean = false) {
    Column(
        modifier = Modifier.padding(bottom = 8.dp),
        horizontalAlignment = if (isRightAlign) Alignment.End else Alignment.Start
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, fontSize = 14.sp, color = TextDarkBlue, fontWeight = FontWeight.Bold)
        Text(count, style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = TextBlue)
    }
}