package com.example.smartfashion.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo
val CommunityPrimary = Color(0xFF6200EE)

// Model bài đăng cộng đồng
data class CommunityPost(
    val id: String,
    val imageUrl: String,
    val authorName: String,
    val authorAvatar: String,
    val likes: Int,
    val description: String,
    val heightRatio: Float // Tỷ lệ chiều cao để tạo hiệu ứng so le (Pinterest)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTrendScreen(
    onBackClick: () -> Unit = {},
    onPostClick: (String) -> Unit = {}
) {
    // Dữ liệu giả lập (Pinterest style)
    val posts = remember {
        listOf(
            CommunityPost("1", "https://i.postimg.cc/9MXZHYtp/3.jpg", "Anna Nguyen", "https://i.postimg.cc/9MXZHYtp/3.jpg", 1240, "OOTD đi cafe cuối tuần ☕️", 1.2f),
            CommunityPost("2", "https://i.postimg.cc/9MXZHYtp/3.jpg", "Minh Tuấn", "https://i.postimg.cc/9MXZHYtp/3.jpg", 856, "Streetwear vibe", 1.5f),
            CommunityPost("3", "https://i.postimg.cc/9MXZHYtp/3.jpg", "Sara fashion", "https://i.postimg.cc/9MXZHYtp/3.jpg", 2300, "Mùa đông không lạnh", 1.0f),
            CommunityPost("4", "https://i.postimg.cc/9MXZHYtp/3.jpg", "Linh Ka", "https://i.postimg.cc/9MXZHYtp/3.jpg", 543, "Vintage style", 1.6f),
            CommunityPost("5", "https://i.postimg.cc/9MXZHYtp/3.jpg", "Fashion Week", "https://i.postimg.cc/9MXZHYtp/3.jpg", 112, "Đi làm mặc gì?", 1.1f),
            CommunityPost("6", "https://i.postimg.cc/9MXZHYtp/3.jpg", "Boorin", "https://i.postimg.cc/9MXZHYtp/3.jpg", 999, "Black on black ⚫️", 1.4f),
        )
    }

    var selectedFilter by remember { mutableStateOf("Dành cho bạn") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            Column {
                // Header hàng 1: Back, Search
                TopAppBar(
                    title = {
                        // Thanh search giả
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color(0xFFF5F5F5),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .clickable { }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            ) {
                                Icon(Icons.Default.Search, null, tint = Color.Gray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tìm ý tưởng phối đồ...", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )

                // Header hàng 2: Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf("Dành cho bạn", "Đang hot", "Mới nhất", "Streetwear", "Vintage", "Office")
                    items(filters.size) { index ->
                        val filter = filters[index]
                        val isSelected = filter == selectedFilter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color.Black,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // Nút đăng bài (Chia sẻ Outfit)
            ExtendedFloatingActionButton(
                onClick = { /* Mở màn hình chọn Outfit để đăng */ },
                containerColor = Color.Black,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Chia sẻ Outfit") }
            )
        }
    ) { paddingValues ->
        // LƯỚI SO LE (Pinterest Grid)
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(bottom = 80.dp), // Chừa chỗ cho FAB
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp
        ) {
            items(posts) { post ->
                CommunityPostCard(post, onClick = { onPostClick(post.id) })
            }
        }
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost, onClick: () -> Unit) {
    var isLiked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(bottom = 8.dp)
    ) {
        // Ảnh Outfit (Bo góc)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f / post.heightRatio), // Tỷ lệ động
                    contentScale = ContentScale.Crop
                )

                // Nút tim trên ảnh (giống Pinterest)
                IconButton(
                    onClick = { isLiked = !isLiked },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Thông tin người đăng
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar nhỏ
            AsyncImage(
                model = post.authorAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Tên & Caption
            Column {
                Text(
                    text = post.description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorName,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Favorite, null, tint = Color.Gray, modifier = Modifier.size(8.dp))
                    Text(
                        text = " ${post.likes}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Nút more
            Icon(Icons.Default.MoreHoriz, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityPreview() {
    CommunityTrendScreen()
}