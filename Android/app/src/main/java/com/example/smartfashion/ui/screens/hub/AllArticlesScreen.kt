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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllArticlesScreen(
    onBackClick: () -> Unit = {},
    onArticleClick: (String) -> Unit = {}
) {
    // 1. Quản lý trạng thái lọc Category
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    // 2. Lấy dữ liệu thực tế từ MockArticleData và thực hiện lọc
    val displayArticles = remember(selectedCategory) {
        if (selectedCategory == "Tất cả") {
            MockArticleData.articles
        } else {
            MockArticleData.articles.filter {
                it.category.contains(selectedCategory, ignoreCase = true)
            }
        }
    }

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
                        IconButton(onClick = { /* Xử lý search bài báo */ }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )

                // Thanh lọc Category
                CategoryFilterBar(
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it }
                )
                HorizontalDivider(color = Color.LightGray.copy(0.2f))
            }
        }
    ) { paddingValues ->
        // Danh sách các bài báo sau khi lọc
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            items(displayArticles) { article ->
                FullWidthArticleCard(
                    article = article,
                    onClick = { onArticleClick(article.id) }
                )
            }

            // Hiện thông báo nếu không có bài báo nào thuộc category đó
            if (displayArticles.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 100.dp), contentAlignment = Alignment.Center) {
                        Text("Chưa có bài báo nào ở mục này.", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterBar(selected: String, onSelect: (String) -> Unit) {
    // Danh sách các mục
    val categories = listOf("Tất cả", "Mùa đông", "Mẹo", "Phong cách", "Capsule")

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
fun FullWidthArticleCard(article: MockArticle, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(140.dp)
        ) {
            // Ảnh bài báo
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(120.dp)
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

            // Nội dung tóm tắt bài báo
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
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = article.intro,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
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
                        Text(article.readTime, fontSize = 11.sp, color = Color.Gray)
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