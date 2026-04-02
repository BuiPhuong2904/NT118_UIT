package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@Composable
fun StoreScreen(
    navController: NavController,
    viewModel: StoreViewModel = hiltViewModel()
) {
    val context = LocalContext.current
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
        contentWindowInsets = WindowInsets(0.dp)
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
                            text = "Kho mẫu",
                            style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                            fontWeight = FontWeight.Bold
                        )
                        Text("Khám phá ý tưởng phối đồ", fontSize = 12.sp, color = TextLightBlue)
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

            // --- DANH SÁCH SẢN PHẨM ---
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

                    SystemClothesCard(
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
                        }
                    )
                }

                if (isLoading && storeItems.isNotEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryCyan)
                        }
                    }
                }
            }
        }
    }
}

// COMPONENT: Thanh tìm kiếm Kho Mẫu
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
fun SystemClothesCard(
    item: SystemClothing,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val parsedColor = try {
        Color(android.graphics.Color.parseColor(item.colorHex ?: "#E0E0E0"))
    } catch (e: Exception) { Color.LightGray }

    val finalCategoryName = item.categoryName ?: "CHƯA PHÂN LOẠI"

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = finalCategoryName.uppercase(),
                        color = AccentBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.name,
                    color = TextDarkBlue,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}