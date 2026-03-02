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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

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
fun InsightsScreen(navController: NavController) {
    Scaffold(
        containerColor = BgLight,
        topBar = {
            // HEADER
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            UtilizationCard()
            OutfitRatingCard()
            EventBreakdownCard()
            WardrobeDnaCard()

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- 1. ĐỘ NĂNG ĐỘNG CỦA TỦ ĐỒ ---
@Composable
fun UtilizationCard() {
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
                Text("Tháng này", style = MaterialTheme.typography.bodyLarge, color = TextLightBlue, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Thanh Progress
            LinearProgressIndicator(
                progress = { 0.65f },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(50)),
                color = AccentBlue,
                trackColor = BgLight,
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("65%", style = MaterialTheme.typography.titleLarge, fontSize = 24.sp, color = AccentBlue)
                    Text("Đồ đang sử dụng", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = TextBlue)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("35%", style = MaterialTheme.typography.titleLarge, fontSize = 24.sp, color = TextLightBlue.copy(alpha = 0.5f))
                    Text("Đồ bám bụi", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = TextLightBlue)
                }
            }
        }
    }
}

// --- 2. AI STYLIST VS BẠN ---
@Composable
fun OutfitRatingCard() {
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
                ScoreColumn(title = "Bạn tự phối", score = "3.8", subtext = "20 bộ", icon = Icons.Rounded.Person, tint = PrimaryCyan)

                Box(modifier = Modifier.width(1.dp).height(50.dp).background(BgLight))

                ScoreColumn(title = "AI Gợi ý", score = "4.7", subtext = "25 bộ", icon = Icons.Rounded.AutoAwesome, tint = PrimaryPink)
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
fun EventBreakdownCard() {
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

            EventBar(label = "Đi làm (Daily)", percent = 0.6f, color = AccentBlue)
            Spacer(modifier = Modifier.height(12.dp))
            EventBar(label = "Hẹn hò (Dating)", percent = 0.25f, color = PrimaryPink)
            Spacer(modifier = Modifier.height(12.dp))
            EventBar(label = "Du lịch (Travel)", percent = 0.15f, color = PrimaryCyan)
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
fun WardrobeDnaCard() {
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
                // Cột Chất liệu
                Column(modifier = Modifier.weight(1f)) {
                    Text("Top Chất liệu", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextLightBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    DnaItem("Cotton", "45 món")
                    DnaItem("Denim", "12 món")
                    DnaItem("Lụa (Silk)", "8 món")
                }

                // Cột Thương hiệu
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Top Thương hiệu", style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextLightBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    DnaItem("Uniqlo", "30 món", isRightAlign = true)
                    DnaItem("Zara", "15 món", isRightAlign = true)
                    DnaItem("Local Brand", "22 món", isRightAlign = true)
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

@Preview(showBackground = true)
@Composable
fun InsightsScreenPreview() {
    InsightsScreen(navController = rememberNavController())
}