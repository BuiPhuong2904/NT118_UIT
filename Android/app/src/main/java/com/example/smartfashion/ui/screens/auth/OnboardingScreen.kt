package com.example.smartfashion.ui.screens.auth

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

// Màu chủ đạo
val OnboardingPrimary = Color(0xFF6200EE)

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageUrl: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit = {} // Gọi khi bấm nút "Bắt đầu"
) {
    val pages = listOf(
        OnboardingPage(
            "Quản lý Tủ đồ Thông minh",
            "Số hóa tủ đồ của bạn chỉ trong vài giây. Tự động phân loại và tìm kiếm dễ dàng.",
            "https://i.postimg.cc/9MXZHYtp/3.jpg" // Thay ảnh minh họa Closet
        ),
        OnboardingPage(
            "AI Stylist & Phối đồ",
            "Nhận gợi ý trang phục mỗi ngày dựa trên thời tiết và phong cách cá nhân của bạn.",
            "https://i.postimg.cc/9MXZHYtp/3.jpg" // Thay ảnh minh họa Studio
        ),
        OnboardingPage(
            "Lên lịch & Du lịch",
            "Lên kế hoạch mặc đẹp cho cả tuần hoặc chuẩn bị hành lý du lịch không lo quên đồ.",
            "https://i.postimg.cc/9MXZHYtp/3.jpg" // Thay ảnh minh họa Calendar
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Nút Bỏ qua (Skip)
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(onClick = onFinish) {
                        Text("Bỏ qua", color = Color.Gray)
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp)) // Giữ chỗ
                }
            }

            // Slider
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                OnboardingPageContent(pages[index])
            }

            // Footer (Indicator + Button)
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicator (Mấy dấu chấm)
                Row(
                    modifier = Modifier.padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(pages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) OnboardingPrimary else Color.LightGray
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(if (pagerState.currentPage == iteration) 12.dp else 8.dp)
                        )
                    }
                }

                // Nút Next / Get Started
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OnboardingPrimary)
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) "Bắt đầu ngay" else "Tiếp theo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.size(280.dp)
        ) {
            AsyncImage(
                model = page.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = page.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 22.sp
        )
    }
}

@Preview
@Composable
fun OnboardingPreview() {
    OnboardingScreen()
}