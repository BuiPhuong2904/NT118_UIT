package com.example.smartfashion.ui.screens.hub

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartfashion.ui.screens.studio.ItemBorderColor
import com.example.smartfashion.ui.theme.*
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

// Bảng 12 màu chuẩn xác
val colorWheel = listOf(
    Color(0xFFFF0000), Color(0xFFFF5500), Color(0xFFFFAA00), Color(0xFFFFD400),
    Color(0xFFFFFF00), Color(0xFFAAFF00), Color(0xFF00FF00), Color(0xFF00FFAA),
    Color(0xFF00FFFF), Color(0xFF00AAFF), Color(0xFF0000FF), Color(0xFFAA00FF)
)

enum class HarmonyRule(val title: String, val desc: String) {
    COMPLEMENTARY("Tương phản", "Hai màu đối diện nhau trực tiếp trên bánh xe. Rất nổi bật và táo bạo."),
    ANALOGOUS("Tương đồng", "Ba màu nằm liền kề nhau. Tạo cảm giác hài hòa, êm dịu, thanh lịch."),
    TRIADIC("Bổ túc bộ 3", "Ba màu cách đều nhau tạo thành tam giác đều. Cân bằng và đa dạng."),
    SPLIT_COMPLEMENTARY("Chữ Y", "Một màu kết hợp với hai màu kề bên của màu đối diện nó. Ít gắt hơn và tương phản trực tiếp.")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorWheelScreen(navController: NavController) {
    var selectedColorIndex by remember { mutableStateOf(0) }
    var selectedRule by remember { mutableStateOf(HarmonyRule.COMPLEMENTARY) }

    var targetAngle by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(selectedColorIndex) {
        val newTarget = selectedColorIndex * (360f / 12f)
        val diff = (newTarget - targetAngle) % 360f
        val shortestDiff = if (diff > 180f) diff - 360f else if (diff < -180f) diff + 360f else diff
        targetAngle += shortestDiff
    }

    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "angleRotation"
    )

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bánh xe Màu sắc", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", tint = TextDarkBlue) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. TABS CHỌN QUY TẮC PHỐI MÀU
            Surface(color = SecWhite, shape = RoundedCornerShape(50), shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(4.dp)) {
                    HarmonyRule.entries.take(2).forEach { rule ->
                        RuleTab(rule, selectedRule, Modifier.weight(1f)) { selectedRule = rule }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = SecWhite, shape = RoundedCornerShape(50), shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(4.dp)) {
                    HarmonyRule.entries.drop(2).forEach { rule ->
                        RuleTab(rule, selectedRule, Modifier.weight(1f)) { selectedRule = rule }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. VÒNG QUAY BÁNH XE
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(SecWhite)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            var angle = Math.toDegrees(atan2((offset.y - centerY).toDouble(), (offset.x - centerX).toDouble())).toFloat()
                            if (angle < 0) angle += 360f
                            val segmentAngle = 360f / 12f
                            selectedColorIndex = ((angle + segmentAngle / 2) / segmentAngle).toInt() % 12
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val sweepAngle = 360f / 12f
                    colorWheel.forEachIndexed { index, color ->
                        drawArc(color = color, startAngle = index * sweepAngle - sweepAngle / 2, sweepAngle = sweepAngle, useCenter = true, size = Size(size.width, size.height))
                    }

                    drawCircle(color = SecWhite, radius = size.width / 4f)

                    val radius = size.width / 2.8f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    withTransform({
                        rotate(degrees = animatedAngle, pivot = center)
                    }) {
                        fun getLocalPoint(steps: Int): Offset {
                            val angleRad = (steps * sweepAngle) * (PI / 180f)
                            return Offset(center.x + radius * cos(angleRad).toFloat(), center.y + radius * sin(angleRad).toFloat())
                        }

                        val mainP = getLocalPoint(0)
                        val strokeW = Stroke(width = 8f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
                        val strokeColor = Stroke(width = 4f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)

                        when (selectedRule) {
                            HarmonyRule.COMPLEMENTARY -> {
                                val opp = getLocalPoint(6)
                                drawLine(Color.White, mainP, opp, strokeWidth = 10f)
                                drawLine(TextDarkBlue, mainP, opp, strokeWidth = 4f)
                            }
                            HarmonyRule.ANALOGOUS -> {
                                val pLeft = getLocalPoint(1)
                                val pRight = getLocalPoint(-1)
                                val path = Path().apply { moveTo(pLeft.x, pLeft.y); lineTo(mainP.x, mainP.y); lineTo(pRight.x, pRight.y) }
                                drawPath(path, Color.White, style = strokeW)
                                drawPath(path, TextDarkBlue, style = strokeColor)
                            }
                            HarmonyRule.TRIADIC -> {
                                val p2 = getLocalPoint(4)
                                val p3 = getLocalPoint(8)
                                val path = Path().apply { moveTo(mainP.x, mainP.y); lineTo(p2.x, p2.y); lineTo(p3.x, p3.y); close() }
                                drawPath(path, Color.White, style = strokeW)
                                drawPath(path, TextDarkBlue, style = strokeColor)
                            }
                            HarmonyRule.SPLIT_COMPLEMENTARY -> {
                                val p2 = getLocalPoint(5)
                                val p3 = getLocalPoint(7)
                                val path = Path().apply { moveTo(p2.x, p2.y); lineTo(mainP.x, mainP.y); lineTo(p3.x, p3.y) }
                                drawPath(path, Color.White, style = strokeW)
                                drawPath(path, TextDarkBlue, style = strokeColor)
                            }
                        }

                        drawCircle(Color.White, radius = 24f, center = mainP)
                        drawCircle(TextDarkBlue, radius = 18f, center = mainP)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. HIỂN THỊ CÁC MÀU KẾT QUẢ VỚI ANIMATION POP-UP
            val resultingColors = when (selectedRule) {
                HarmonyRule.COMPLEMENTARY -> listOf(colorWheel[selectedColorIndex], colorWheel[(selectedColorIndex + 6) % 12])
                HarmonyRule.ANALOGOUS -> listOf(colorWheel[(selectedColorIndex + 11) % 12], colorWheel[selectedColorIndex], colorWheel[(selectedColorIndex + 1) % 12])
                HarmonyRule.TRIADIC -> listOf(colorWheel[selectedColorIndex], colorWheel[(selectedColorIndex + 4) % 12], colorWheel[(selectedColorIndex + 8) % 12])
                HarmonyRule.SPLIT_COMPLEMENTARY -> listOf(colorWheel[selectedColorIndex], colorWheel[(selectedColorIndex + 5) % 12], colorWheel[(selectedColorIndex + 7) % 12])
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SecWhite),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Phối màu: ${selectedRule.title}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextBlue)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        resultingColors.forEachIndexed { index, c ->
                            var trigger by remember { mutableStateOf(false) }
                            LaunchedEffect(c) {
                                trigger = false
                                trigger = true
                            }
                            val scale by animateFloatAsState(
                                targetValue = if (trigger) 1f else 0.5f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                                label = "scaleAnim"
                            )

                            Surface(
                                shape = CircleShape, color = c, shadowElevation = 4.dp,
                                modifier = Modifier.size(56.dp).scale(scale)
                            ) {}
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = ItemBorderColor)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Info, null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = selectedRule.desc,
                            style = MaterialTheme.typography.bodyMedium, color = TextLightBlue,
                            lineHeight = 22.sp, textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RuleTab(rule: HarmonyRule, selectedRule: HarmonyRule, modifier: Modifier, onClick: () -> Unit) {
    val isSelected = selectedRule == rule
    Box(
        modifier = modifier.clip(RoundedCornerShape(50)).background(if (isSelected) AccentBlue else Color.Transparent).clickable { onClick() }.padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = rule.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else TextLightBlue, fontSize = 12.sp)
    }
}