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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.model.Outfit

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientPrimaryButton
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitSelectionScreen(
    navController: NavController,
    viewModel: OutfitSelectionViewModel = hiltViewModel(),
    isSingleSelection: Boolean = true // Mặc định true cho Calendar
) {
    val context = LocalContext.current
    val userId = remember { TokenManager(context).getUserId() }

    // Dữ liệu động từ ViewModel
    val outfits by viewModel.outfits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Khởi tạo lấy dữ liệu
    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.loadOutfits(userId)
        }
    }

    // Danh sách ID đang được chọn (Dùng Int vì ID thật trong DB là số)
    val selectedIds = remember { mutableStateListOf<Int>() }

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
                    IconButton(onClick = { navController.popBackStack() }) {
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
                    onClick = {
                        // KHI BẤM NÚT XÁC NHẬN CHỌN
                        if (isSingleSelection && selectedIds.isNotEmpty()) {
                            val selectedId = selectedIds.first()
                            val selectedOutfit = outfits.find { it.outfitId == selectedId }

                            if (selectedOutfit != null) {
                                // Truyền dữ liệu về lại cho ScheduleBottomSheet
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedOutfitId", selectedOutfit.outfitId)
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedOutfitName", selectedOutfit.name)
                                navController.previousBackStackEntry?.savedStateHandle?.set("selectedOutfitImage", selectedOutfit.imagePreviewUrl)

                                // Đóng màn hình này lại
                                navController.popBackStack()
                            }
                        } else {
                            // Xử lý lưu nhiều bộ đồ cho tính năng Vali Du Lịch (Sẽ code sau)
                        }
                    },
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            } else if (outfits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tủ đồ của bạn chưa có bộ phối nào.", color = TextLightBlue)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(outfits) { outfit ->
                        val isSelected = selectedIds.contains(outfit.outfitId)
                        SelectableOutfitCard(
                            outfit = outfit,
                            isSelected = isSelected,
                            onClick = {
                                outfit.outfitId?.let { id ->
                                    if (isSingleSelection) {
                                        selectedIds.clear()
                                        selectedIds.add(id)
                                    } else {
                                        if (isSelected) selectedIds.remove(id)
                                        else selectedIds.add(id)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableOutfitCard(
    outfit: Outfit, // Nhận đối tượng Outfit thật
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
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.9f)
                        .background(Color(0xFFEBF2FA))
                ) {
                    AsyncImage(
                        model = outfit.imagePreviewUrl ?: "https://i.postimg.cc/9MXZHYtp/3.jpg",
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (isSelected) {
                        Box(modifier = Modifier.fillMaxSize().background(AccentBlue.copy(alpha = 0.15f)))
                    }
                }

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
                        text = "${outfit.clothes?.size ?: 0} món đồ", // Lấy số lượng quần áo thật
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 12.sp,
                        color = TextLightBlue
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(24.dp).background(AccentBlue, CircleShape).border(2.dp, SecWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            } else {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(24.dp).background(SecWhite.copy(alpha = 0.8f), CircleShape).border(1.dp, TextLightBlue.copy(alpha = 0.3f), CircleShape))
            }
        }
    }
}