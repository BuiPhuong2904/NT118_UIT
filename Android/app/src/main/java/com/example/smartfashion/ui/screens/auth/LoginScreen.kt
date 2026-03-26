package com.example.smartfashion.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.smartfashion.R

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.GradientSoft
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.TextBlue
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.Typography
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (String) -> Unit = { _ ->},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val loginState = viewModel.loginState.value
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var isSuccessSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                isSuccessSnackbar = true

                // Dùng launch để Snackbar hiện lên mà không chặn tiến trình đếm ngược chuyển trang
                launch {
                    snackbarHostState.showSnackbar(
                        message = "Đăng nhập thành công!",
                        duration = SnackbarDuration.Short
                    )
                }

                delay(1000)

                onLoginSuccess(state.token)
                viewModel.resetState()
            }
            is LoginState.Error -> {
                isSuccessSnackbar = false

                snackbarHostState.showSnackbar(
                    message = state.message,
                    duration = SnackbarDuration.Short
                )
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = if (isSuccessSnackbar) Color(0xFF4CAF50) else Color(0xFFF44336), // Xanh lá hoặc Đỏ
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = data.visuals.message, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            // Gọi hàm Giao diện ở dưới và truyền logic vào
            LoginContent(
                loginState = loginState,
                onLoginClick = { email, password ->
                    viewModel.login(email, password)
                },
                onSignUpClick = onSignUpClick,
                onForgotPasswordClick = onForgotPasswordClick
            )
        }
    }
}

@Composable
fun LoginContent(
    loginState: LoginState,
    onLoginClick: (String, String) -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

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

            Text(
                text = "Chào mừng trở lại!",
                style = Typography.titleLarge.copy(brush = GradientText)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Đăng nhập để tiếp tục",
                color = TextBlue,
                style = Typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                enabled = loginState !is LoginState.Loading,
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

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Mật khẩu", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                enabled = loginState !is LoginState.Loading,
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
                    focusedTrailingIconColor = AccentBlue,
                    unfocusedTrailingIconColor = TextBlue.copy(alpha = 0.6f),
                    cursorColor = AccentBlue
                )
            )

            if (loginState is LoginState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = loginState.message,
                    color = Color.Red,
                    style = Typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 4. Quên mật khẩu
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { onForgotPasswordClick() }) {
                    Text("Quên mật khẩu?", color = TextBlue, style = Typography.bodyLarge, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Nút Đăng Nhập
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = loginState !is LoginState.Loading,
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
                            brush = if (loginState is LoginState.Loading) GradientSoft else GradientText,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (loginState is LoginState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Đăng nhập", style = Typography.titleMedium, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 6. Ngăn cách
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = TextBlue.copy(alpha = 0.2f))
                Text("  hoặc  ", color = TextBlue.copy(alpha = 0.6f), style = Typography.bodyLarge, fontWeight = FontWeight.Medium)
                HorizontalDivider(modifier = Modifier.weight(1f), color = TextBlue.copy(alpha = 0.2f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 7. Đăng nhập Mạng xã hội
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.85f))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Google", style = Typography.titleMedium, color = TextDarkBlue)
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.85f))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook Icon",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Facebook", style = Typography.titleMedium, color = TextDarkBlue)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 8. Chuyển sang Đăng ký
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chưa có tài khoản? ", color = TextDarkBlue, style = Typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(
                    "Đăng ký ngay",
                    color = TextBlue,
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUpClick() },
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}
