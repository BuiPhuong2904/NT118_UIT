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

data class CommunityPost(
    val id: String,
    val imageUrl: String,
    val authorName: String,
    val authorAvatar: String,
    val likes: Int,
    val description: String,
    val heightRatio: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTrendScreen(
    onBackClick: () -> Unit = {},
    onPostClick: (String) -> Unit = {}
) {
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
        containerColor = BgLight,
        topBar = {
            Column(modifier = Modifier.background(BgLight)) {
                TopAppBar(
                    title = {
                        // Thanh search
                        Surface(
                            shape = CircleShape,
                            color = SecWhite,
                            shadowElevation = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clickable { }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(Icons.Default.Search, null, tint = TextLightBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tìm ý tưởng phối đồ...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextLightBlue.copy(alpha = 0.7f)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = TextDarkBlue)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
                )

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
                            label = {
                                Text(
                                    text = filter,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentBlue,
                                selectedLabelColor = Color.White,
                                containerColor = SecWhite,
                                labelColor = TextLightBlue
                            ),
                            shape = CircleShape,
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // Nút đăng bài
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(GradientText)
                    .clickable { /* Mở màn hình chọn Outfit để đăng */ }
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chia sẻ Outfit", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 16.dp
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
        modifier = Modifier.clickable { onClick() }
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f / post.heightRatio),
                    contentScale = ContentScale.Crop
                )

                // Nút tim trên ảnh
                Surface(
                    color = SecWhite.copy(alpha = 0.8f),
                    shape = CircleShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clickable { isLiked = !isLiked }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) TextPink else TextDarkBlue,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Thông tin người đăng
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            // Avatar nhỏ
            AsyncImage(
                model = post.authorAvatar,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Tên & Caption
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 13.sp,
                    color = TextDarkBlue,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 11.sp,
                        color = TextLightBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Favorite, null, tint = TextLightBlue, modifier = Modifier.size(10.dp))
                    Text(
                        text = " ${post.likes}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 11.sp,
                        color = TextLightBlue
                    )
                }
            }

            Icon(Icons.Default.MoreHoriz, null, tint = TextLightBlue, modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommunityPreview() {
    CommunityTrendScreen()
}