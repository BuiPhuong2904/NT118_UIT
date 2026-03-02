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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
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
                label = { Text("Email", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
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

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
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

            // 4. Quên mật khẩu
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                TextButton(onClick = { onForgotPasswordClick() }) {
                    Text("Quên mật khẩu?", color = TextBlue, style = Typography.bodyLarge, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Nút Đăng Nhập
            Button(
                onClick = onLoginSuccess,
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
                    Text("Đăng nhập", style = Typography.titleMedium, color = Color.White)
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
                    AsyncImage(
                        model = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/53/Google_%22G%22_Logo.svg/512px-Google_%22G%22_Logo.svg.png",
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
                    AsyncImage(
                        model = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b8/2021_Facebook_icon.svg/512px-2021_Facebook_icon.svg.png",
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

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen()
}