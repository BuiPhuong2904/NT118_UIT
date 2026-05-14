package com.example.smartfashion.ui.screens.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.smartfashion.model.SystemClothing
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.PrimaryCyan
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

import androidx.compose.ui.platform.LocalContext
import com.example.smartfashion.data.local.TokenManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.smartfashion.ui.screens.closet.StoreViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun ShoppingScreen(
    navController: NavController,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val tokenManager = remember { TokenManager(context) }
    val currentUserId = tokenManager.getUserId()

    val storeItems by viewModel.storeItems.collectAsState()
    val wishlistMap by viewModel.wishlistMap.collectAsState()
    val filterGroups by viewModel.filterGroups.collectAsState()
    val selectedFilters by viewModel.selectedFilters.collectAsState()
    val parentCategories by viewModel.parentCategories.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var localSearchText by remember { mutableStateOf("") }
    var expandedGroup by remember { mutableStateOf<String?>(null) }
    val gridState = rememberLazyGridState()

    // Lắng nghe cuộn tải thêm
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null) {
                    val totalItems = gridState.layoutInfo.totalItemsCount
                    if (lastIndex >= totalItems - 2 && !isLoading) {
                        viewModel.loadMore()
                    }
                }
            }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (currentUserId != -1) {
                    viewModel.fetchUserWishlist(currentUserId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        containerColor = BgLight,
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            // Chỉ hiện nút khi đã cuộn qua vài item
            AnimatedVisibility(visible = gridState.firstVisibleItemIndex > 4) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    },
                    containerColor = AccentBlue,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Lên đầu")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .background(BgLight)
            ) {
                // HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mua sắm",
                            style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                            fontWeight = FontWeight.Bold
                        )
                        Text("Gợi ý mua sắm từ AI", fontSize = 12.sp, color = TextLightBlue)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // THANH TÌM KIẾM
                StoreSearchBar(
                    searchQuery = localSearchText,
                    onSearchChange = { text ->
                        localSearchText = text
                        viewModel.onSearchQueryChanged(text)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                // THANH FILTER DROPDOWN
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    // NÚT "TẤT CẢ"
                    item {
                        val isAllSelected = selectedFilters.isEmpty() && selectedCategories.isEmpty()

                        Surface(
                            shape = CircleShape,
                            color = if (isAllSelected) AccentBlue else Color.Transparent,
                            border = if (isAllSelected) null else BorderStroke(1.dp, TextLightBlue.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { viewModel.clearAllFilters() }
                        ) {
                            Text(
                                text = "Tất cả",
                                color = if (isAllSelected) Color.White else TextBlue,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // DROPDOWN "DANH MỤC"
                    if (parentCategories.isNotEmpty()) {
                        item {
                            val isSelected = selectedCategories.isNotEmpty()
                            val catDisplayText = if (!isSelected) "Danh mục" else selectedCategories.joinToString(", ") { it.name }

                            Box {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) AccentBlue else Color.Transparent,
                                    border = if (isSelected) null else BorderStroke(1.dp, TextLightBlue.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable { expandedGroup = "CategoryDropdown" }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = catDisplayText,
                                            color = if (isSelected) Color.White else TextBlue,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.widthIn(max = 140.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Rounded.ArrowDropDown,
                                            contentDescription = "Dropdown",
                                            tint = if (isSelected) Color.White else TextLightBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = expandedGroup == "CategoryDropdown",
                                    onDismissRequest = { expandedGroup = null },
                                    modifier = Modifier.background(SecWhite)
                                ) {
                                    parentCategories.forEach { category ->
                                        val isCatSelected = selectedCategories.any { it.categoryId == category.categoryId }
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = category.name,
                                                    color = if (isCatSelected) AccentBlue else TextDarkBlue,
                                                    fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            },
                                            onClick = { viewModel.updateCategoryFilter(category) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // CÁC NÚT DROPDOWN TAGS
                    items(filterGroups.keys.toList()) { groupName ->
                        val options = filterGroups[groupName] ?: emptyList()
                        val selectedOptionsInGroup = selectedFilters[groupName] ?: emptyList()
                        val isSelected = selectedOptionsInGroup.isNotEmpty()

                        val displayText = if (!isSelected) {
                            groupName
                        } else if (groupName == "Mùa" && selectedOptionsInGroup.size == 4) {
                            "4 mùa"
                        } else {
                            selectedOptionsInGroup.joinToString(", ")
                        }

                        Box {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) AccentBlue else Color.Transparent,
                                border = if (isSelected) null else BorderStroke(1.dp, TextLightBlue.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable { expandedGroup = groupName }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = displayText,
                                        color = if (isSelected) Color.White else TextBlue,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.widthIn(max = 140.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = "Dropdown",
                                        tint = if (isSelected) Color.White else TextLightBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expandedGroup == groupName,
                                onDismissRequest = { expandedGroup = null },
                                modifier = Modifier.background(SecWhite)
                            ) {
                                options.forEach { option ->
                                    val isOptionSelected = selectedOptionsInGroup.contains(option)
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option,
                                                color = if (isOptionSelected) AccentBlue else TextDarkBlue,
                                                fontWeight = if (isOptionSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        },
                                        onClick = { viewModel.updateFilter(groupName, option) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- DANH SÁCH SẢN PHẨM MUA SẮM ---
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp, start = 20.dp, end = 20.dp),
                modifier = Modifier.fillMaxSize().weight(1f)
            ) {
                items(storeItems, key = { it.templateId ?: it.hashCode() }) { item ->

                    val isFavorite = item.templateId?.let { wishlistMap.containsKey(it) } ?: false

                    ShoppingItemCard(
                        item = item,
                        isFavorite = isFavorite,
                        onClick = {
                            item.templateId?.let { id ->
                                navController.navigate("store_item_detail/$id")
                            }
                        },
                        onFavoriteClick = {
                            if (currentUserId != -1) {
                                viewModel.toggleWishlist(item, currentUserId)
                            }
                        },
                        onBuyClick = {
                            Toast.makeText(context, "Đang chuyển hướng đến Shopee...", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Nếu không có dữ liệu và cũng không đang load
                if (!isLoading && storeItems.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = "Empty",
                                modifier = Modifier.size(64.dp),
                                tint = TextLightBlue.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Không tìm thấy món đồ nào phù hợp!",
                                color = TextLightBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

// COMPONENT: Thanh tìm kiếm
@Composable
fun StoreSearchBar(searchQuery: String, onSearchChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(50.dp),
        shape = RoundedCornerShape(16.dp),
        color = SecWhite,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Search, "Search", tint = PrimaryCyan)
            Spacer(modifier = Modifier.width(12.dp))

            BasicTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextDarkBlue, fontSize = 14.sp),
                singleLine = true,
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text(text = "Tìm mẫu: Áo khoác, váy...", style = MaterialTheme.typography.bodyLarge, color = TextBlue.copy(alpha = 0.4f), fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )

            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Rounded.Close, "Clear", tint = TextLightBlue)
                }
            }
        }
    }
}

@Composable
fun ShoppingItemCard(
    item: SystemClothing,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onBuyClick: () -> Unit
) {
    val fakePrices = listOf("199.000đ", "250.000đ", "320.000đ", "150.000đ", "450.000đ", "280.000đ")
    val oldPrices = listOf("300.000đ", "350.000đ", "500.000đ", "250.000đ", "600.000đ", "400.000đ")

    val baseId = abs((item.templateId ?: item.hashCode()))
    val priceIndex = baseId % fakePrices.size
    val fakePrice = remember { fakePrices[priceIndex] }
    val oldPrice = remember { oldPrices[priceIndex] }

    val ratingIndex = baseId % 4
    val ratings = listOf("4.8", "4.9", "5.0", "4.7")
    val soldCounts = listOf("1.2k", "850", "3.4k", "500+")
    val rating = remember { ratings[ratingIndex] }
    val soldCount = remember { soldCounts[ratingIndex] }

    val finalCategoryName = item.categoryName ?: "THỜI TRANG"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFF3F6FA))
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentScale = ContentScale.Fit
                )

                // Badge "Sale"
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(TextPink, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("Sale", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                // Nút Wishlist
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isFavorite) TextPink else TextLightBlue.copy(alpha = 0.7f)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = finalCategoryName.uppercase(),
                    color = TextLightBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    color = TextDarkBlue,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp,
                    modifier = Modifier.height(32.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Đánh giá và Lượt bán
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⭐ $rating", fontSize = 10.sp, color = TextDarkBlue, fontWeight = FontWeight.Medium)
                    Text(text = " | ", fontSize = 10.sp, color = TextLightBlue)
                    Text(text = "Đã bán $soldCount", fontSize = 10.sp, color = TextLightBlue)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hàng giá tiền
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = fakePrice,
                        color = TextPink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = oldPrice,
                        color = TextLightBlue,
                        fontSize = 11.sp,
                        textDecoration = TextDecoration.LineThrough,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nút Mua ngay
                Button(
                    onClick = onBuyClick,
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mua ngay", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}