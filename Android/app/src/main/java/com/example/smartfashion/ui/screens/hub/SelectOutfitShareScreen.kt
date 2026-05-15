package com.example.smartfashion.ui.screens.hub

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.smartfashion.ui.screens.studio.OutfitFilterTabs
import com.example.smartfashion.ui.screens.studio.OutfitViewModel
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectOutfitShareScreen(
    navController: NavController,
    outfitViewModel: OutfitViewModel = hiltViewModel(),
    communityViewModel: CommunityTrendViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userId = tokenManager.getUserId()

    // Lấy danh sách Outfits của user
    val outfits by outfitViewModel.outfits.collectAsState()
    val isLoading by outfitViewModel.isLoading.collectAsState()
    val isPosting by communityViewModel.isLoading.collectAsState()

    // Trạng thái chọn Outfit và nhập Caption
    var selectedOutfit by remember { mutableStateOf<Outfit?>(null) }
    var showCaptionDialog by remember { mutableStateOf(false) }
    var captionText by remember { mutableStateOf("") }

    // Biến lọc
    var isFavorite by remember { mutableStateOf(false) }
    var selectedTags by remember { mutableStateOf<Set<String>>(emptySet()) }

    LaunchedEffect(isFavorite, selectedTags, userId) {
        if (userId != -1) {
            outfitViewModel.fetchOutfits(
                userId = userId,
                isFavorite = if (isFavorite) true else null,
                tags = if (selectedTags.isNotEmpty()) selectedTags.toList() else null
            )
        }
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgLight)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // --- Header ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Chia sẻ Outfit",
                            style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                            fontWeight = FontWeight.Bold
                        )
                        Text("Chọn một bộ trang phục để khoe nào", fontSize = 12.sp, color = TextLightBlue)
                    }
                }

                // --- Bộ lọc (Dùng chung của màn Studio) ---
                Box(modifier = Modifier.padding(bottom = 12.dp)) {
                    OutfitFilterTabs(
                        isFavorite = isFavorite,
                        selectedTags = selectedTags,
                        onFavoriteToggle = {
                            isFavorite = !isFavorite
                            selectedTags = emptySet()
                        },
                        onTagToggle = { tag ->
                            isFavorite = false
                            selectedTags = if (selectedTags.contains(tag)) selectedTags - tag else selectedTags + tag
                        },
                        onClearAll = {
                            isFavorite = false
                            selectedTags = emptySet()
                        }
                    )
                }
            }
        },
        bottomBar = {
            // Nút "Tiếp tục" chỉ hiện khi đã chọn 1 outfit
            if (selectedOutfit != null) {
                Surface(
                    color = SecWhite,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showCaptionDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .height(54.dp)
                    ) {
                        Text("Viết Caption & Đăng bài", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(outfits.size) { index ->
                    val outfit = outfits[index]
                    val isSelected = selectedOutfit?.outfitId == outfit.outfitId

                    SelectableOutfitCard(
                        outfit = outfit,
                        isSelected = isSelected,
                        onClick = { selectedOutfit = outfit }
                    )
                }
            }
        }

        // --- DIALOG NHẬP CAPTION VÀ ĐĂNG ---
        if (showCaptionDialog && selectedOutfit != null) {
            AlertDialog(
                onDismissRequest = { if (!isPosting) showCaptionDialog = false },
                containerColor = SecWhite,
                title = { Text("Bạn đang nghĩ gì?", fontWeight = FontWeight.Bold, color = TextDarkBlue) },
                text = {
                    Column {
                        AsyncImage(
                            model = selectedOutfit!!.imagePreviewUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = captionText,
                            onValueChange = { captionText = it },
                            placeholder = { Text("VD: OOTD đi cafe nhẹ nhàng...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (userId != -1 && selectedOutfit?.outfitId != null) {
                                communityViewModel.createPost(
                                    userId = userId,
                                    outfitId = selectedOutfit!!.outfitId!!,
                                    imageUrl = selectedOutfit!!.imagePreviewUrl ?: "",
                                    description = captionText,
                                    onSuccess = {
                                        showCaptionDialog = false
                                        navController.popBackStack()
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        enabled = !isPosting
                    ) {
                        if (isPosting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đăng lên")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCaptionDialog = false }, enabled = !isPosting) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            )
        }
    }
}

// Component Card
@Composable
fun SelectableOutfitCard(
    outfit: Outfit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) AccentBlue else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box {
            AsyncImage(
                model = outfit.imagePreviewUrl ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )

            // Hiện icon Check xanh nếu được chọn
            if (isSelected) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentBlue)
                }
            }
        }
    }
}