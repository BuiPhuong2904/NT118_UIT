package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDetailScreen(
    navController: NavController,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết Phối đồ",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = AccentBlue)
                    }
                    IconButton(onClick = { /* onEditClick */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { /* Chuyển sang màn hình Calendar / Mở BottomSheet Lên lịch */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextDarkBlue)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Lên lịch mặc bộ này",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(Color(0xFFEBF2FA))
            ) {
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = "Outfit Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .padding(20.dp)
                        .align(Alignment.BottomStart),
                    shape = RoundedCornerShape(16.dp),
                    color = SecWhite.copy(alpha = 0.95f),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = TextPink, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gợi ý bởi AI",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextDarkBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Set đồ công sở Hè",
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 26.sp,
                        color = TextDarkBlue,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "4.5",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextDarkBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Lịch sự nhưng vẫn phải mát mẻ để đi họp khách hàng vào buổi chiều. Phù hợp cho những ngày nắng nóng.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextLightBlue,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutfitTag(label = "Công sở")
                    OutfitTag(label = "Mùa Hè")
                }
            }

            HorizontalDivider(thickness = 2.dp, color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 24.dp))

            Column(modifier = Modifier.padding(vertical = 24.dp)) {
                Text(
                    text = "Món đồ trong set",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDarkBlue,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    items(3) {
                        ComponentItemCard()
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun OutfitTag(label: String) {
    Surface(
        color = AccentBlue.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = AccentBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ComponentItemCard() {
    Column(
        modifier = Modifier.width(110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SecWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .size(110.dp)
                .border(1.dp, TextLightBlue.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFEBF2FA)), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Áo Sơ Mi Trắng",
            style = MaterialTheme.typography.titleMedium,
            color = TextDarkBlue,
            fontSize = 13.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Uniqlo",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 12.sp,
            color = TextLightBlue
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OutfitDetailPreview() {
    OutfitDetailScreen(navController = rememberNavController())
}