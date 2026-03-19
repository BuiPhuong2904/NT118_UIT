package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    navController: NavController,
    clothingId: Int,
    viewModel: ItemDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(clothingId) {
        viewModel.fetchClothingDetail(clothingId)
    }

    val item by viewModel.clothingItem.collectAsState()
    val categoryName by viewModel.categoryName.collectAsState()
    val scrollState = rememberScrollState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusMenu by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize().background(BgLight), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentBlue)
        }
        return
    }

    val safeItem = item!!

    var editName by remember(safeItem) { mutableStateOf(safeItem.name) }
    var editStatus by remember(safeItem) { mutableStateOf(safeItem.status) }
    var editBrand by remember(safeItem) { mutableStateOf(safeItem.brandName ?: "") }
    var editMaterial by remember(safeItem) { mutableStateOf(safeItem.material ?: "") }
    var editSize by remember(safeItem) { mutableStateOf(safeItem.size ?: "") }

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(safeItem.colorHex ?: "#CCCCCC"))
    } catch (e: Exception) { Color.LightGray }

    val currentStatus = if (isEditing) editStatus else safeItem.status
    val (statusText, statusBgColor, statusTextColor) = when(currentStatus) {
        "active" -> Triple("Đang dùng", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "in_wash" -> Triple("Đang giặt", Color(0xFFE3F2FD), Color(0xFF1565C0))
        "archived" -> Triple("Đã cất tủ", Color(0xFFF5F0E6), Color(0xFF6D4C41))
        else -> Triple("Không rõ", Color(0xFFFFEBEE), Color(0xFFC62828))
    }

    val lastWornText = remember(safeItem.lastWorn) {
        safeItem.lastWorn?.let { date ->
            val diffInMillis = System.currentTimeMillis() - date.time
            val days = diffInMillis / (1000 * 60 * 60 * 24)
            when {
                days <= 0 -> "Hôm nay"
                days == 1L -> "Hôm qua"
                else -> "$days ngày trước"
            }
        } ?: "Chưa mặc"
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa món đồ này?", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa khỏi tủ đồ không? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteClothing(clothingId) { navController.popBackStack() }
                }) { Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy", color = TextDarkBlue) }
            }
        )
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Chỉnh sửa đồ" else "Chi tiết món đồ", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isEditing) isEditing = false else navController.popBackStack()
                    }) { Icon(if (isEditing) Icons.Rounded.Close else Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDarkBlue) }
                },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = {
                            viewModel.updateClothingDetails(
                                safeItem.copy(
                                    name = editName,
                                    status = editStatus,
                                    brandName = editBrand.ifEmpty { null },
                                    material = editMaterial.ifEmpty { null },
                                    size = editSize.ifEmpty { null }
                                )
                            )
                            isEditing = false

                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Đã lưu thay đổi thành công!",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }) { Icon(Icons.Rounded.Check, "Save", tint = AccentBlue) }
                    } else {
                        IconButton(onClick = { isEditing = true }) { Icon(Icons.Rounded.Edit, "Edit", tint = TextLightBlue) }
                        IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Rounded.DeleteOutline, "Delete", tint = TextPink) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            if (!isEditing) {
                Surface(modifier = Modifier.fillMaxWidth().padding(20.dp), color = Color.Transparent) {
                    Button(
                        onClick = { /* Sang màn hình Mix & Match */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextDarkBlue)
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Gợi ý phối đồ", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        // BỌC TOÀN BỘ NỘI DUNG VÀO MỘT THẺ BOX
        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState)) {
                Box(modifier = Modifier.fillMaxWidth().height(350.dp).background(BgLight), contentAlignment = Alignment.Center) {
                    Surface(modifier = Modifier.size(280.dp), shape = RoundedCornerShape(24.dp), color = SecWhite, shadowElevation = 4.dp) {
                        AsyncImage(
                            model = safeItem.imageUrl,
                            contentDescription = safeItem.name,
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(SecWhite).padding(24.dp)
                ) {
                    if (isEditing) {
                        BasicTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            textStyle = MaterialTheme.typography.titleLarge.copy(color = AccentBlue, fontSize = 26.sp),
                            singleLine = true,
                            cursorBrush = SolidColor(AccentBlue)
                        )
                    } else {
                        Text(text = safeItem.name, style = MaterialTheme.typography.titleLarge, color = TextDarkBlue, fontSize = 26.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = statusBgColor,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(enabled = isEditing) { showStatusMenu = true }
                            ) {
                                Text(
                                    text = if (isEditing) "$statusText ▾" else statusText,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp),
                                    color = statusTextColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showStatusMenu,
                                onDismissRequest = { showStatusMenu = false },
                                modifier = Modifier.background(SecWhite)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Đang dùng", color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium) },
                                    onClick = { editStatus = "active"; showStatusMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Đang giặt", color = Color(0xFF1565C0), fontWeight = FontWeight.Medium) },
                                    onClick = { editStatus = "in_wash"; showStatusMenu = false }
                                )
                                DropdownMenuItem(
                                    text = { Text("Đã cất tủ", color = Color(0xFF6D4C41), fontWeight = FontWeight.Medium) },
                                    onClick = { editStatus = "archived"; showStatusMenu = false }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "• Mặc lần cuối: $lastWornText",
                            style = MaterialTheme.typography.bodyLarge, color = TextLightBlue, fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = BgLight)

                    DetailRowElegant("Danh mục", categoryName, isPrimary = true, isEditing = false)
                    DetailRowElegant("Thương hiệu", editBrand, isEditing = isEditing, onValueChange = { editBrand = it })

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Màu sắc", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                safeItem.colorFamily ?: "Không rõ",
                                style = MaterialTheme.typography.bodyLarge, color = TextLightBlue, modifier = Modifier.padding(end = 8.dp)
                            )
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(parsedColor).border(1.dp, TextLightBlue.copy(0.2f), CircleShape))
                        }
                    }
                    HorizontalDivider(color = BgLight)

                    DetailRowElegant("Chất liệu", editMaterial, isEditing = isEditing, onValueChange = { editMaterial = it })
                    DetailRowElegant("Kích cỡ", editSize, isEditing = isEditing, onValueChange = { editSize = it })

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }

            // ĐẶT SNACKBAR Ở ĐÂY ĐỂ NÓ NỔI LÊN TRÊN TOP VÀ DƯỚI HEADER
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = paddingValues.calculateTopPadding() + 8.dp)
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun DetailRowElegant(
    label: String,
    value: String,
    isPrimary: Boolean = false,
    isEditing: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = TextDarkBlue)

            if (isEditing && !isPrimary) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f).padding(start = 16.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = AccentBlue,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(AccentBlue),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            if (value.isEmpty()) {
                                Text("Nhập thông tin", color = TextLightBlue.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyLarge)
                            }
                            innerTextField()
                        }
                    }
                )
            } else {
                Text(
                    text = value.ifEmpty { "Không rõ" },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (isPrimary) AccentBlue else TextLightBlue,
                        fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal
                    ),
                    modifier = Modifier.padding(start = 16.dp),
                    textAlign = TextAlign.End
                )
            }
        }
        HorizontalDivider(color = BgLight)
    }
}