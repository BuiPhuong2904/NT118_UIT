package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Favorite // <-- THÊM IMPORT NÀY
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

import com.example.smartfashion.model.Outfit
import com.example.smartfashion.ui.components.BottomNavigationBar
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SavedOutfitsScreen(
    navController: NavController,
    viewModel: OutfitViewModel = hiltViewModel()
) {
    // 1. STATE ĐỂ QUẢN LÝ LỌC NHIỀU MỤC
    var isFavorite by remember { mutableStateOf(false) }
    var selectedTags by remember { mutableStateOf<Set<String>>(emptySet()) }

    val outfits by viewModel.outfits.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Lắng nghe sự thay đổi của bộ lọc và tự động gọi API
    LaunchedEffect(isFavorite, selectedTags) {
        viewModel.fetchOutfits(
            userId = 1,
            isFavorite = if (isFavorite) true else null,
            tags = if (selectedTags.isNotEmpty()) selectedTags.toList() else null
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgLight)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp)) {
                    SavedOutfitsHeader()
                }

                Box(modifier = Modifier.padding(bottom = 16.dp)) {
                    // Truyền State xuống component bộ lọc
                    OutfitFilterTabs(
                        isFavorite = isFavorite,
                        selectedTags = selectedTags,
                        onFavoriteToggle = {
                            isFavorite = !isFavorite
                            selectedTags = emptySet() // Thích thì reset tag khi đổi mode, không thì bỏ dòng này
                        },
                        onTagToggle = { tag ->
                            isFavorite = false // Khi chọn tag thì bỏ chọn Tất cả/Yêu thích
                            selectedTags = if (selectedTags.contains(tag)) {
                                selectedTags - tag
                            } else {
                                selectedTags + tag
                            }
                        },
                        onClearAll = {
                            isFavorite = false
                            selectedTags = emptySet()
                        }
                    )
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController = navController, selectedItem = 2) },
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .background(BgLight),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding() + 20.dp,
                    start = 20.dp,
                    end = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    CreateNewOutfitCard(onClick = { navController.navigate("studio_screen") })
                }

                items(outfits.size) { index ->
                    val outfit = outfits[index]
                    OutfitItemCard(
                        outfit = outfit,
                        onClick = {
                            outfit.outfitId?.let { id ->
                                navController.navigate("outfit_detail_screen/$id")
                            }
                        },
                        // Gọi hàm thả tim từ ViewModel
                        onFavoriteClick = {
                            outfit.outfitId?.let { id ->
                                // Truyền id bộ đồ và trạng thái đảo ngược (nếu đang thích thì thành ko thích và ngược lại)
                                viewModel.toggleFavorite(id, !outfit.isFavorite)
                            }
                        }
                    )
                }
            }
        }
    }
}

// --- HEADER ---
@Composable
fun SavedOutfitsHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Phối đồ",
                style = MaterialTheme.typography.headlineMedium.copy(
                    brush = GradientText
                ),
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Bộ sưu tập & Ý tưởng",
                style = MaterialTheme.typography.titleMedium,
                color = TextBlue,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Notifications, contentDescription = "Thông báo", tint = TextPink)
            }
            IconButton(onClick = {}) {
                Icon(Icons.Outlined.Settings, contentDescription = "Cài đặt", tint = AccentBlue)
            }
        }
    }
}

