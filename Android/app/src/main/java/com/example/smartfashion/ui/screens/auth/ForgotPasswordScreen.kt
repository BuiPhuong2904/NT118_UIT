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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartfashion.ui.theme.*

@Composable
fun ForgotPasswordScreen(
    onBackToLoginClick: () -> Unit = {},
    onSendEmailClick: (String) -> Unit = {},
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }

    // Lấy state từ ViewModel
    val state = viewModel.state.value

    val snackbarHostState = remember { SnackbarHostState() }

    // Lắng nghe lỗi để hiện Snackbar
    LaunchedEffect(state) {
        if (state is ForgotPasswordState.Error) {
            snackbarHostState.showSnackbar(
                message = state.message,
                duration = SnackbarDuration.Short
            )
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = Color(0xFFF44336),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = data.visuals.message, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    text = "Đừng lo lắng! Vui lòng nhập địa chỉ email liên kết với tài khoản của bạn để nhận mã OTP khôi phục mật khẩu.",
                    color = TextBlue,
                    style = Typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4. Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email của bạn", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                    textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    enabled = state !is ForgotPasswordState.Loading,
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

                // 5. Nút Gửi OTP
                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            onSendEmailClick(email)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = email.isNotBlank() && state !is ForgotPasswordState.Loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = if (email.isNotBlank() && state !is ForgotPasswordState.Loading) GradientText else GradientSoft,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Hiện vòng xoay nếu đang Loading, không thì hiện chữ
                        if (state is ForgotPasswordState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Gửi mã OTP", style = Typography.titleMedium, color = Color.White)
                        }
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
}