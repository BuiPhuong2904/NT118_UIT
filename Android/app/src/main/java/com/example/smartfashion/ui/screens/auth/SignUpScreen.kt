package com.example.smartfashion.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage

// Theme và Model
import com.example.smartfashion.ui.theme.*
import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.model.RegisterState

// Hilt và Lifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SignUpScreen(
    onSignUpSuccess: (String) -> Unit = { _ -> },
    onLoginClick: () -> Unit = {},
    viewModel: SignUpViewModel = hiltViewModel()
) {
    // 1. State nhập liệu
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedToTerms by remember { mutableStateOf(false) }

    // 2. Thu thập State từ ViewModel
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // 3. Xử lý khi đăng ký thành công
    LaunchedEffect(registerState) {
        val currentState = registerState
        if (currentState is RegisterState.Success) {
            tokenManager.saveToken(currentState.token)
            onSignUpSuccess(currentState.token)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientSoft),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo App
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(80.dp),
                shadowElevation = 8.dp
            ) {
                AsyncImage(
                    model = "https://res.cloudinary.com/dna9qbejm/image/upload/v1771943318/logo_notext_nobg_1_tukvbz.png",
                    contentDescription = "Logo App",
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tạo tài khoản",
                style = Typography.titleLarge.copy(brush = GradientText)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tham gia cộng đồng SmartFashion",
                color = TextLightBlue,
                style = Typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Ô nhập Họ và tên
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Họ và tên", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = getGlassmorphismTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô nhập Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = getGlassmorphismTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô nhập Mật khẩu
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
                colors = getGlassmorphismTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ô xác nhận Mật khẩu
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Xác nhận mật khẩu", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                isError = confirmPassword.isNotEmpty() && confirmPassword != password,
                colors = getGlassmorphismTextFieldColors()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Checkbox điều khoản
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = AccentBlue,
                        uncheckedColor = TextDarkBlue,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "Tôi đồng ý với Điều khoản và Chính sách",
                    style = Typography.bodyLarge,
                    color = TextDarkBlue,
                    modifier = Modifier.clickable { agreedToTerms = !agreedToTerms }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Hiển thị lỗi từ Server nếu có
            if (registerState is RegisterState.Error) {
                Text(
                    text = (registerState as RegisterState.Error).message,
                    color = Color.Red,
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Nút Đăng ký
            val isFormValid = fullName.isNotBlank() && email.isNotBlank() &&
                    password.isNotBlank() && (password == confirmPassword) &&
                    agreedToTerms

            Button(
                onClick = {
                    viewModel.register(username = fullName, email = email, password = password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.White.copy(alpha = 0.3f)
                ),
                contentPadding = PaddingValues(),
                enabled = isFormValid && registerState !is RegisterState.Loading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isFormValid) Modifier.background(brush = GradientText, shape = RoundedCornerShape(16.dp))
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (registerState is RegisterState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Đăng ký",
                            style = Typography.titleMedium,
                            color = if (isFormValid) Color.White else TextDarkBlue.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Chuyển sang Đăng nhập
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Đã có tài khoản? ", color = TextDarkBlue, style = Typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(
                    text = "Đăng nhập",
                    color = TextBlue,
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLoginClick() },
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun getGlassmorphismTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White.copy(alpha = 0.9f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.75f),
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    errorBorderColor = Color.Red.copy(alpha = 0.8f),
    focusedTextColor = TextDarkBlue,
    unfocusedTextColor = TextDarkBlue,
    errorTextColor = TextDarkBlue,
    focusedLabelColor = AccentBlue,
    unfocusedLabelColor = TextBlue.copy(alpha = 0.7f),
    errorLabelColor = Color.Red.copy(alpha = 0.8f),
    focusedLeadingIconColor = AccentBlue,
    unfocusedLeadingIconColor = TextBlue.copy(alpha = 0.6f),
    focusedTrailingIconColor = AccentBlue,
    unfocusedTrailingIconColor = TextBlue.copy(alpha = 0.6f),
    cursorColor = AccentBlue
)