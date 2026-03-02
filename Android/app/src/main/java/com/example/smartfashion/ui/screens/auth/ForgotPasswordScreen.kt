package com.example.smartfashion.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.GradientSoft
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.Typography

@Composable
fun ForgotPasswordScreen(
    onBackToLoginClick: () -> Unit = {},
    onSendEmailClick: (String) -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

    var isEmailSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientSoft),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Logo App
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(94.dp),
                shadowElevation = 8.dp
            ) {
                AsyncImage(
                    model = "https://res.cloudinary.com/dna9qbejm/image/upload/v1771943318/logo_notext_nobg_1_tukvbz.png",
                    contentDescription = "Logo App",
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Tiêu đề
            Text(
                text = "Quên mật khẩu?",
                style = Typography.titleLarge.copy(brush = GradientText),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 3. Phụ đề
            Text(
                text = if (isEmailSent) {
                    "Chúng tôi đã gửi một liên kết đặt lại mật khẩu đến email của bạn. Vui lòng kiểm tra hộp thư."
                } else {
                    "Đừng lo lắng! Vui lòng nhập địa chỉ email liên kết với tài khoản của bạn để nhận liên kết đặt lại mật khẩu."
                },
                color = TextBlue,
                style = Typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Phần nội dung
            if (!isEmailSent) {
                // 4. Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email của bạn", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                    textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.9f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.75f),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = TextDarkBlue,
                        unfocusedTextColor = TextDarkBlue,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = TextBlue.copy(alpha = 0.7f),
                        focusedLeadingIconColor = AccentBlue,
                        unfocusedLeadingIconColor = TextBlue.copy(alpha = 0.6f),
                        cursorColor = AccentBlue
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 5. Nút Gửi liên kết
                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            // TODO: Xử lý logic gửi thực tế
                            isEmailSent = true
                            onSendEmailClick(email)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = GradientText, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Gửi liên kết", style = Typography.titleMedium, color = Color.White)
                    }
                }
            } else {
                TextButton(onClick = { isEmailSent = false }) {
                    Text(
                        text = "Thử một email khác?",
                        color = TextBlue,
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 6. Quay lại trang đăng nhập
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onBackToLoginClick() }
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextDarkBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quay lại Đăng nhập",
                    color = TextBlue,
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    ForgotPasswordScreen()
}