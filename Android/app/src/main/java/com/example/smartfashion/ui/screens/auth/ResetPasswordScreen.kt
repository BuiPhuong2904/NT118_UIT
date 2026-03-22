package com.example.smartfashion.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

import com.example.smartfashion.ui.theme.*

@Composable
fun ResetPasswordScreen(
    onBackToLoginClick: () -> Unit = {},
    onResetPasswordClick: (String) -> Unit = {}
) {

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    var isSuccess by remember { mutableStateOf(false) }

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
                    "Nhập mật khẩu mới để bảo vệ tài khoản SmartFashion của bạn.",
                color = TextBlue,
                style = Typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (!isSuccess) {

                // PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu mới", style = Typography.bodyLarge) },
                    textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {

                        val icon =
                            if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff

                        IconButton(
                            onClick = { passwordVisible = !passwordVisible }
                        ) {
                            Icon(icon, null)
                        }

                    },
                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = getGlassmorphismTextFieldColors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CONFIRM PASSWORD
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Nhập lại mật khẩu", style = Typography.bodyLarge) },
                    textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    trailingIcon = {

                        val icon =
                            if (confirmVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff

                        IconButton(
                            onClick = { confirmVisible = !confirmVisible }
                        ) {
                            Icon(icon, null)
                        }

                    },
                    visualTransformation =
                        if (confirmVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                    isError =
                        confirmPassword.isNotEmpty() &&
                                confirmPassword != password,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = getGlassmorphismTextFieldColors()
                )

                Spacer(modifier = Modifier.height(24.dp))

                val isValid =
                    password.isNotBlank() &&
                            password == confirmPassword

                // BUTTON RESET
                Button(
                    onClick = {

                        if (isValid) {

                            onResetPasswordClick(password)

                            isSuccess = true
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(),
                    enabled = isValid
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = GradientText,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            "Đặt lại mật khẩu",
                            color = Color.White,
                            style = Typography.titleMedium
                        )

                    }

                }

            } else {

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onBackToLoginClick() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Quay lại đăng nhập")
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
