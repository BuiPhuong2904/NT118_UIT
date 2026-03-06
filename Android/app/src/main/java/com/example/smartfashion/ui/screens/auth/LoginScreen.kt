package com.example.smartfashion.ui.screens.auth

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartfashion.viewmodel.LoginViewModel
import com.example.smartfashion.viewmodel.LoginState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.smartfashion.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val loginState = viewModel.loginState.value




    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    LoginContent(
        loginState = loginState,
        onLoginClick = { email, password ->
            viewModel.login(email, password)
        },
        onSignUpClick = onSignUpClick,
        onForgotPasswordClick = onForgotPasswordClick
    )
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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", fontWeight = FontWeight.Medium) },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu", fontWeight = FontWeight.Medium) },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, null)
                    }
                },
                visualTransformation =
                    if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(onClick = onForgotPasswordClick) {
                    Text(
                        "Quên mật khẩu?",
                        color = TextBlue,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Đăng nhập")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                Text("Chưa có tài khoản? ", color = TextDarkBlue)
                Text(
                    "Đăng ký ngay",
                    color = TextBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignUpClick() },
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginContent(
        loginState = LoginState.Idle,
        onLoginClick = { _, _ -> },
        onSignUpClick = {},
        onForgotPasswordClick = {}
    )
}
