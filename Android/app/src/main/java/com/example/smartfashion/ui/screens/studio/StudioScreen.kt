package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlin.math.roundToInt

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
    val imageUrl: String,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var scale: Float = 1f,
    var rotation: Float = 0f,
    val type: String = "TOP"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen() {
    var currentMode by remember { mutableStateOf(StudioMode.FLAT_LAY) }
    val canvasItems = remember { mutableStateListOf(
        CanvasItem("1", "https://i.postimg.cc/9MXZHYtp/3.jpg", offsetX = 0f, offsetY = -150f, type = "TOP"),
        CanvasItem("2", "https://i.postimg.cc/9MXZHYtp/3.jpg", offsetX = 0f, offsetY = 150f, type = "BOTTOM")
    )}
    val selectedTab = remember { mutableStateOf("Tủ đồ") }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(SecWhite)) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                        }
                    },
                    actions = {
                        TextButton(onClick = {}) {
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
            StudioBottomPanel(selectedTab = selectedTab)
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
                        DraggableImage(item)
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(12) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F6FA)),
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