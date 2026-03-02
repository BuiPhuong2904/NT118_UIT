package com.example.smartfashion.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

val ArticlePrimary = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    onBackClick: () -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            ArticleBottomBar(
                isLiked = isLiked,
                onLikeClick = { isLiked = !isLiked }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            item {
                Box(modifier = Modifier.height(350.dp).fillMaxWidth()) {
                    AsyncImage(
                        model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Brush.verticalGradient(listOf(Color.Black.copy(0.6f), Color.Transparent)))
                    )
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Surface(
                        color = ArticlePrimary,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "TRENDING • MÙA ĐÔNG",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "5 Cách Phối Đồ Layering 'Chuẩn' Fashionista Cho Mùa Đông",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 32.sp,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                            contentDescription = null,
                            modifier = Modifier.size(40.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Bởi Vogue Editor", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("12 Tháng 2, 2026 • 5 phút đọc", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.LightGray.copy(0.3f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Layering (phối đồ nhiều lớp) không chỉ là cách giữ ấm hiệu quả mà còn là nghệ thuật thể hiện cá tính. Tuy nhiên, nếu không khéo léo, bạn rất dễ biến mình thành một 'chiếc bánh nếp' di động.\n\nDưới đây là 5 quy tắc vàng giúp bạn chinh phục phong cách này:",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFF333333)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "1. Quy tắc mỏng trong - dày ngoài",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Luôn bắt đầu với lớp áo mỏng nhất (như áo giữ nhiệt, sơ mi) và tăng dần độ dày ra bên ngoài (len, áo khoác dạ).",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            item {
                ShopTheLookSection()
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun ShopTheLookSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9))
            .padding(vertical = 24.dp)
    ) {
        Padding(start = 20.dp, end = 20.dp, bottom = 16.dp) {
            Text("Gợi ý từ tủ đồ của bạn", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Các món đồ phù hợp với bài viết này", fontSize = 12.sp, color = Color.Gray)
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(4) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.width(140.dp)
                ) {
                    Column {
                        AsyncImage(
                            model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                            contentDescription = null,
                            modifier = Modifier.height(140.dp).fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Áo Len Cổ Lọ", fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                            Text("Trong tủ đồ", fontSize = 11.sp, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Padding(
    start: androidx.compose.ui.unit.Dp = 0.dp,
    top: androidx.compose.ui.unit.Dp = 0.dp,
    end: androidx.compose.ui.unit.Dp = 0.dp,
    bottom: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.padding(start, top, end, bottom)) {
        content()
    }
}

@Composable
fun ArticleBottomBar(
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    Surface(
        shadowElevation = 16.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFF5F5F5),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Viết bình luận...", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = onLikeClick) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) Color.Red else Color.Black
                )
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = "Save")
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticleDetailPreview() {
    ArticleDetailScreen()
}