package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.TextPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreItemDetailScreen(
    navController: NavController,
    templateId: Int,
    viewModel: StoreItemDetailViewModel = hiltViewModel()
) {
    // Gọi tải dữ liệu khi vào màn hình
    LaunchedEffect(templateId) {
        viewModel.fetchSystemClothingDetail(templateId)
    }

    val item by viewModel.systemItem.collectAsState()

    // Lấy trạng thái Wishlist từ ViewModel
    val wishlistId by viewModel.wishlistId.collectAsState()
    val isFavorite = wishlistId != null

    val scrollState = rememberScrollState()

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize().background(BgLight), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentBlue)
        }
        return
    }

    val safeItem = item!!

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(safeItem.colorHex ?: "#CCCCCC"))
    } catch (e: Exception) { Color.LightGray }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết mẫu", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDarkBlue)
                    }
                },
                actions = {
                    //  Gọi logic toggle Wishlist mới
                    IconButton(onClick = {
                        viewModel.toggleWishlist()
                    }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) TextPink else TextLightBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth().padding(20.dp), color = Color.Transparent) {
                Button(
                    onClick = { /* Gọi ViewModel để Copy item này vào Closet cá nhân */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Icon(Icons.Rounded.AddCircleOutline, null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Thêm vào Tủ đồ", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(scrollState)) {
                // Vùng chứa ảnh
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

                // Vùng chứa nội dung
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(SecWhite).padding(24.dp)
                ) {
                    Text(text = safeItem.name, style = MaterialTheme.typography.titleLarge, color = TextDarkBlue, fontSize = 26.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BgLight)

                    DetailRowElegantStore("Danh mục", safeItem.categoryName ?: "Không rõ", isPrimary = true)

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

                    if (!safeItem.description.isNullOrEmpty()) {
                        DetailRowElegantStore("Mô tả", safeItem.description)
                    }

                    if (!safeItem.tags.isNullOrEmpty()) {
                        DetailRowElegantStore("Tags", safeItem.tags.joinToString(", "))
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun DetailRowElegantStore(label: String, value: String, isPrimary: Boolean = false) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = TextDarkBlue,
                modifier = Modifier.padding(end = 20.dp)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isPrimary) AccentBlue else TextLightBlue,
                    fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.End
            )
        }
        HorizontalDivider(color = BgLight)
    }
}