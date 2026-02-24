package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlin.math.roundToInt

// Màu chủ đạo
val StudioPrimary = Color(0xFF6200EE)
val CanvasBackground = Color(0xFFEBF2FA)

// Enum xác định chế độ
enum class StudioMode {
    FLAT_LAY, // Sắp đặt tự do
    MANNEQUIN // Ướm thử người mẫu
}

// Data Model giả lập
data class CanvasItem(
    val id: String,
    val imageUrl: String,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var scale: Float = 1f,
    var rotation: Float = 0f,
    val type: String = "TOP" // Loại đồ để ướm lên Mannequin (TOP, BOTTOM, SHOES)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen() {
    // State quản lý chế độ hiện tại
    var currentMode by remember { mutableStateOf(StudioMode.FLAT_LAY) }

    // State danh sách đồ (Dùng chung cho cả 2 chế độ, nhưng cách hiển thị khác nhau)
    val canvasItems = remember { mutableStateListOf(
        CanvasItem("1", "https://i.postimg.cc/9MXZHYtp/3.jpg", offsetX = 0f, offsetY = -150f, type = "TOP"),
        CanvasItem("2", "https://i.postimg.cc/9MXZHYtp/3.jpg", offsetX = 0f, offsetY = 150f, type = "BOTTOM")
    )}

    val selectedTab = remember { mutableStateOf("Closet") }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        TextButton(onClick = {}) {
                            Text("Save", fontWeight = FontWeight.Bold, color = StudioPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                // --- THANH CHUYỂN ĐỔI CHẾ ĐỘ (Tab Switcher) ---
                ModeSwitcher(
                    currentMode = currentMode,
                    onModeChanged = { currentMode = it }
                )
            }
        },
        bottomBar = {
            StudioBottomPanel(selectedTab = selectedTab)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // KHU VỰC CHÍNH (CANVAS HOẶC MANNEQUIN)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(CanvasBackground)
                    .clipToBounds()
            ) {
                if (currentMode == StudioMode.MANNEQUIN) {
                    // --- CHẾ ĐỘ MANNEQUIN ---
                    MannequinView(canvasItems)
                } else {
                    // --- CHẾ ĐỘ FLAT-LAY ---
                    canvasItems.forEach { item ->
                        DraggableImage(item)
                    }
                }
            }
        }
    }
}

// Composable: Thanh chuyển đổi chế độ Sắp đặt / Ướm thử
@Composable
fun ModeSwitcher(currentMode: StudioMode, onModeChanged: (StudioMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFFF0F0F0),
            modifier = Modifier.height(40.dp)
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                ModeButton(
                    text = "Flat-lay",
                    isSelected = currentMode == StudioMode.FLAT_LAY,
                    onClick = { onModeChanged(StudioMode.FLAT_LAY) }
                )
                Spacer(modifier = Modifier.width(4.dp))
                ModeButton(
                    text = "Mannequin",
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
            containerColor = if (isSelected) Color.White else Color.Transparent,
            contentColor = if (isSelected) Color.Black else Color.Gray
        ),
        elevation = if (isSelected) ButtonDefaults.buttonElevation(2.dp) else ButtonDefaults.buttonElevation(0.dp),
        shape = RoundedCornerShape(50),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

// --- GIAO DIỆN NGƯỜI MẪU (Mannequin View) ---
@Composable
fun MannequinView(items: List<CanvasItem>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ảnh Người mẫu nền (Body Base)
        // Bạn thay link ảnh này bằng ảnh Mannequin rỗng của bạn
        AsyncImage(
            model = "https://i.postimg.cc/9MXZHYtp/3.jpg", // Tạm dùng ảnh demo, thay bằng ảnh Body Base
            contentDescription = "Mannequin Base",
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .alpha(0.5f), // Làm mờ ảnh demo để dễ hình dung
            contentScale = ContentScale.Fit
        )

        // 2. Các món đồ ướm lên người
        // Logic: Ở chế độ này, quần áo sẽ tự động vào vị trí (hoặc kéo thả hạn chế)
        items.forEach { item ->
            // Ví dụ đơn giản: Xếp áo lên trên, quần xuống dưới
            val targetY = if (item.type == "TOP") (-100).dp else 100.dp

            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(140.dp)
                    .offset(y = targetY)
            )
        }

        // Nút điều chỉnh Body (Màu da, Tóc...)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(8.dp)
        ) {
            SmallToolButton(text = "Skin")
            Spacer(modifier = Modifier.height(8.dp))
            SmallToolButton(text = "Hair")
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
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Extension function sửa lỗi alpha (nếu chưa có)
fun Modifier.alpha(alpha: Float) = this.graphicsLayer(alpha = alpha)


// --- GIAO DIỆN SẮP ĐẶT (Giữ nguyên logic cũ) ---
@Composable
fun DraggableImage(item: CanvasItem) {
    var offsetX by remember { mutableFloatStateOf(item.offsetX) }
    var offsetY by remember { mutableFloatStateOf(item.offsetY) }
    var scale by remember { mutableFloatStateOf(item.scale) }
    var rotation by remember { mutableFloatStateOf(item.rotation) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation
            )
            .size(150.dp)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotate ->
                    scale *= zoom
                    rotation += rotate
                    offsetX += pan.x
                    offsetY += pan.y
                }
            }
    ) {
        Image(
            painter = rememberAsyncImagePainter(item.imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun StudioBottomPanel(selectedTab: MutableState<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Giảm chiều cao chút cho cân đối
            .background(Color.White)
    ) {
        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))

        // Hàng Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Closet", "Backgrounds", "Text").forEach { tab ->
                val isSelected = selectedTab.value == tab
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = tab,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.Gray,
                        modifier = Modifier.noRippleClickable { selectedTab.value = tab }
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color.LightGray.copy(0.3f))

        // Nội dung Tab Closet
        if (selectedTab.value == "Closet") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(onClick = {}, label = { Text("All") }, shape = CircleShape)
                SuggestionChip(onClick = {}, label = { Text("Tops") }, shape = CircleShape)
                SuggestionChip(onClick = {}, label = { Text("Bottoms") }, shape = CircleShape)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(12) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter("https://i.postimg.cc/9MXZHYtp/3.jpg"),
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onTap = { onClick() })
}

@Preview(showBackground = true)
@Composable
fun StudioScreenPreview() {
    StudioScreen()
}