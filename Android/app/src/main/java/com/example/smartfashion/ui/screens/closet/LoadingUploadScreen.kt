package com.example.smartfashion.ui.screens.closet

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.ui.theme.GradientSoft

@Composable
fun LoadingUploadScreen(
    imageUri: String = "",
    // link NoBg, link Gốc, và ID ảnh
    onFinished: (String, String, Int) -> Unit = { _, _, _ -> },
    onCancel: () -> Unit = {},
    viewModel: UploadViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val userId = tokenManager.getUserId()

    val uploadedUrl by viewModel.uploadedUrl.collectAsState()
    val isError by viewModel.isError.collectAsState()

    // 1. Lấy phần trăm THẬT (từ lúc truyền file)
    val actualUploadProgress by viewModel.uploadProgress.collectAsState()

    // 2. Tạo biến HIỂN THỊ ẢO cho giao diện
    var displayProgress by remember { mutableFloatStateOf(0f) }

    // Hiệu ứng nhịp đập (Logo Pulse)
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoPulse"
    )

    // Gọi API ngay khi màn hình vừa mở lên
    LaunchedEffect(Unit) {
        if (imageUri.isNotEmpty()) {
            val decodedUri = Uri.parse(Uri.decode(imageUri))
            val currentUserId = if (userId != -1) userId else 1
            viewModel.uploadImageToAI(context, decodedUri, currentUserId)
        } else {
            onCancel()
        }
    }

    // =========================================================
    // KỸ THUẬT FAKE SCALING (CHIA TỶ LỆ %)
    // =========================================================

    // GIAI ĐOẠN 1: Up ảnh từ điện thoại lên Node.js (Chiếm 0% -> 40%)
    LaunchedEffect(actualUploadProgress) {
        if (actualUploadProgress <= 100f && displayProgress < 40f) {
            // Ánh xạ 100% thật thành 40% hiển thị
            displayProgress = actualUploadProgress * 0.4f
        }
    }

    // GIAI ĐOẠN 2: Server đang xử lý AI và tải lên Cloudinary (Chiếm 40% -> 90%)
    LaunchedEffect(actualUploadProgress, uploadedUrl, isError) {
        if (actualUploadProgress == 100f && uploadedUrl == null && !isError) {
            // Bắt đầu chạy rùa bò từ 40% lên 90%
            while (displayProgress < 90f && uploadedUrl == null) {
                delay(200) // Cứ 0.2s tăng 1%
                displayProgress += 1f
            }
        }
    }

    // GIAI ĐOẠN 3: Hoàn tất (Chốt 100%)
    LaunchedEffect(uploadedUrl) {
        if (uploadedUrl != null) {
            displayProgress = 100f
            delay(600)

            val originalUrlFromApi = viewModel.originalUrl.value ?: ""
            // LẤY ID ẢNH TỪ VIEWMODEL: (Bạn cần vào UploadViewModel thêm biến để lưu id này nhé)
            val imageIdFromApi = viewModel.imageId.value ?: 0

            val encodedNoBgUrl = Uri.encode(uploadedUrl)
            val encodedOriginalUrl = Uri.encode(originalUrlFromApi)

            // TRẢ VỀ 3 THAM SỐ
            onFinished(encodedNoBgUrl, encodedOriginalUrl, imageIdFromApi)
        }
    }
    // =========================================================

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientSoft)
    ) {
        IconButton(
            onClick = onCancel,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(Icons.Rounded.Close, contentDescription = "Hủy", modifier = Modifier.size(28.dp), tint = Color.White)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulseScale),
                shadowElevation = 12.dp
            ) {
                AsyncImage(
                    model = "https://res.cloudinary.com/dna9qbejm/image/upload/v1771943318/logo_notext_nobg_1_tukvbz.png",
                    contentDescription = "Đang xử lý",
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = if (displayProgress < 40f) "Đang tải ảnh lên máy chủ..." else "AI đang tách nền và tối ưu ảnh...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                // Sử dụng displayProgress thay vì actualUploadProgress
                progress = { displayProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(100)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${displayProgress.toInt()}% hoàn tất",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Vui lòng giữ nguyên màn hình này",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }

    if (isError) {
        AlertDialog(
            onDismissRequest = onCancel,
            title = { Text("Lỗi xử lý ảnh") },
            text = { Text("Không thể kết nối đến máy chủ AI hoặc ảnh quá dung lượng. Vui lòng thử lại.") },
            confirmButton = {
                TextButton(onClick = onCancel) { Text("Quay lại") }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingUploadPreview() {
    LoadingUploadScreen(imageUri = "")
}