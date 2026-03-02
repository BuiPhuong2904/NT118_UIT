package com.example.smartfashion.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

val BlogPrimary = Color(0xFF6200EE)

data class Article(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val imageUrl: String,
    val timeAgo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllArticlesScreen(
    onBackClick: () -> Unit = {},
    onArticleClick: (String) -> Unit = {}
) {
    val articles = remember {
        listOf(
            Article("1", "5 Quy tắc phối màu cơ bản", "Bánh xe màu sắc là công cụ không thể thiếu...", "Kiến thức", "https://i.postimg.cc/9MXZHYtp/3.jpg", "2 giờ trước"),
            Article("2", "Phối đồ Layering mùa đông", "Giữ ấm mà vẫn thời trang với kỹ thuật layer...", "Mùa đông", "https://i.postimg.cc/9MXZHYtp/3.jpg", "1 ngày trước"),
            Article("3", "Cách chọn quần Jean cho dáng quả lê", "Bí quyết hack dáng cực đỉnh...", "Dáng người", "https://i.postimg.cc/9MXZHYtp/3.jpg", "3 ngày trước"),
            Article("4", "Minimalism: Lối sống tối giản", "Ít hơn là nhiều - Phong cách sống hiện đại...", "Phong cách", "https://i.postimg.cc/9MXZHYtp/3.jpg", "1 tuần trước"),
            Article("5", "Phụ kiện làm điểm nhấn", "Đừng để bộ đồ của bạn trở nên nhàm chán...", "Phụ kiện", "https://i.postimg.cc/9MXZHYtp/3.jpg", "2 tuần trước"),
        )
    }

    var selectedCategory by remember { mutableStateOf("Tất cả") }

    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("Cẩm nang thời trang", fontWeight = FontWeight.Bold) },
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

                CategoryFilterBar(
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it }
                )
                HorizontalDivider(color = Color.LightGray.copy(0.2f))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            items(articles) { article ->
                FullWidthArticleCard(article = article, onClick = { onArticleClick(article.id) })
            }
        }
    }
}

@Composable
fun CategoryFilterBar(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf("Tất cả", "Kiến thức", "Mùa đông", "Dáng người", "Phong cách", "Phụ kiện")

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            val isSelected = cat == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(cat) },
                label = { Text(cat) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BlogPrimary,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF0F0F0),
                    labelColor = Color.Black
                ),
                border = null,
                shape = RoundedCornerShape(50)
            )
        }
    }
}

@Composable
fun FullWidthArticleCard(article: Article, onClick: () -> Unit) {
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
                .height(110.dp)
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = article.category.uppercase(),
                        color = BlogPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = article.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = article.summary,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AccessTime, null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(article.timeAgo, fontSize = 11.sp, color = Color.Gray)
                    }

                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = "Save",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllArticlesPreview() {
    AllArticlesScreen()
}