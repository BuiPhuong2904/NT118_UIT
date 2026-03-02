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
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FashionHubScreen(
    onBackClick: () -> Unit = {},
    onArticleClick: (String) -> Unit = {},
    onTrendClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Khám phá",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextDarkBlue)
                    }
                },
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = SecWhite,
                        shadowElevation = 1.dp,
                        modifier = Modifier.padding(end = 16.dp).size(40.dp)
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = TextDarkBlue)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            item {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    ColorToolBanner()
                }
            }

            item {
                Column {
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                        SectionHeader(
                            title = "Xu hướng thịnh hành",
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            onSeeAllClick = onTrendClick
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        items(5) {
                            TrendCard(onClick = onTrendClick)
                        }
                    }
                }
            }

            item {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    SectionHeader(
                        title = "Mẹo phối đồ",
                        icon = Icons.Default.ColorLens,
                        onSeeAllClick = { }
                    )
                }
            }

            items(4) {
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    ArticleCard(onClick = { onArticleClick("article_id") })
                }
            }
        }
    }
}

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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                color = AccentBlue.copy(alpha = 0.1f),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(icon, null, tint = AccentBlue, modifier = Modifier.padding(6.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextDarkBlue,
                fontSize = 18.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onSeeAllClick() }
        ) {
            Text("Xem tất cả", style = MaterialTheme.typography.titleMedium, fontSize = 12.sp, color = TextPink)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(14.dp), tint = TextPink)
        }
    }
}

@Composable
fun ColorToolBanner() {
    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD1FF),
                                Color(0xFFE2D1F9),
                                Color(0xFFD1E8FF)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.6f)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.White.copy(0.7f), Color.Transparent)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bánh xe Màu sắc",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextDarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Khám phá quy tắc phối màu chuẩn Stylist.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDarkBlue.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TrendCard(onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .width(150.dp)
            .height(200.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.5f to Color.Transparent,
                            1f to Color.Black.copy(0.8f)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Vintage Style",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "#Cozy #Winter",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ArticleCard(onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "5 Quy tắc phối màu cơ bản bạn cần biết",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextDarkBlue,
                    fontSize = 14.sp,
                    maxLines = 2,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = TextPink.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Mẹo & Tricks",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPink,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("5 phút đọc", style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextLightBlue)
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