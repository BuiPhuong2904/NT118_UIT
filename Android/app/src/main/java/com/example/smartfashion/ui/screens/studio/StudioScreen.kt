package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.model.Clothing
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.GradientAccent
import com.example.smartfashion.ui.theme.GradientSoft
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

val CanvasBackground = Color(0xFFEBF2FA)

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
    val canvasItems by viewModel.canvasItems.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    var currentMode by remember { mutableStateOf(StudioMode.FLAT_LAY) }
    val selectedTab = remember { mutableStateOf("Tủ đồ") }

    // State cho Dialog nhập tên Outfit
    var showSaveDialog by remember { mutableStateOf(false) }
    var outfitName by remember { mutableStateOf("") }

    // State cho Snackbar báo lỗi/thành công
    val snackbarHostState = remember { SnackbarHostState() }
    var isSuccessSnackbar by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.fetchUserClothes(userId)
        }
    }

    // Lắng nghe trạng thái lưu từ ViewModel
    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is SaveOutfitState.Success -> {
                showSaveDialog = false // Đóng hộp thoại nhập tên
                outfitName = "" // Reset tên
                isSuccessSnackbar = true
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        duration = SnackbarDuration.Short
                    )
                }
                viewModel.resetSaveState()
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
                        TextButton(
                            onClick = {
                                if (canvasItems.isEmpty()) {
                                    coroutineScope.launch {
                                        isSuccessSnackbar = false
                                        snackbarHostState.showSnackbar("Vui lòng chọn ít nhất 1 món đồ!", duration = SnackbarDuration.Short)
                                    }
                                } else {
                                    showSaveDialog = true // Hiện Dialog nhập tên
                                }
                            }
                        ) {
                            Text("Lưu", style = TextStyle(brush = GradientText), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SecWhite)
                )
                ModeSwitcher(
                    currentMode = currentMode,
                    onModeChanged = { currentMode = it }
                )
            }
        },
        bottomBar = {
            StudioBottomPanel(
                selectedTab = selectedTab,
                userClothes = userClothes,
                onClothingItemClick = { clickedClothing ->
                    viewModel.addItemToCanvas(clickedClothing)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(SecWhite)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(CanvasBackground)
                    .clipToBounds()
            ) {
                if (currentMode == StudioMode.MANNEQUIN) {
                    MannequinView(canvasItems)
                } else {
                    canvasItems.forEach { item ->
                        DraggableImage(
                            item = item,
                            onTransformChanged = { id, newX, newY, newScale, newRotation ->
                                viewModel.updateItemTransform(id, newX, newY, newScale, newRotation)
                            }
                        )
                    }
                }
            }
        }
    }

    // --- HIỆN HỘP THOẠI NHẬP TÊN OUTFIT ---
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = {
                if (saveState !is SaveOutfitState.Loading) {
                    showSaveDialog = false
                }
            },
            title = {
                Text(
                    text = "Lưu bộ phối đồ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextBlue
                )
            },
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
                TextButton(
                    onClick = { showSaveDialog = false },
                    enabled = saveState !is SaveOutfitState.Loading
                ) {
                    Text("Hủy", color = TextLightBlue)
                }
            },
            containerColor = SecWhite
        )
    }
}

// BỔ SUNG TRUYỀN CALLBACK CHO DraggableImage
@Composable
fun DraggableImage(
    item: CanvasItem,
    onTransformChanged: (String, Float, Float, Float, Float) -> Unit
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
            .size(150.dp)
            .pointerInput(item.id) { // Nhớ pointer cho từng Item cụ thể
                detectTransformGestures { _, pan, zoom, rotate ->
                    scale *= zoom
                    rotation += rotate
                    offsetX += pan.x
                    offsetY += pan.y

                    // Gửi lên ViewModel để lưu (đề phòng bị reset khi Compose vẽ lại)
                    onTransformChanged(item.id, offsetX, offsetY, scale, rotation)
                }
            }
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// SỬA BẢNG CHỌN ĐỂ HIỂN THỊ TỦ ĐỒ THẬT
@Composable
fun StudioBottomPanel(
    selectedTab: MutableState<String>,
    userClothes: List<Clothing>, // Truyền danh sách đồ thật vào
    onClothingItemClick: (Clothing) -> Unit // Callback khi click đồ
) {
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(SecWhite)
    ) {
        HorizontalDivider(color = TextLightBlue.copy(alpha = 0.1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Tủ đồ", "Nền", "Chữ").forEach { tab ->
                val isSelected = selectedTab.value == tab
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) AccentBlue else TextLightBlue.copy(alpha = 0.8f),
                        modifier = Modifier.noRippleClickable { selectedTab.value = tab }
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .height(3.dp)
                                .width(24.dp)
                                .clip(RoundedCornerShape(50))
                                .background(brush = GradientAccent)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = TextLightBlue.copy(0.1f))

        if (selectedTab.value == "Tủ đồ" || selectedTab.value == "Closet") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("Tất cả", "Áo", "Quần", "Phụ kiện")
                categories.forEach { cat ->
                    val isCatSelected = selectedCategory == cat
                    Surface(
                        shape = CircleShape,
                        color = if (isCatSelected) AccentBlue else Color.Transparent,
                        border = if (isCatSelected) null else BorderStroke(1.dp, TextLightBlue.copy(alpha = 0.3f)),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 12.sp,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCatSelected) Color.White else TextBlue,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // LOAD ĐỒ THẬT CỦA USER VÀO ĐÂY
            if (userClothes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tủ đồ trống", color = TextLightBlue)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
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
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF3F6FA))
                                .clickable { onClothingItemClick(clothing) }, // NÉM LÊN CANVAS
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = clothing.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.padding(8.dp),
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
            modifier = Modifier.height(46.dp)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                ModeButton(
                    text = "Sắp đặt",
                    isSelected = currentMode == StudioMode.FLAT_LAY,
                    onClick = { onModeChanged(StudioMode.FLAT_LAY) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                ModeButton(
                    text = "Ướm mẫu",
                    isSelected = currentMode == StudioMode.MANNEQUIN,
                    onClick = { onModeChanged(StudioMode.MANNEQUIN) }
                )
            }
        }
    }
}

@Composable
fun ModeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.fillMaxHeight().width(110.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isSelected) Modifier.background(brush = GradientSoft, shape = RoundedCornerShape(50))
                    else Modifier.background(Color.Transparent)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isSelected) Color.White else TextLightBlue.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun MannequinView(items: List<CanvasItem>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
            contentDescription = "Mannequin Base",
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .alpha(0.5f),
            contentScale = ContentScale.Fit
        )

        items.forEach { item ->
            val targetY = if (item.type == "TOP") (-100).dp else 100.dp

            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .offset(y = targetY)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(8.dp)
        ) {
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
    Surface(
        onClick = {},
        shape = CircleShape,
        color = SecWhite,
        shadowElevation = 4.dp,
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, style = MaterialTheme.typography.bodyLarge, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkBlue)
        }
    }
}

fun Modifier.alpha(alpha: Float) = this.graphicsLayer(alpha = alpha)

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onTap = { onClick() })
}
