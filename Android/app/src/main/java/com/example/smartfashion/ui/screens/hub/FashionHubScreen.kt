package com.example.smartfashion.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
val HubPrimary = Color(0xFF6200EE)
val HubBackground = Color(0xFFF9F9F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FashionHubScreen(
    onBackClick: () -> Unit = {},
    onArticleClick: (String) -> Unit = {}, // Bấm vào 1 bài viết -> ArticleDetailScreen
    onTrendClick: () -> Unit = {}          // Bấm "Xem thêm" Xu hướng -> CommunityTrendScreen
) {
    Scaffold(
        containerColor = HubBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Fashion Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            // 1. CÔNG CỤ MÀU SẮC (Color Theory Tool)
            item {
                ColorToolBanner()
            }

            // 2. XU HƯỚNG MỚI (Trending Now) - List Ngang
            item {
                // Header có nút "Xem thêm" -> Bấm vào để mở CommunityTrendScreen
                SectionHeader(
                    title = "Xu hướng thịnh hành",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    onSeeAllClick = onTrendClick
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(5) {
                        // Bấm vào card con cũng có thể mở chi tiết hoặc mở Trend Screen tùy bạn
                        TrendCard(onClick = onTrendClick)
                    }
                }
            }

            // 3. BÀI VIẾT / TIPS (Vertical List)
            item {
                SectionHeader(
                    title = "Mẹo phối đồ",
                    icon = Icons.Default.ColorLens,
                    // Mục này thường cuộn xuống dưới luôn nên có thể không cần nút Xem thêm,
                    // nhưng nếu muốn làm trang danh sách bài viết riêng thì thêm vào.
                    onSeeAllClick = { /* Mở danh sách tất cả bài viết */ }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Danh sách bài viết
            items(4) {
                // Bấm vào bài viết -> Mở ArticleDetailScreen
                ArticleCard(onClick = { onArticleClick("article_id") })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// Cập nhật SectionHeader: Thêm nút "Xem thêm"
@Composable
fun SectionHeader(
    title: String,
    icon: ImageVector,
    onSeeAllClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Tiêu đề bên trái
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = HubPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        // Nút "Xem thêm" bên phải
        TextButton(onClick = onSeeAllClick) {
            Text("Xem thêm", fontSize = 12.sp, color = HubPrimary, fontWeight = FontWeight.SemiBold)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(12.dp), tint = HubPrimary)
        }
    }
}

@Composable
fun ColorToolBanner() {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { /* Mở màn hình Bánh xe màu sắc */ },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Gradient 7 màu
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFCDD2), Color(0xFFE1BEE7), Color(0xFFC5CAE9),
                                Color(0xFFB2DFDB), Color(0xFFDCEDC8), Color(0xFFFFF9C4), Color(0xFFFFE0B2)
                            )
                        )
                    )
            )

            // Nội dung
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Bánh xe Màu sắc",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tìm màu phối chuẩn Stylist",
                        fontSize = 14.sp,
                        color = Color(0xFF555555)
                    )
                }

                // Nút giả
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = HubPrimary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TrendCard(onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() } // Bấm vào card cũng chuyển trang
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .height(180.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            AsyncImage(
                model = "https://i.postimg.cc/9MXZHYtp/3.jpg", // Ảnh trend mẫu
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Vintage Style", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("#Cozy #Winter", fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ArticleCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp)
        ) {
            // Ảnh bài viết
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f)
            ) {
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Nội dung
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "5 Quy tắc phối màu cơ bản bạn cần biết",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = HubPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Tips",
                            color = HubPrimary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("5 phút đọc", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FashionHubPreview() {
    FashionHubScreen()
}