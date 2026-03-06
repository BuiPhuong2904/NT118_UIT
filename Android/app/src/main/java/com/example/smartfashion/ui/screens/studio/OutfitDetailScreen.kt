package com.example.smartfashion.ui.screens.studio

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
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
    outfitId: Int, // Nhận ID từ màn hình trước truyền sang
    viewModel: OutfitDetailViewModel = hiltViewModel() // Ép Hilt bơm ViewModel vào
) {
    val scrollState = rememberScrollState()

    // Quan sát dữ liệu từ ViewModel
    val outfit by viewModel.outfit.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Gọi API lấy dữ liệu chi tiết khi màn hình vừa mở
    LaunchedEffect(outfitId) {
        viewModel.fetchOutfitDetail(outfitId)
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chi tiết Phối đồ",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = AccentBlue)
                    }
                    IconButton(onClick = { /* onEditClick */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = { /* Chuyển sang màn hình Calendar / Mở BottomSheet Lên lịch */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TextDarkBlue)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Lên lịch mặc bộ này",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->

        // KIỂM TRA LOADING: Đang tải thì xoay vòng vòng
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else if (outfit != null) {
            // NẾU CÓ DỮ LIỆU THÌ ĐỔ RA GIAO DIỆN
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                        .background(Color(0xFFEBF2FA))
                ) {
                    AsyncImage(
                        // Lấy ảnh thật từ API
                        model = outfit?.imagePreviewUrl ?: "https://i.postimg.cc/9MXZHYtp/3.jpg",
                        contentDescription = "Outfit Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Ẩn/Hiện nhãn AI dựa vào data thật
                    if (outfit?.isAiSuggested == true) {
                        Surface(
                            modifier = Modifier
                                .padding(20.dp)
                                .align(Alignment.BottomStart),
                            shape = RoundedCornerShape(16.dp),
                            color = SecWhite.copy(alpha = 0.95f),
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = TextPink, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gợi ý bởi AI",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextDarkBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = outfit?.name ?: "Chưa có tên", // Lấy tên thật
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 26.sp,
                            color = TextDarkBlue,
                            modifier = Modifier.weight(1f)
                        )

                        // Ẩn/Hiện Rating dựa vào data thật
                        if (outfit?.rating != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = outfit?.rating.toString(), // Lấy điểm đánh giá thật
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextDarkBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = outfit?.description ?: "Chưa có mô tả cho bộ phối đồ này.", // Lấy mô tả thật
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextLightBlue,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Phần Tag tạm thời giữ nguyên (có thể làm API mảng Tag sau)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutfitTag(label = "Công sở")
                        OutfitTag(label = "Mùa Hè")
                    }
                }

                HorizontalDivider(thickness = 2.dp, color = TextLightBlue.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 24.dp))

                // Phần các món đồ nhỏ trong set
                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    Text(
                        text = "Món đồ trong set",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextDarkBlue,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // LẤY MẢNG QUẦN ÁO TỪ OUTFIT
                    val clothesList = outfit?.clothes ?: emptyList()

                    if (clothesList.isEmpty()) {
                        Text(
                            text = "Chưa có món đồ nào trong bộ này.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextLightBlue,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            items(clothesList.size) { index ->
                                val clothing = clothesList[index]
                                ComponentItemCard(clothing = clothing) // Truyền data thật vào Card
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        } else {
            // Trường hợp lỗi hoặc không tìm thấy ID
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy dữ liệu bộ đồ này 😢", color = TextLightBlue)
            }
        }
    }
}

@Composable
fun OutfitTag(label: String) {
    Surface(
        color = AccentBlue.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = AccentBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ComponentItemCard(clothing: Clothing) {
    Column(
        modifier = Modifier.width(110.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SecWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .size(110.dp)
                .border(1.dp, TextLightBlue.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFEBF2FA)), contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = clothing.imageUrl ?: "https://res.cloudinary.com/dna9qbejm/image/upload/v1772213478/xe-tam-ky-hoi-an-banner_bsoc2r.jpg",
                    contentDescription = clothing.name,
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = clothing.name ?: "Chưa có tên",
            style = MaterialTheme.typography.titleMedium,
            color = TextDarkBlue,
            fontSize = 13.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = clothing.brandName ?: "No brand",
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 12.sp,
            color = TextLightBlue,
            maxLines = 1
        )
    }
}