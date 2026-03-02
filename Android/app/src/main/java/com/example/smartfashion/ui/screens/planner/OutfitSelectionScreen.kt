package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientPrimaryButton
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

data class SelectableOutfit(
    val id: String,
    val name: String,
    val imageUrl: String,
    val itemsCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitSelectionScreen(
    isSingleSelection: Boolean = false,
    onBackClick: () -> Unit = {},
    onConfirmClick: (List<String>) -> Unit = {}
) {
    val savedOutfits = remember {
        listOf(
            SelectableOutfit("1", "Set đi biển sáng", "https://i.postimg.cc/9MXZHYtp/3.jpg", 4),
            SelectableOutfit("2", "Tiệc tối sang trọng", "https://i.postimg.cc/9MXZHYtp/3.jpg", 3),
            SelectableOutfit("3", "Dạo phố thoải mái", "https://i.postimg.cc/9MXZHYtp/3.jpg", 2),
            SelectableOutfit("4", "Đi bar buổi tối", "https://i.postimg.cc/9MXZHYtp/3.jpg", 5),
            SelectableOutfit("5", "Ngủ (Pajamas)", "https://i.postimg.cc/9MXZHYtp/3.jpg", 2),
            SelectableOutfit("6", "Thể thao sáng sớm", "https://i.postimg.cc/9MXZHYtp/3.jpg", 3),
        )
    }

    val selectedIds = remember { mutableStateListOf<String>() }

    val titleText = if (isSingleSelection) "Chọn 1 bộ đồ" else "Thêm đồ vào Vali"
    val buttonText = if (isSingleSelection) "Xác nhận chọn" else "Thêm vào vali (${selectedIds.size})"

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = titleText,
                            style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                            fontWeight = FontWeight.Bold
                        )
                        if (!isSingleSelection) {
                            Text(
                                text = "Đã chọn ${selectedIds.size} bộ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 12.sp,
                                color = AccentBlue
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextDarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { onConfirmClick(selectedIds.toList()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .then(
                            if (selectedIds.isNotEmpty()) Modifier.background(GradientPrimaryButton, RoundedCornerShape(16.dp))
                            else Modifier
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.LightGray
                    ),
                    enabled = selectedIds.isNotEmpty()
                ) {
                    Text(
                        text = buttonText,
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
                .padding(horizontal = 24.dp)
        ) {
            // Thanh tìm kiếm nhanh
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Tìm theo tên (ví dụ: Đi làm, Tiệc...)", style = MaterialTheme.typography.bodyLarge, color = TextLightBlue) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = { Icon(Icons.Rounded.Search, null, tint = TextLightBlue) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = TextLightBlue.copy(alpha = 0.2f),
                    focusedContainerColor = SecWhite,
                    unfocusedContainerColor = SecWhite
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Grid hiển thị
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedOutfits) { outfit ->
                    val isSelected = selectedIds.contains(outfit.id)
                    SelectableOutfitCard(
                        outfit = outfit,
                        isSelected = isSelected,
                        onClick = {
                            if (isSingleSelection) {
                                // CHẾ ĐỘ 1: Chọn Lên Lịch -> Bấm bộ mới thì xóa bộ cũ đi (Chỉ giữ 1)
                                selectedIds.clear()
                                selectedIds.add(outfit.id)
                            } else {
                                // CHẾ ĐỘ 2: Chọn Bỏ Vali -> Tích/Bỏ tích thoải mái
                                if (isSelected) selectedIds.remove(outfit.id)
                                else selectedIds.add(outfit.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableOutfitCard(
    outfit: SelectableOutfit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) AccentBlue else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Box {
            // Ảnh và Thông tin
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.9f)
                        .background(Color(0xFFEBF2FA))
                ) {
                    AsyncImage(
                        model = outfit.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(AccentBlue.copy(alpha = 0.15f))
                        )
                    }
                }

                // Thông tin tên
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = outfit.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDarkBlue,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${outfit.itemsCount} món đồ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 12.sp,
                        color = TextLightBlue
                    )
                }
            }

            // Checkbox Icon ở góc
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(24.dp)
                        .background(AccentBlue, CircleShape)
                        .border(2.dp, SecWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(24.dp)
                        .background(SecWhite.copy(alpha = 0.8f), CircleShape)
                        .border(1.dp, TextLightBlue.copy(alpha = 0.3f), CircleShape)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Single Selection (For Calendar)")
@Composable
fun OutfitSelectionSinglePreview() {
    OutfitSelectionScreen(isSingleSelection = true)
}

@Preview(showBackground = true, name = "Multiple Selection (For Luggage)")
@Composable
fun OutfitSelectionMultiplePreview() {
    OutfitSelectionScreen(isSingleSelection = false)
}