// --- TABS BỘ LỌC NGANG CHỈ CÓ 1 DÒNG ---
@Composable
fun OutfitFilterTabs(
    isFavorite: Boolean,
    selectedTags: Set<String>,
    onFavoriteToggle: () -> Unit,
    onTagToggle: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val filterGroups = mapOf(
        "Mùa" to listOf("Mùa Xuân", "Mùa Hạ", "Mùa Thu", "Mùa Đông", "Bốn Mùa"),
        "Thời tiết" to listOf("Nắng Nóng", "Mát Mẻ", "Lạnh", "Mưa"),
        "Dịp" to listOf("Đi Làm", "Đi Học", "Đi Chơi", "Tiệc Tùng", "Thể Thao", "Mặc Nhà"),
        "Phong cách" to listOf("Cơ Bản", "Thanh Lịch", "Năng Động", "Nữ Tính", "Cá Tính", "Vintage")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        // Nút Tất cả
        item {
            val isAllSelected = !isFavorite && selectedTags.isEmpty()
            Surface(
                shape = CircleShape,
                color = if (isAllSelected) AccentBlue else Color.Transparent,
                border = if (isAllSelected) null else BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                modifier = Modifier.clickable { onClearAll() }
            ) {
                Text(
                    text = "Tất cả",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isAllSelected) Color.White else AccentBlue,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
        }

        // Nút Yêu thích
        item {
            Surface(
                shape = CircleShape,
                color = if (isFavorite) AccentBlue else Color.Transparent,
                border = if (isFavorite) null else BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                modifier = Modifier.clickable { onFavoriteToggle() }
            ) {
                Text(
                    text = "Yêu thích",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isFavorite) FontWeight.Bold else FontWeight.Medium,
                    color = if (isFavorite) Color.White else AccentBlue,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                )
            }
        }

        // Nút Dropdown cho các danh mục khác
        filterGroups.forEach { (groupName, options) ->
            item {
                DropdownFilterSurfaceChip(
                    groupName = groupName,
                    options = options,
                    selectedTags = selectedTags,
                    onTagToggle = onTagToggle
                )
            }
        }
    }
}

// --- THIẾT KẾ RIÊNG: NÚT CÓ MŨI TÊN VÀ DROPDOWN KHÔNG CHECKBOX ---
@Composable
fun DropdownFilterSurfaceChip(
    groupName: String,
    options: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Đếm xem trong nhóm này user đã chọn bao nhiêu mục
    val selectedCountInGroup = options.count { it in selectedTags }
    val isSelected = selectedCountInGroup > 0

    Box {
        Surface(
            shape = CircleShape,
            color = if (isSelected) AccentBlue else Color.Transparent,
            border = if (isSelected) null else BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
            modifier = Modifier.clickable { expanded = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 18.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = if (selectedCountInGroup > 0) "$groupName ($selectedCountInGroup)" else groupName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else AccentBlue
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else AccentBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            options.forEach { option ->
                val isChecked = selectedTags.contains(option)
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = if (isChecked) AccentBlue else TextDarkBlue,
                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = { onTagToggle(option) },
                    modifier = Modifier.background(if (isChecked) AccentBlue.copy(alpha = 0.1f) else Color.Transparent)
                )
            }
        }
    }
}

// --- THẺ (CARD) HIỂN THỊ OUTFIT ---
@Composable
fun OutfitItemCard(
    outfit: Outfit,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit // <-- Thêm callback bấm tim
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = outfit.imagePreviewUrl ?: "https://res.cloudinary.com/dna9qbejm/image/upload/v1772213478/xe-tam-ky-hoi-an-banner_bsoc2r.jpg",
                    contentDescription = outfit.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Giao diện Nút Thả tim đổi màu theo dữ liệu
                val isFav = outfit.isFavorite
                Surface(
                    shape = CircleShape,
                    color = SecWhite.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clickable { onFavoriteClick() } // <-- Xử lý bấm tim ở đây
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            // Đổi hình tim: đỏ đặc nếu thích, rỗng viền nếu chưa
                            imageVector = if (isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Yêu thích",
                            // Đổi màu: hồng nếu thích, xám nếu chưa
                            tint = if (isFav) TextPink else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = outfit.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextDarkBlue,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = outfit.description ?: "Chưa có mô tả",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))

                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateStr = outfit.createdAt?.let { dateFormat.format(it) } ?: "Đang cập nhật"

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextLightBlue.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// --- THẺ TẠO MỚI (DASHED CARD) ---
@Composable
fun CreateNewOutfitCard(onClick: () -> Unit) {
    val stroke = Stroke(
        width = 4f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .drawBehind {
                drawRoundRect(
                    color = AccentBlue.copy(alpha = 0.4f),
                    style = stroke,
                    cornerRadius = CornerRadius(20.dp.toPx())
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = AccentBlue.copy(alpha = 0.1f),
                modifier = Modifier.size(54.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Tạo bộ mới",
                style = MaterialTheme.typography.titleMedium,
                color = AccentBlue,
                fontWeight = FontWeight.Bold
            )
        }
    }
}