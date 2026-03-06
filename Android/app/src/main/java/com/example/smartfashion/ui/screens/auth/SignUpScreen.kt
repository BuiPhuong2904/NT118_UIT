package com.example.smartfashion.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.smartfashion.ui.theme.TextLightBlue
import com.example.smartfashion.ui.theme.Typography

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    // State nhập liệu
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // State cho Checkbox Điều khoản
    var agreedToTerms by remember { mutableStateOf(false) }
    val viewModel: SignUpViewModel = viewModel()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            onSignUpSuccess()
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

            // 1. Logo App
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

            // 2. Họ và tên
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Họ và tên", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = getGlassmorphismTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = getGlassmorphismTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Mật khẩu
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
                colors = getGlassmorphismTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Xác nhận Mật khẩu
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Xác nhận mật khẩu", style = Typography.bodyLarge, fontWeight = FontWeight.Medium) },
                textStyle = Typography.bodyLarge.copy(color = TextDarkBlue),
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(), // Luôn ẩn
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,

                isError = confirmPassword.isNotEmpty() && confirmPassword != password,
                colors = getGlassmorphismTextFieldColors()
            )

            Spacer(modifier = Modifier.height(20.dp))

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

            val isFormValid = fullName.isNotBlank() && email.isNotBlank() &&
                    password.isNotBlank() && (password == confirmPassword) &&
                    agreedToTerms

            Button(
                onClick = {
                    viewModel.register(
                        username = fullName,
                        email = email,
                        password = password
                    )
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
                enabled = isFormValid
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
                    Text(
                        text = "Đăng ký",
                        style = Typography.titleMedium,
                        color = if (isFormValid) Color.White else TextDarkBlue.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    SignUpScreen()
}