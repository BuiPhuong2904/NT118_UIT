package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartfashion.model.Clothing

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitDetailScreen(
    navController: NavController,
    outfitId: Int,
    viewModel: OutfitDetailViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    val outfit by viewModel.outfit.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val allTags by viewModel.allTags.collectAsState()

    var showEditSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(outfitId) {
        viewModel.fetchOutfitDetail(outfitId)
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = {
                    Text("Chi tiết Phối đồ", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText), fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Share, contentDescription = "Share", tint = AccentBlue) }
                    IconButton(onClick = { showEditSheet = true }) { Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextPink) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth().padding(20.dp), color = Color.Transparent) {
                Button(
                    onClick = { /* Mở BottomSheet Lên lịch */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextDarkBlue)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lên lịch mặc bộ này", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = AccentBlue) }
        } else if (outfit != null) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState)) {
                Box(modifier = Modifier.fillMaxWidth().height(420.dp).clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(Color(0xFFEBF2FA))) {
                    AsyncImage(
                        model = outfit?.imagePreviewUrl ?: "https://i.postimg.cc/9MXZHYtp/3.jpg",
                        contentDescription = "Outfit Image", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                    )
                    if (outfit?.isAiSuggested == true) {
                        Surface(modifier = Modifier.padding(20.dp).align(Alignment.BottomStart), shape = RoundedCornerShape(16.dp), color = SecWhite.copy(alpha = 0.95f), shadowElevation = 4.dp) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = TextPink, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Gợi ý bởi AI", style = MaterialTheme.typography.bodyLarge, color = TextDarkBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Text(outfit?.name ?: "Chưa có tên", style = MaterialTheme.typography.titleLarge, fontSize = 26.sp, color = TextDarkBlue, modifier = Modifier.weight(1f))
                        if (outfit?.rating != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(outfit?.rating.toString(), style = MaterialTheme.typography.titleMedium, color = TextDarkBlue)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(outfit?.description ?: "Chưa có mô tả cho bộ phối đồ này.", style = MaterialTheme.typography.bodyLarge, color = TextLightBlue, lineHeight = 22.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    val tagsList = outfit?.tagNames ?: emptyList()
                    if (tagsList.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(tagsList) { tagName -> OutfitTag(label = tagName) }
                        }
                    }
                }

                HorizontalDivider(thickness = 2.dp, color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 24.dp))

                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    Text("Món đồ trong set", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, modifier = Modifier.padding(horizontal = 24.dp))
                    Spacer(modifier = Modifier.height(16.dp))

                    val clothesList = outfit?.clothes ?: emptyList()
                    if (clothesList.isEmpty()) {
                        Text("Chưa có món đồ nào trong bộ này.", style = MaterialTheme.typography.bodyMedium, color = TextLightBlue, modifier = Modifier.padding(horizontal = 24.dp))
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(horizontal = 24.dp)) {
                            items(clothesList) { clothing -> ComponentItemCard(clothing = clothing) }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) { Text("Không tìm thấy dữ liệu bộ đồ này 😢", color = TextLightBlue) }
        }
    }

    // --- BOTTOM SHEET SỬA THÔNG TIN & TAGS ---
    if (showEditSheet && outfit != null) {
        var editName by remember { mutableStateOf(outfit!!.name ?: "") }
        var editDesc by remember { mutableStateOf(outfit!!.description ?: "") }

        var selectedTags by remember { mutableStateOf(outfit!!.tagNames?.toSet() ?: emptySet()) }

        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            containerColor = SecWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Chỉnh sửa bộ đồ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextDarkBlue)
                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = editName, onValueChange = { editName = it },
                    label = { Text("Tên bộ đồ") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = editDesc, onValueChange = { editDesc = it },
                    label = { Text("Mô tả") }, modifier = Modifier.fillMaxWidth(), maxLines = 3, shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (allTags.isEmpty()) {
                    Text("Đang tải danh sách thẻ...", color = TextLightBlue, fontSize = 13.sp)
                } else {
                    val groupedTags = allTags.groupBy { it.tagGroup ?: "Khác" }

                    groupedTags.forEach { (groupName, tagsInGroup) ->
                        Text(text = groupName, style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tagsInGroup) { tag ->
                                val isSelected = selectedTags.contains(tag.tagName)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedTags = if (isSelected) {
                                            selectedTags - tag.tagName
                                        } else {
                                            selectedTags + tag.tagName
                                        }
                                    },
                                    label = { Text(text = tag.tagName, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentBlue,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected, borderColor = AccentBlue.copy(alpha = 0.5f)),
                                    shape = RoundedCornerShape(50)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.updateOutfit(outfitId, editName, editDesc, selectedTags.toList())
                        showEditSheet = false
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Lưu thay đổi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        showEditSheet = false
                        showDeleteConfirmDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336)),
                    border = BorderStroke(1.dp, Color(0xFFF44336))
                ) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xóa bộ phối đồ này", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Xác nhận xóa", fontWeight = FontWeight.Bold, color = TextDarkBlue) },
            text = { Text("Bạn có chắc chắn muốn xóa bộ phối đồ này không? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteOutfit(outfitId)
                        navController.popBackStack()
                    }
                ) {
                    Text("Xóa", color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Hủy", color = TextLightBlue) } },
            containerColor = SecWhite
        )
    }
}

@Composable
fun OutfitTag(label: String) {
    Surface(color = AccentBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
fun ComponentItemCard(clothing: Clothing) {
    Column(modifier = Modifier.width(110.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SecWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.size(110.dp).border(1.dp, TextLightBlue.copy(alpha = 0.15f), RoundedCornerShape(20.dp))) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFEBF2FA)), contentAlignment = Alignment.Center) {
                AsyncImage(model = clothing.imageUrl ?: "https://res.cloudinary.com/dna9qbejm/image/upload/v1772213478/xe-tam-ky-hoi-an-banner_bsoc2r.jpg", contentDescription = clothing.name, modifier = Modifier.padding(12.dp).fillMaxSize(), contentScale = ContentScale.Fit)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(clothing.name ?: "Chưa có tên", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 13.sp, maxLines = 1)
        Spacer(modifier = Modifier.height(2.dp))
        Text(clothing.brandName ?: "No brand", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = TextLightBlue, maxLines = 1)
    }
}