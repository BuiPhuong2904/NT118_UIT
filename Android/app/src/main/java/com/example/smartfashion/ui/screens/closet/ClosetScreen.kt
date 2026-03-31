package com.example.smartfashion.ui.screens.closet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext

import com.example.smartfashion.ui.components.BottomNavigationBar
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.PrimaryCyan
import com.example.smartfashion.ui.theme.PrimaryPink
import com.example.smartfashion.ui.theme.SecDarkPink
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.SoftBlue
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.Category
import com.example.smartfashion.data.local.TokenManager

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.example.smartfashion.ui.theme.TextDarkBlue

@Composable
fun ClosetScreen(
    navController: NavController,
    viewModel: ClosetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userId = tokenManager.getUserId()

    // Lấy toàn bộ State từ ViewModel
    val allItems by viewModel.clothingList.collectAsState()
    val categories by viewModel.categoryList.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var localSearchText by remember { mutableStateOf(searchQuery) }

    // State để theo dõi việc cuộn trang
    val gridState = rememberLazyStaggeredGridState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (userId != -1) {
                    viewModel.fetchClothesForUser(userId = userId, isRefresh = true)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null) {
                    val totalItems = gridState.layoutInfo.totalItemsCount
                    if (lastIndex >= totalItems - 2 && !isLoading) {
                        if (userId != -1) {
                            viewModel.loadMore(userId = userId)
                        }
                    }
                }
            }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgLight)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                ClosetHeader()
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController, selectedItem = 1) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight)
                .padding(top = paddingValues.calculateTopPadding() + 10.dp)
                .padding(horizontal = 20.dp)
        ) {
            // --- Gắn biến cục bộ vào thanh Search ---
            ClosetSearchBar(
                searchQuery = localSearchText,
                onSearchChange = { text ->
                    localSearchText = text
                    if (userId != -1) {
                        viewModel.onSearchQueryChanged(text, userId)
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            UtilityRow(navController = navController)
            Spacer(modifier = Modifier.height(15.dp))

            // Lọc ra nút "Tất cả" (ID=0) và các danh mục cha (parentId = null hoặc 0)
            val rootCategories = categories.filter {
                it.categoryId == 0 || it.parentId == null || it.parentId == 0
            }

            CategoryTabs(
                categories = rootCategories,
                selectedId = selectedCategoryId,
                onSelect = { id ->
                    if (userId != -1) {
                        viewModel.onCategorySelected(id, userId = userId)
                    }
                }
            )
            Spacer(modifier = Modifier.height(15.dp))

            LazyVerticalStaggeredGrid(
                state = gridState,
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding() + 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                item { AddNewItemCard(navController) }

                items(
                    items = allItems,
                    key = { item -> item.clothingId ?: item.hashCode() }
                ) { item ->
                    StaggeredClosetItem(
                        item = item,
                        navController = navController,
                        onFavoriteClick = { isFavorite ->
                            viewModel.updateFavoriteStatus(item, isFavorite)
                        }
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClosetHeader() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(text = "Tủ đồ", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText), fontWeight = FontWeight.Bold)
            Text(text = "Quản lý & Sắp xếp gọn gàng", style = MaterialTheme.typography.bodyLarge, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = TextLightBlue)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = null, tint = TextPink) }
            IconButton(onClick = {}) { Icon(Icons.Outlined.Settings, contentDescription = null, tint = AccentBlue) }
        }
    }
}

// Component Tìm kiếm
@Composable
fun ClosetSearchBar(searchQuery: String, onSearchChange: (String) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp), color = SecWhite, shadowElevation = 2.dp) {
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
                        Text(text = "Tìm nhanh: Áo thun, Chân váy...", style = MaterialTheme.typography.bodyLarge, color = TextBlue.copy(alpha = 0.4f), fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )

            // Hiện nút X xóa chữ khi đang gõ
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Rounded.Close, "Clear", tint = TextLightBlue)
                }
            } else {
                Icon(Icons.Rounded.Tune, "Filter", tint = AccentBlue)
            }
        }
    }
}

