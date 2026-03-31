package com.example.smartfashion.ui.screens.closet

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientAccent
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    navController: NavController,
    imageUri: String = "",
    originalImageUri: String = "",
    imageId: Int = 0,
    viewModel: AddItemViewModel = hiltViewModel()
){
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userId = tokenManager.getUserId()

    val scrollState = rememberScrollState()
    val isLoading by viewModel.isLoading.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val categories by viewModel.categories.collectAsState()
    val allTags by viewModel.tags.collectAsState()

    val isAiLoading by viewModel.isAiLoading.collectAsState()
    val aiAnalyzedData by viewModel.aiAnalyzedData.collectAsState()

    val seasonTags = allTags.filter { it.tagGroup == "Season" }.map { it.tagName }
    val weatherTags = allTags.filter { it.tagGroup == "Weather" }.map { it.tagName }
    val occasionTags = allTags.filter { it.tagGroup == "Occasion" }.map { it.tagName }
    val styleTags = allTags.filter { it.tagGroup == "Style" }.map { it.tagName }

    // XỬ LÝ LƯU THÀNH CÔNG VÀ CHUYỂN TRANG
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Thêm đồ thành công!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            navController.navigate("closet_screen") {
                popUpTo("closet_screen") { inclusive = true }
            }
        }
    }

    // XỬ LÝ NẾU SERVER BÁO LỖI
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    var itemName by remember { mutableStateOf("") }
    var colorHex by remember { mutableStateOf("") }
    var colorFamily by remember { mutableStateOf("") }
    var materialValue by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("") }
    var sizeValue by remember { mutableStateOf("") }
    var isShowOriginal by remember { mutableStateOf(false) }

    // --- Quản lý Category ---
    var showCategorySheet by remember { mutableStateOf(false) }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var showTagSheet by remember { mutableStateOf(false) }

    var selectedSeason by remember { mutableStateOf(setOf<String>()) }
    var selectedWeather by remember { mutableStateOf(setOf<String>()) }
    var selectedOccasion by remember { mutableStateOf(setOf<String>()) }
    var selectedStyle by remember { mutableStateOf(setOf<String>()) }

    // --- AI ĐIỀN TỰ ĐỘNG ---
    LaunchedEffect(aiAnalyzedData) {
        aiAnalyzedData?.let { data ->
            itemName = data.name
            colorHex = data.color_hex
            colorFamily = data.color_family
            materialValue = data.material
            selectedSeason = data.seasons.toSet()
            selectedWeather = data.weathers.toSet()
            selectedOccasion = data.occasions.toSet()
            selectedStyle = data.styles.toSet()

            val matchedCategory = categories.find { it.name.equals(data.category_name, ignoreCase = true) }
            if (matchedCategory != null) {
                selectedCategoryId = matchedCategory.categoryId
            }

            viewModel.resetAiData()
        }
    }

    val currentCategory = categories.find { it.categoryId == selectedCategoryId }
    val parentCategory = categories.find { it.categoryId == currentCategory?.parentId }

    val categoryDisplayText = if (currentCategory != null) {
        if (parentCategory != null) "${parentCategory.name} > ${currentCategory.name}" else currentCategory.name
    } else "Chọn phân loại"

    val allSelectedTags = selectedSeason + selectedWeather + selectedOccasion + selectedStyle
    val tagDisplayText = if (allSelectedTags.isEmpty()) "Thêm thẻ..." else allSelectedTags.joinToString(", ")

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = { Text("Thêm đồ mới", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextDarkBlue) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth().padding(20.dp), color = Color.Transparent) {
                Button(
                    onClick = {
                        val safeUserId = if (userId != -1) userId else 1
                        viewModel.saveNewItem(
                            userId = safeUserId,
                            imageId = imageId,
                            categoryId = selectedCategoryId ?: 1,
                            name = itemName.ifEmpty { "Món đồ mới" },
                            colorHex = colorHex,
                            colorFamily = colorFamily,
                            brand = brandName,
                            size = sizeValue,
                            material = materialValue,
                            imageUrl = imageUri,
                            seasons = selectedSeason,
                            weathers = selectedWeather,
                            occasions = selectedOccasion,
                            styles = selectedStyle
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextDarkBlue),
                    enabled = !isLoading && !isAiLoading && selectedCategoryId != null
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Lưu vào tủ đồ", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState)) {

            Box(modifier = Modifier.fillMaxWidth().height(320.dp).background(BgLight), contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(240.dp), shape = RoundedCornerShape(24.dp), color = SecWhite, shadowElevation = 2.dp) {
                    val uriToDisplay = if (isShowOriginal) originalImageUri else imageUri
                    val decodedUri = remember(uriToDisplay) { android.net.Uri.decode(uriToDisplay) }

                    if (decodedUri.isNotEmpty()) {
                        AsyncImage(
                            model = decodedUri,
                            contentDescription = if (isShowOriginal) "Ảnh gốc" else "Ảnh đã xóa nền",
                            modifier = Modifier.fillMaxSize().padding(if (isShowOriginal) 0.dp else 16.dp),
                            contentScale = if (isShowOriginal) ContentScale.Crop else ContentScale.Fit
                        )
                    } else {
                        Icon(Icons.Rounded.Checkroom, null, modifier = Modifier.padding(40.dp), tint = TextDarkBlue.copy(0.1f))
                    }
                }

                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButton(
                        label = if (isShowOriginal) "Gốc: Bật" else "Gốc: Tắt",
                        icon = if (isShowOriginal) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        onClick = { isShowOriginal = !isShowOriginal }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PaddingValues(horizontal = 24.dp).let {
                Button(
                    onClick = {
                        if (imageUri.isNotEmpty()) viewModel.analyzeImage(imageUri)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(50.dp)
                        .background(
                            brush = GradientAccent,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    contentPadding = PaddingValues(),
                    enabled = !isAiLoading && imageUri.isNotEmpty()
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI đang phân tích...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    } else {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI", tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Nhờ AI tự động điền thông tin", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(SecWhite).padding(24.dp)) {

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Tên trang phục") },
                    placeholder = { Text("AI sẽ tự động điền...", color = TextLightBlue.copy(0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = TextLightBlue.copy(0.3f),
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = TextDarkBlue
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                InfoRowElegant(
                    label = "Phân loại",
                    value = categoryDisplayText,
                    isPrimary = true,
                    onClick = { showCategorySheet = true }
                )

                InfoRowElegant(
                    label = "Gắn thẻ",
                    value = tagDisplayText,
                    isPrimary = true,
                    onClick = { showTagSheet = true }
                )

                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Màu sắc", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (colorHex.isNotEmpty()) {
                            Text(colorHex.uppercase(), style = MaterialTheme.typography.bodyLarge, color = TextLightBlue, modifier = Modifier.padding(end = 8.dp))
                            val parsedColor = try { Color(android.graphics.Color.parseColor(colorHex)) } catch (e: Exception) { Color.Transparent }
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(parsedColor).border(1.dp, TextLightBlue.copy(0.2f), CircleShape))
                        } else {
                            Text("AI sẽ phân tích...", style = MaterialTheme.typography.bodyLarge.copy(color = TextLightBlue.copy(0.5f)), modifier = Modifier.padding(end = 8.dp))
                        }
                    }
                }
                HorizontalDivider(color = BgLight)

                InputRowElegant(
                    label = "Chất liệu",
                    value = materialValue,
                    onValueChange = { materialValue = it },
                    placeholder = "VD: Cotton, Len, Da..."
                )

                InputRowElegant(
                    label = "Thương hiệu",
                    value = brandName,
                    onValueChange = { brandName = it },
                    placeholder = "Nhập thương hiệu"
                )

                InputRowElegant(
                    label = "Kích cỡ",
                    value = sizeValue,
                    onValueChange = { sizeValue = it },
                    placeholder = "S, M, L..."
                )

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        if (showCategorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showCategorySheet = false },
                containerColor = SecWhite
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Chọn phân loại chi tiết", style = MaterialTheme.typography.titleLarge, color = TextDarkBlue, modifier = Modifier.padding(bottom = 16.dp))

                    val rootCategories = categories.filter { it.parentId == null || it.parentId == 0 }

                    rootCategories.forEach { root ->
                        val childCategories = categories.filter { it.parentId == root.categoryId }

                        if (childCategories.isNotEmpty()) {
                            Text(text = root.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextDarkBlue, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(childCategories) { child ->
                                    val isSelected = selectedCategoryId == child.categoryId
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedCategoryId = child.categoryId
                                            showCategorySheet = false
                                        },
                                        label = { Text(child.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)) },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AccentBlue.copy(alpha = 0.15f),
                                            selectedLabelColor = AccentBlue,
                                            labelColor = TextLightBlue
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true, selected = isSelected,
                                            borderColor = if (isSelected) AccentBlue else TextLightBlue.copy(0.2f),
                                            selectedBorderColor = AccentBlue, borderWidth = if (isSelected) 1.5.dp else 1.dp
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showTagSheet) {
            ModalBottomSheet(
                onDismissRequest = { showTagSheet = false },
                containerColor = SecWhite
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).verticalScroll(rememberScrollState())
                ) {
                    Text("Chọn thẻ", style = MaterialTheme.typography.titleLarge, color = TextDarkBlue, modifier = Modifier.padding(bottom = 16.dp))

                    if (seasonTags.isNotEmpty()) MultiSelectTagGroup("Mùa", seasonTags, selectedSeason) { selectedSeason = it }
                    if (weatherTags.isNotEmpty()) MultiSelectTagGroup("Thời tiết", weatherTags, selectedWeather) { selectedWeather = it }
                    if (occasionTags.isNotEmpty()) MultiSelectTagGroup("Dịp mặc", occasionTags, selectedOccasion) { selectedOccasion = it }
                    if (styleTags.isNotEmpty()) MultiSelectTagGroup("Phong cách", styleTags, selectedStyle) { selectedStyle = it }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showTagSheet = false },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextDarkBlue)
                    ) {
                        Text("Xong", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun MultiSelectTagGroup(title: String, options: List<String>, selectedItems: Set<String>, onSelectionChange: (Set<String>) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextDarkBlue)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(end = 16.dp)) {
            items(options) { option ->
                val isSelected = selectedItems.contains(option)
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectionChange(if (isSelected) selectedItems - option else selectedItems + option) },
                    label = { Text(option, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AccentBlue.copy(alpha = 0.15f), selectedLabelColor = AccentBlue, labelColor = TextLightBlue),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected, borderColor = if (isSelected) AccentBlue else TextLightBlue.copy(0.2f), selectedBorderColor = AccentBlue, borderWidth = if (isSelected) 1.5.dp else 1.dp)
                )
            }
        }
    }
}

@Composable
fun ActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(12.dp), color = Color.White.copy(0.9f), shadowElevation = 4.dp) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = TextDarkBlue)
            Text(" $label", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold), color = TextDarkBlue)
        }
    }
}

@Composable
fun InfoRowElegant(label: String, value: String, isPrimary: Boolean = false, onClick: () -> Unit = {}) {
    Column {
        Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = TextDarkBlue)
            Row(modifier = Modifier.weight(1f).padding(start = 16.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(color = if (isPrimary) AccentBlue else TextLightBlue, fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal), maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.End, modifier = Modifier.weight(1f, fill = false))
                Icon(Icons.Rounded.ChevronRight, null, tint = TextLightBlue.copy(0.5f))
            }
        }
        HorizontalDivider(color = BgLight)
    }
}

@Composable
fun InputRowElegant(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String = "") {
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = TextDarkBlue)
            Box(modifier = Modifier.weight(1f).padding(start = 16.dp), contentAlignment = Alignment.CenterEnd) {
                if (value.isEmpty()) Text(text = placeholder, style = MaterialTheme.typography.bodyLarge.copy(color = TextLightBlue.copy(0.5f), textAlign = TextAlign.End))
                BasicTextField(value = value, onValueChange = onValueChange, textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextLightBlue, textAlign = TextAlign.End), singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        }
        HorizontalDivider(color = BgLight)
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemScreenPreview() { AddItemScreen(navController = rememberNavController()) }