package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.Category // ĐÃ THÊM IMPORT NÀY
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.GradientSoft
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

val CanvasBackground = Color(0xFFF7F9FC)
val ItemBackground = Color(0xFFF8FAFC)
val ItemBorderColor = Color(0xFFE2E8F0)

enum class StudioMode {
    FLAT_LAY,
    MANNEQUIN
}

data class CanvasItem(
    val id: String,
    val imageUrl: String?,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var scale: Float = 1f,
    var rotation: Float = 0f,
    val type: String = "TOP"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    navController: NavController,
    viewModel: StudioViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userId = tokenManager.getUserId()

    val userClothes by viewModel.userClothes.collectAsState()
    val categories by viewModel.categoryList.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()

    val canvasItems by viewModel.canvasItems.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    var currentMode by remember { mutableStateOf(StudioMode.FLAT_LAY) }
    val selectedTab = remember { mutableStateOf("Tủ đồ") }

    var showSaveDialog by remember { mutableStateOf(false) }
    var outfitName by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    var isSuccessSnackbar by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    var selectedCanvasItemId by remember { mutableStateOf<String?>(null) }

    // 👇 2. GỌI API LẤY CẢ DANH MỤC VÀ QUẦN ÁO KHI MỚI VÀO MÀN HÌNH 👇
    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.fetchCategories()
            viewModel.fetchUserClothes(userId) // Mặc định sẽ lấy categoryId = 0 (Tất cả)
        }
    }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is SaveOutfitState.Success -> {
                showSaveDialog = false
                outfitName = ""
                isSuccessSnackbar = true
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Short
                    )
                }
                viewModel.resetSaveState()

                navController.navigate("outfit_detail_screen/${state.outfitId}")
            }
            is SaveOutfitState.Error -> {
                isSuccessSnackbar = false
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Short
                    )
                }
                viewModel.resetSaveState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = if (isSuccessSnackbar) Color(0xFF4CAF50) else Color(0xFFF44336),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = data.visuals.message, fontWeight = FontWeight.Bold)
                }
            }
        },
        topBar = {
            Column(modifier = Modifier.background(SecWhite)) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                        }
                    },
                    actions = {
                        Button(
                            onClick = {
                                if (canvasItems.isEmpty()) {
                                    coroutineScope.launch {
                                        isSuccessSnackbar = false
                                        snackbarHostState.showSnackbar("Vui lòng chọn ít nhất 1 món đồ!", duration = SnackbarDuration.Short)
                                    }
                                } else {
                                    showSaveDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.padding(end = 16.dp).height(36.dp).width(80.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(brush = GradientSoft, shape = RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Lưu", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SecWhite)
                )
                ModeSwitcher(currentMode = currentMode, onModeChanged = { currentMode = it })
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SecWhite,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                // 👇 3. TRUYỀN DANH MỤC VÀO BOTTOM PANEL 👇
                StudioBottomPanel(
                    selectedTab = selectedTab,
                    userClothes = userClothes,
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategorySelect = { catId ->
                        if (userId != -1) {
                            viewModel.onCategorySelected(catId, userId)
                        }
                    },
                    onClothingItemClick = { clickedClothing ->
                        viewModel.addItemToCanvas(clickedClothing)
                        selectedCanvasItemId = clickedClothing.clothingId.toString()
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(CanvasBackground)
                .drawBehind {
                    val dotRadius = 3f
                    val spacing = 64.dp.toPx()
                    val dotColor = Color(0xFFDCE2EB)
                    var x = spacing / 2
                    while (x < size.width) {
                        var y = spacing / 2
                        while (y < size.height) {
                            drawCircle(color = dotColor, radius = dotRadius, center = Offset(x, y))
                            y += spacing
                        }
                        x += spacing
                    }
                }
                .clipToBounds()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { selectedCanvasItemId = null })
                }
        ) {
            if (currentMode == StudioMode.MANNEQUIN) {
                MannequinView(canvasItems)
            } else {
                canvasItems.forEach { item ->
                    DraggableImage(
                        item = item,
                        isSelected = selectedCanvasItemId == item.id,
                        onClick = { selectedCanvasItemId = item.id },
                        onTransformChanged = { id, newX, newY, newScale, newRotation ->
                            viewModel.updateItemTransform(id, newX, newY, newScale, newRotation)
                        },
                        onDelete = { id ->
                            viewModel.removeItemFromCanvas(id)
                            if (selectedCanvasItemId == id) selectedCanvasItemId = null
                        }
                    )
                }
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { if (saveState !is SaveOutfitState.Loading) showSaveDialog = false },
            title = { Text(text = "Lưu bộ phối đồ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextBlue) },
            text = {
                OutlinedTextField(
                    value = outfitName,
                    onValueChange = { outfitName = it },
                    placeholder = { Text("Nhập tên bộ đồ (VD: Đồ đi chơi)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = TextLightBlue.copy(alpha = 0.5f)
                    ),
                    enabled = saveState !is SaveOutfitState.Loading
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (userId != -1) {
                            val finalName = if (outfitName.isNotBlank()) outfitName else "Bộ phối đồ mới"
                            viewModel.saveOutfit(userId, finalName)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(8.dp),
                    enabled = saveState !is SaveOutfitState.Loading
                ) {
                    if (saveState is SaveOutfitState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Xác nhận", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }, enabled = saveState !is SaveOutfitState.Loading) {
                    Text("Hủy", color = TextLightBlue)
                }
            },
            containerColor = SecWhite
        )
    }
}

@Composable
fun DraggableImage(
    item: CanvasItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onTransformChanged: (String, Float, Float, Float, Float) -> Unit,
    onDelete: (String) -> Unit
) {
    var offsetX by remember(item.id) { mutableFloatStateOf(item.offsetX) }
    var offsetY by remember(item.id) { mutableFloatStateOf(item.offsetY) }
    var scale by remember(item.id) { mutableFloatStateOf(item.scale) }
    var rotation by remember(item.id) { mutableFloatStateOf(item.rotation) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation
            )
            .size(160.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .drawBehind {
                    if (isSelected) {
                        drawRoundRect(
                            color = AccentBlue.copy(alpha = 0.6f),
                            style = Stroke(
                                width = 3f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                            ),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                        )
                    }
                }
                .pointerInput(item.id) {
                    detectTransformGestures { _, pan, zoom, rotate ->
                        onClick()
                        scale *= zoom
                        rotation += rotate
                        offsetX += pan.x
                        offsetY += pan.y
                        onTransformChanged(item.id, offsetX, offsetY, scale, rotation)
                    }
                }
                .pointerInput(item.id + "tap") {
                    detectTapGestures(onTap = { onClick() })
                }
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isSelected) {
            Surface(
                shape = CircleShape,
                color = SecWhite,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.TopStart)
                    .clickable { onDelete(item.id) }
            ) {
                Icon(Icons.Default.Close, contentDescription = "Xóa", tint = Color.Red, modifier = Modifier.padding(4.dp))
            }

            Surface(
                shape = CircleShape,
                color = AccentBlue,
                shadowElevation = 3.dp,
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Sync, contentDescription = "Scale/Rotate", tint = Color.White, modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
fun StudioBottomPanel(
    selectedTab: MutableState<String>,
    userClothes: List<Clothing>,
    categories: List<Category>,
    selectedCategoryId: Int,
    onCategorySelect: (Int) -> Unit,
    onClothingItemClick: (Clothing) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(SecWhite)
    ) {
        Surface(
            color = Color(0xFFF0F4F8),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 14.dp)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                listOf("Tủ đồ", "Nền", "Chữ").forEach { tab ->
                    val isSelected = selectedTab.value == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) Color.White else Color.Transparent)
                            .clickable { selectedTab.value = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (isSelected) AccentBlue else TextLightBlue,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = ItemBorderColor.copy(alpha = 0.5f))

        if (selectedTab.value == "Tủ đồ" || selectedTab.value == "Closet") {
            val rootCategories = categories.filter { it.categoryId == 0 || it.parentId == null || it.parentId == 0 }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                items(rootCategories) { cat ->
                    val isCatSelected = selectedCategoryId == cat.categoryId
                    Surface(
                        shape = CircleShape,
                        color = if (isCatSelected) TextDarkBlue else SecWhite,
                        border = if (isCatSelected) null else BorderStroke(1.dp, ItemBorderColor),
                        modifier = Modifier.clickable { cat.categoryId?.let { onCategorySelect(it) } }
                    ) {
                        Text(
                            text = cat.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCatSelected) Color.White else TextBlue,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // GRID QUẦN ÁO TỪ API TRẢ VỀ
            if (userClothes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có món đồ nào", color = TextLightBlue)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = userClothes,
                        key = { it.clothingId ?: it.hashCode() }
                    ) { clothing ->
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ItemBackground)
                                .border(1.dp, ItemBorderColor, RoundedCornerShape(16.dp))
                                .clickable { onClothingItemClick(clothing) },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = clothing.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModeSwitcher(currentMode: StudioMode, onModeChanged: (StudioMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SecWhite)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = SecWhite,
            border = BorderStroke(1.dp, TextLightBlue.copy(alpha = 0.3f)),
            modifier = Modifier.height(42.dp)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                ModeButton(text = "Sắp đặt", isSelected = currentMode == StudioMode.FLAT_LAY, onClick = { onModeChanged(StudioMode.FLAT_LAY) })
                Spacer(modifier = Modifier.width(4.dp))
                ModeButton(text = "Ướm mẫu", isSelected = currentMode == StudioMode.MANNEQUIN, onClick = { onModeChanged(StudioMode.MANNEQUIN) })
            }
        }
    }
}

@Composable
fun ModeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxHeight().width(110.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().then(if (isSelected) Modifier.background(brush = GradientSoft, shape = RoundedCornerShape(50)) else Modifier.background(Color.Transparent)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextLightBlue.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun MannequinView(items: List<CanvasItem>) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AsyncImage(model = "https://i.postimg.cc/9MXZHYtp/3.jpg", contentDescription = "Mannequin Base", modifier = Modifier.fillMaxHeight(0.9f).alpha(0.5f), contentScale = ContentScale.Fit)
        items.forEach { item ->
            val targetY = if (item.type == "TOP") (-100).dp else 100.dp
            AsyncImage(model = item.imageUrl, contentDescription = null, modifier = Modifier.size(140.dp).offset(y = targetY))
        }
        Column(modifier = Modifier.align(Alignment.CenterEnd).padding(8.dp)) {
            SmallToolButton(text = "Da")
            Spacer(modifier = Modifier.height(8.dp))
            SmallToolButton(text = "Tóc")
            Spacer(modifier = Modifier.height(8.dp))
            SmallToolButton(text = "Size")
        }
    }
}

@Composable
fun SmallToolButton(text: String) {
    Surface(onClick = {}, shape = CircleShape, color = SecWhite, shadowElevation = 4.dp, modifier = Modifier.size(48.dp)) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextDarkBlue)
        }
    }
}

fun Modifier.alpha(alpha: Float) = this.graphicsLayer(alpha = alpha)