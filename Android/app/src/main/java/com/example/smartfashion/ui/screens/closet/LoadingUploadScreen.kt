package com.example.smartfashion.ui.screens.closet

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

import com.example.smartfashion.ui.theme.GradientSoft

@Composable
fun LoadingUploadScreen(
    onFinished: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var progress by remember { mutableFloatStateOf(0f) }

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

    LaunchedEffect(Unit) {
        while (progress < 100f) {
            delay(30)
            progress += 1.5f
            if (progress > 100f) progress = 100f
        }
        delay(600)
        onFinished()
    }

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
                text = "AI đang xử lý hình ảnh...",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress / 100f },
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
                text = "${progress.toInt()}% hoàn tất",
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
}

@Preview(showBackground = true)
@Composable
fun LoadingUploadPreview() {
    LoadingUploadScreen()
}