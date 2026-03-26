package com.example.smartfashion.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// Bổ sung import Hilt
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartfashion.ui.theme.*

@Composable
fun ResetPasswordScreen(
    email: String = "",
    onBackToLoginClick: () -> Unit = {},
    onResetPasswordClick: (String, String, String) -> Unit = { _, _, _ -> },
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {

    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    // 1. Lấy State từ ViewModel thay vì dùng biến giả
    val state = viewModel.state.value
    val isSuccess = state is ResetPasswordState.Success

    // 2. Chuẩn bị Snackbar báo lỗi
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state) {
        if (state is ResetPasswordState.Error) {
            snackbarHostState.showSnackbar(
                message = state.message,
                duration = SnackbarDuration.Short
            )
            viewModel.resetState()
        }
    }

    // 3. Bọc màn hình bằng Scaffold
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

                // Logo
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    modifier = Modifier.size(94.dp),
                    shadowElevation = 8.dp
                ) {
                    AsyncImage(
                        model = "https://res.cloudinary.com/dna9qbejm/image/upload/v1771943318/logo_notext_nobg_1_tukvbz.png",
                        contentDescription = "Logo",
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Đặt lại mật khẩu",
                    style = Typography.titleLarge.copy(brush = GradientText),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isSuccess)
                        "Mật khẩu của bạn đã được cập nhật thành công."
                    else
                        "Nhập mã OTP và mật khẩu mới để bảo vệ tài khoản.",
                    color = TextBlue,
                    style = Typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (!isSuccess) {
                    // Ô NHẬP MÃ OTP
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it },
                        placeholder = { Text("Mã xác nhận (OTP)", style = Typography.bodyLarge) },
                        textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        enabled = state !is ResetPasswordState.Loading,
                        colors = getGlassmorphismTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // PASSWORD
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Mật khẩu mới", style = Typography.bodyLarge) },
                        textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, null)
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        enabled = state !is ResetPasswordState.Loading, // Khóa khi Loading
                        colors = getGlassmorphismTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // CONFIRM PASSWORD
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Nhập lại mật khẩu", style = Typography.bodyLarge) },
                        textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            val icon = if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                Icon(icon, null)
                            }
                        },
                        visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPassword.isNotEmpty() && confirmPassword != password,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        enabled = state !is ResetPasswordState.Loading,
                        colors = getGlassmorphismTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    val isValid = otp.isNotBlank() && password.isNotBlank() && password == confirmPassword

                    // BUTTON RESET
                    Button(
                        onClick = {
                            if (isValid) {
                                onResetPasswordClick(email, otp, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(),
                        enabled = isValid && state !is ResetPasswordState.Loading
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = if (isValid && state !is ResetPasswordState.Loading) GradientText else GradientSoft,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state is ResetPasswordState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    "Đặt lại mật khẩu",
                                    color = if (isValid) Color.White else TextDarkBlue.copy(alpha = 0.5f),
                                    style = Typography.titleMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // BACK LOGIN
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onBackToLoginClick() }
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
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