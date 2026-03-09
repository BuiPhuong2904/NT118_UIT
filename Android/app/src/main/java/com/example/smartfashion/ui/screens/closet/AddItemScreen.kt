package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Giả định các màu bạn đã import, nhớ giữ nguyên đường dẫn import của bạn nhé!
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    // States cho các trường dữ liệu
    var itemName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Váy > Váy Midi") }

    // States cho các tag (cho phép chọn nhiều - Set)
    var selectedSeason by remember { mutableStateOf(setOf("Hè")) }
    var selectedWeather by remember { mutableStateOf(setOf<String>()) }
    var selectedOccasion by remember { mutableStateOf(setOf("Đi chơi")) }
    var selectedStyle by remember { mutableStateOf(setOf<String>()) }

    var brandName by remember { mutableStateOf("Zara") }
    var sizeValue by remember { mutableStateOf("M") }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Thêm đồ mới",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextDarkBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { /* Logic lưu vào bảng Clothes */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextDarkBlue)
                ) {
                    Text(
                        "Lưu vào tủ đồ",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // Vùng chứa Ảnh trang phục
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(BgLight),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(240.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = SecWhite,
                    shadowElevation = 2.dp
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Checkroom,
                        contentDescription = null,
                        modifier = Modifier.padding(40.dp),
                        tint = TextDarkBlue.copy(0.1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActionChip(label = "Gốc: Tắt", icon = Icons.Rounded.VisibilityOff)
                    ActionChip(label = "Sửa", icon = Icons.Rounded.Edit)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vùng thông tin chi tiết (Nền trắng bo góc)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(SecWhite)
                    .padding(24.dp)
            ) {
                // 1. Nhập Tên trang phục
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Tên trang phục") },
                    placeholder = { Text("VD: Váy hoa nhí dạo phố...", color = TextLightBlue.copy(0.5f)) },
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

                // 2. Các nhóm Tags chọn nhiều
                MultiSelectTagGroup(
                    title = "Mùa sử dụng",
                    options = listOf("Xuân", "Hè", "Thu", "Đông"),
                    selectedItems = selectedSeason,
                    onSelectionChange = { selectedSeason = it }
                )

                MultiSelectTagGroup(
                    title = "Thời tiết",
                    options = listOf("Nắng nóng", "Mát mẻ", "Se lạnh", "Mưa", "Khô hanh"),
                    selectedItems = selectedWeather,
                    onSelectionChange = { selectedWeather = it }
                )

                MultiSelectTagGroup(
                    title = "Dịp mặc",
                    options = listOf("Đi làm", "Đi chơi", "Tiệc tùng", "Thể thao", "Ở nhà"),
                    selectedItems = selectedOccasion,
                    onSelectionChange = { selectedOccasion = it }
                )

                MultiSelectTagGroup(
                    title = "Phong cách",
                    options = listOf("Thanh lịch", "Năng động", "Vintage", "Minimalist", "Cá tính"),
                    selectedItems = selectedStyle,
                    onSelectionChange = { selectedStyle = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BgLight, thickness = 2.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // 3. Thông tin chi tiết khác
                InfoRowElegant("Phân loại", selectedCategory, isPrimary = true)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Màu sắc",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDarkBlue
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Đen",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextLightBlue,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(1.dp, TextLightBlue.copy(0.2f), CircleShape)
                        )
                        Icon(Icons.Rounded.ChevronRight, null, tint = TextLightBlue.copy(0.5f))
                    }
                }
                HorizontalDivider(color = BgLight)

                InfoRowElegant("Thương hiệu", brandName, isPrimary = false)
                InfoRowElegant("Chất liệu", "Cotton / Lụa", isPrimary = false)
                InfoRowElegant("Kích cỡ", sizeValue, isPrimary = false)

                Spacer(modifier = Modifier.height(120.dp)) // Chừa không gian cho nút Lưu
            }
        }
    }
}

// ----------------------------------------------------
// COMPONENTS PHỤ TRỢ MỚI & ĐÃ CẬP NHẬT
// ----------------------------------------------------

@Composable
fun MultiSelectTagGroup(
    title: String,
    options: List<String>,
    selectedItems: Set<String>,
    onSelectionChange: (Set<String>) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextDarkBlue
        )
        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(options) { option ->
                val isSelected = selectedItems.contains(option)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newSelection = if (isSelected) {
                            selectedItems - option
                        } else {
                            selectedItems + option
                        }
                        onSelectionChange(newSelection)
                    },
                    label = {
                        Text(
                            option,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentBlue.copy(alpha = 0.15f),
                        selectedLabelColor = AccentBlue,
                        labelColor = TextLightBlue
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) AccentBlue else TextLightBlue.copy(0.2f),
                        selectedBorderColor = AccentBlue,
                        borderWidth = if (isSelected) 1.5.dp else 1.dp
                    )
                )
            }
        }
    }
}

@Composable
fun ActionChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(0.9f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = TextDarkBlue)
            Text(
                " $label",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                color = TextDarkBlue
            )
        }
    }
}

@Composable
fun InfoRowElegant(label: String, value: String, isPrimary: Boolean = false) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium,
                color = TextDarkBlue
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (isPrimary) AccentBlue else TextLightBlue,
                        fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal
                    )
                )
                Icon(Icons.Rounded.ChevronRight, null, tint = TextLightBlue.copy(0.5f))
            }
        }
        HorizontalDivider(color = BgLight)
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemScreenPreview() {
    AddItemScreen(navController = rememberNavController())
}