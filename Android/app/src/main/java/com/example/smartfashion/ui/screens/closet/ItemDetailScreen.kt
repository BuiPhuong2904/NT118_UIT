package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
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


data class ClosetItemDetail(
    val clothingId: Int = 1,
    val name: String = "Váy Midi Xếp Ly", // name
    val brandName: String = "Uniqlo", // brand_name
    val imageUrl: String = "https://i.postimg.cc/9MXZHYtp/3.jpg",
    val categoryName: String = "Váy > Váy Midi",
    val seasonTags: List<String> = listOf("Xuân", "Hè"),
    val occasionTags: String = "Đi làm, Dạo phố",
    val colorHex: Color = Color.Black,
    val colorFamily: String = "Đen",
    val material: String = "Cotton / Silk",
    val size: String = "M",
    val status: String = "Sạch sẽ",
    val lastWorn: String = "3 ngày trước"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    navController: NavController,
    item: ClosetItemDetail = ClosetItemDetail()
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết món đồ",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Mở màn hình Edit */ }) {
                        Icon(Icons.Rounded.Edit, "Edit", tint = TextLightBlue)
                    }
                    IconButton(onClick = { /* Xử lý xóa */ }) {
                        Icon(Icons.Rounded.DeleteOutline, "Delete", tint = TextPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            // Nút "Phối đồ ngay"
            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { /* Sang màn hình Mix & Match (bảng Outfits) */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextDarkBlue)
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Gợi ý phối đồ",
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
            // 1. HIỂN THỊ ẢNH
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(BgLight),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(280.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = SecWhite,
                    shadowElevation = 4.dp
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AccentBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = item.seasonTags.joinToString(", "),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp),
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. PHẦN THÔNG TIN CHI TIẾT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(SecWhite)
                    .padding(24.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextDarkBlue,
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (item.status == "Sạch sẽ") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = item.status,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp),
                            color = if (item.status == "Sạch sẽ") Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "• Mặc lần cuối: ${item.lastWorn}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextLightBlue,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = BgLight)

                DetailRowElegant("Phân loại", item.categoryName, isPrimary = true)
                DetailRowElegant("Thương hiệu", item.brandName)
                DetailRowElegant("Dịp mặc", item.occasionTags)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Màu sắc",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDarkBlue
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            item.colorFamily,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextLightBlue,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(item.colorHex)
                                .border(1.dp, TextLightBlue.copy(0.2f), CircleShape)
                        )
                    }
                }
                HorizontalDivider(color = BgLight)

                DetailRowElegant("Chất liệu", item.material)
                DetailRowElegant("Kích cỡ", item.size)

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun DetailRowElegant(
    label: String,
    value: String,
    isPrimary: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                color = TextDarkBlue
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isPrimary) AccentBlue else TextLightBlue,
                    fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        HorizontalDivider(color = BgLight)
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailPreview() {
    ItemDetailScreen(navController = rememberNavController())
}