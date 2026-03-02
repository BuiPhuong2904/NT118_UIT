package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DryCleaning
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.PrimaryPink
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

data class DeclutterItem(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val lastWorn: String
)

@Composable
fun DeclutterScreen(navController: NavController) {
    var items by remember {
        mutableStateOf(
            listOf(
                DeclutterItem(1, "Áo khoác Bomber", "https://i.postimg.cc/9MXZHYtp/3.jpg", "8 tháng trước"),
                DeclutterItem(2, "Quần Jean rách gối", "https://i.postimg.cc/9MXZHYtp/3.jpg", "1 năm trước"),
                DeclutterItem(3, "Áo len cổ lọ", "https://i.postimg.cc/9MXZHYtp/3.jpg", "7 tháng trước")
            )
        )
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Dọn tủ",
                            style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (items.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = PrimaryPink.copy(alpha = 0.1f),
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Còn ${items.size} món",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PrimaryPink,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            if (items.isEmpty()) {
                DoneDeclutteringState(onFinish = { navController.popBackStack() })
            } else {
                val currentItem = items.first()

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Món này đã nằm góc tủ hơi lâu rồi nha...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextLightBlue,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Thẻ đồ
                    ItemCard(item = currentItem)

                    Spacer(modifier = Modifier.weight(1f))

                    // Khu vực 2 nút Action (Thanh lý / Giữ lại)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ActionButton(
                            icon = Icons.Rounded.Close,
                            label = "Thanh lý",
                            color = PrimaryPink,
                            onClick = {
                                items = items.drop(1)
                            }
                        )

                        ActionButton(
                            icon = Icons.Rounded.Favorite,
                            label = "Giữ lại",
                            color = AccentBlue,
                            onClick = {
                                items = items.drop(1)
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- THẺ HIỂN THỊ MÓN ĐỒ ---
@Composable
fun ItemCard(item: DeclutterItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(BgLight)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                ) {
                    Text(
                        text = "Mặc lần cuối: ${item.lastWorn}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextDarkBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = SecWhite,
            border = BorderStroke(2.dp, color.copy(alpha = 0.3f)),
            shadowElevation = 4.dp,
            modifier = Modifier.size(72.dp)
        ) {
            IconButton(onClick = onClick, modifier = Modifier.fillMaxSize()) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// --- GIAO DIỆN KHI ĐÃ DỌN XONG ---
@Composable
fun DoneDeclutteringState(onFinish: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            shape = CircleShape,
            color = AccentBlue.copy(alpha = 0.1f),
            modifier = Modifier.size(100.dp)
        ) {
            Icon(Icons.Rounded.DryCleaning, contentDescription = null, tint = AccentBlue, modifier = Modifier.padding(24.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Tủ đồ đã gọn gàng!",
            style = MaterialTheme.typography.titleLarge,
            color = TextDarkBlue,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Bạn không còn món đồ nào\nbị lãng quên nữa. Tuyệt vời!",
            style = MaterialTheme.typography.bodyLarge,
            color = TextLightBlue,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
        ) {
            Text("Quay về Tủ đồ", style = MaterialTheme.typography.titleMedium, fontSize = 16.sp, color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeclutterScreenPreview() {
    DeclutterScreen(navController = rememberNavController())
}