@Composable
fun UtilityRow(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        UtilityItem("Thống kê", Icons.Rounded.Insights, AccentBlue.copy(alpha = 0.15f), AccentBlue) { navController.navigate("insights_screen") }
        UtilityItem("Dọn tủ", Icons.Rounded.CleaningServices, SecDarkPink.copy(alpha = 0.1f), SecDarkPink) { navController.navigate("declutter_screen") }
        UtilityItem("Yêu thích", Icons.Rounded.Favorite, PrimaryPink.copy(alpha = 0.15f), TextPink) { navController.navigate("favorites_screen") }
        UtilityItem("Kho mẫu", Icons.Rounded.Store, PrimaryCyan.copy(alpha = 0.2f), TextBlue) { navController.navigate("store_screen") }
    }
}

@Composable
fun UtilityItem(title: String, icon: ImageVector, bgColor: Color, iconColor: Color, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = bgColor, modifier = Modifier.size(56.dp).clickable { onClick() }) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp)) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontSize = 12.sp, color = TextBlue)
    }
}

@Composable
fun AddNewItemCard(navController: NavController) {
    val context = LocalContext.current
    val stroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))

    var showDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val encodedUri = Uri.encode(uri.toString())
            navController.navigate("loading_upload_screen?imageUri=$encodedUri")
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            val encodedUri = Uri.encode(tempCameraUri.toString())
            navController.navigate("loading_upload_screen?imageUri=$encodedUri")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .clickable { showDialog = true }
            .drawBehind { drawRoundRect(color = AccentBlue.copy(alpha = 0.5f), style = stroke, cornerRadius = CornerRadius(16.dp.toPx())) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = AccentBlue.copy(alpha = 0.1f), modifier = Modifier.size(50.dp)) {
                Icon(Icons.Rounded.Add, null, tint = AccentBlue, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Thêm đồ mới", style = MaterialTheme.typography.titleMedium, color = AccentBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Thêm đồ vào tủ", fontWeight = FontWeight.Bold, color = TextDarkBlue) },
            text = { Text("Bạn muốn cung cấp hình ảnh cho AI xử lý từ đâu?") },

            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text("Thư viện ảnh", color = AccentBlue, fontWeight = FontWeight.Bold)
                }
            },

            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    val uri = createImageUri(context)
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                }) {
                    Text("Chụp ảnh mới", color = TextPink, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SecWhite
        )
    }
}

@Composable
fun StaggeredClosetItem(
    item: Clothing,
    navController: NavController,
    onFavoriteClick: (Boolean) -> Unit = {}
) {
    val itemHeight = remember { (160..240).random().dp }
    val imageUrl = item.imageUrl

    Column(modifier = Modifier.fillMaxWidth().clickable {
        item.clothingId?.let { id ->
            navController.navigate("item_detail/$id")
        }
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEEEEEE))
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = {
                    onFavoriteClick(!item.isFavorite)
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
            ) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = "Favorite Toggle",
                    tint = if (item.isFavorite) PrimaryPink else SecWhite
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontSize = 13.sp, color = TextBlue, maxLines = 1)
        Text(text = item.brandName ?: "Chưa phân loại", style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, color = TextLightBlue)
    }
}

@Composable
fun CategoryTabs(
    categories: List<Category>,
    selectedId: Int,
    onSelect: (Int) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories.size) { index ->
            val cat = categories[index]
            val isSelected = cat.categoryId == selectedId

            FilterChip(
                selected = isSelected,
                onClick = { cat.categoryId?.let { onSelect(it) } },
                label = { Text(text = cat.name, style = MaterialTheme.typography.bodyLarge, fontSize = 13.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentBlue,
                    selectedLabelColor = SecWhite,
                    containerColor = SecWhite,
                    labelColor = SoftBlue
                ),
                border = if(isSelected) null else FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = SoftBlue.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(50)
            )
        }
    }
}

fun createImageUri(context: android.content.Context): Uri {
    val file = java.io.File(context.cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
    return androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}

@Preview(showBackground = true)
@Composable
fun ClosetScreenPreview() {
    ClosetScreen(navController = rememberNavController())
}