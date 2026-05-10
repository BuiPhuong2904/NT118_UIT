package com.example.smartfashion.ui.screens.hub

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.model.CommunityPost
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink
import com.example.smartfashion.ui.theme.PrimaryCyan
import com.example.smartfashion.ui.theme.TextBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityTrendScreen(
    navController: NavController,
    onBackClick: () -> Unit = {},
    onPostClick: (String) -> Unit = {},
    viewModel: CommunityTrendViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userId = tokenManager.getUserId()

    val posts by viewModel.postsList.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val filterGroups by viewModel.filterGroups.collectAsState()
    val selectedFilters by viewModel.selectedFilters.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val gridState = rememberLazyStaggeredGridState()

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    // Tải data lần đầu
    LaunchedEffect(Unit) {
        viewModel.fetchPosts(isRefresh = true)
    }

    // Load More (Phân trang)
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && posts.isNotEmpty()) {
                    val totalItems = gridState.layoutInfo.totalItemsCount
                    if (lastIndex >= totalItems - 2 && !isLoading) {
                        viewModel.loadMore()
                    }
                }
            }
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            Column(modifier = Modifier.background(BgLight)) {
                // 1. THANH SEARCH & BACK BUTTON
                TopAppBar(
                    title = {
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
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
                )

                // 2. THANH MODE & NÚT FILTER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút Lọc (Filter Button) mở Bottom Sheet
                    val hasActiveFilters = selectedFilters.isNotEmpty()
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (hasActiveFilters) AccentBlue else SecWhite,
                        border = if (hasActiveFilters) null else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .height(36.dp)
                            .clickable { showFilterSheet = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Lọc",
                                tint = if (hasActiveFilters) Color.White else TextDarkBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            if (hasActiveFilters) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .background(Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = selectedFilters.values.flatten().size.toString(),
                                        color = AccentBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                    Spacer(modifier = Modifier.width(12.dp))

                    // LazyRow cuộn ngang cho các Mode
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val modes = listOf("Dành cho bạn", "Đang hot", "Mới nhất")
                        items(modes) { modeName ->
                            val isSelected = (modeName == selectedMode)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { viewModel.onModeSelected(modeName) }
                            ) {
                                Text(
                                    text = modeName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontSize = 14.sp,
                                    color = if (isSelected) AccentBlue else TextLightBlue,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .width(20.dp)
                                            .height(3.dp)
                                            .background(AccentBlue, RoundedCornerShape(50))
                                    )
                                }
                            }
                        }
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
                    .clickable { navController.navigate("select_outfit_share_screen") }
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
            state = gridState,
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalItemSpacing = 16.dp
        ) {
            items(posts, key = { it.postId ?: it.hashCode() }) { post ->
                CommunityPostCard(
                    post = post,
                    onClick = { onPostClick(post.postId.toString()) },
                    onLikeClick = {
                        if (userId != -1) viewModel.toggleLikeStatus(post, userId)
                    }
                )
            }

            if (isLoading && posts.isNotEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryCyan)
                    }
                }
            }
        }
    }

    // UI BOTTOM SHEET DÀNH CHO BỘ LỌC
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            containerColor = BgLight,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 40.dp)
            ) {
                // Header của Bottom Sheet
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lọc theo phong cách", style = MaterialTheme.typography.titleLarge, color = TextDarkBlue, fontWeight = FontWeight.Bold)
                    if (selectedFilters.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearAllTagFilters() }) {
                            Text("Xóa bộ lọc", color = TextPink, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cuộn dọc danh sách các nhóm Tag
                val scrollState = rememberScrollState()
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    filterGroups.keys.forEach { groupName ->
                        val options = filterGroups[groupName] ?: emptyList()
                        val selectedOptionsInGroup = selectedFilters[groupName] ?: emptyList()

                        Column {
                            Text(
                                text = groupName,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextBlue,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ) {
                                options.forEach { option ->
                                    val isSelected = selectedOptionsInGroup.contains(option)
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) AccentBlue.copy(alpha = 0.1f) else SecWhite,
                                        border = BorderStroke(1.dp, if (isSelected) AccentBlue else Color.LightGray.copy(alpha = 0.3f)),
                                        modifier = Modifier.clickable { viewModel.updateTagFilter(groupName, option) }
                                    ) {
                                        Text(
                                            text = option,
                                            color = if (isSelected) AccentBlue else TextDarkBlue,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nút Áp dụng để đóng Sheet
                Button(
                    onClick = { showFilterSheet = false },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Áp dụng", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    onClick: () -> Unit,
    onLikeClick: () -> Unit
) {
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
                        .clickable { onLikeClick() }
                ) {
                    Icon(
                        imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.isLiked) TextPink else TextDarkBlue,
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
                model = post.authorAvatar ?: "https://cdn-icons-png.flaticon.com/512/149/149071.png",
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
                    text = post.description ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 13.sp,
                    color = TextDarkBlue,
                    maxLines = 2,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.authorName ?: "Người dùng",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 11.sp,
                        color = TextLightBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.Default.Favorite, null, tint = TextLightBlue, modifier = Modifier.size(10.dp))
                    Text(
                        text = " ${post.likesCount}",
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