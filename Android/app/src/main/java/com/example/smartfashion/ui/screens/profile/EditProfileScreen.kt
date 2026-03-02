package com.example.smartfashion.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("Nguyễn Văn A") }
    var phone by remember { mutableStateOf("0909123456") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("65") }
    var bodyShape by remember { mutableStateOf("Tam giác ngược") }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Chỉnh sửa hồ sơ",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                },
                actions = {
                    TextButton(onClick = onSaveClick) {
                        Text(
                            text = "Lưu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. AVATAR CHANGE
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(110.dp)
            ) {
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    shape = CircleShape,
                    color = AccentBlue,
                    modifier = Modifier.size(36.dp).padding(2.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, BgLight)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Avatar",
                        tint = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = TextLightBlue.copy(alpha = 0.3f),
                focusedLabelColor = AccentBlue,
                unfocusedLabelColor = TextLightBlue,
                focusedTextColor = TextDarkBlue,
                unfocusedTextColor = TextDarkBlue,
                cursorColor = AccentBlue,
                focusedContainerColor = SecWhite,
                unfocusedContainerColor = SecWhite
            )

            // 2. THÔNG TIN CÁ NHÂN
            Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
                SectionLabel("Thông tin cá nhân")

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ và tên", style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại", style = MaterialTheme.typography.bodyLarge) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = textFieldColors
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 3. SỐ ĐO CƠ THỂ
                SectionLabel("Thông số cơ thể (Dùng cho AI Stylist)")

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Chiều cao (cm)", style = MaterialTheme.typography.bodyLarge) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = textFieldColors
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Cân nặng (kg)", style = MaterialTheme.typography.bodyLarge) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        colors = textFieldColors
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = bodyShape,
                    onValueChange = {},
                    label = { Text("Dáng người", style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    readOnly = true,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = textFieldColors,
                    trailingIcon = {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = TextLightBlue,
                            modifier = Modifier.rotate(270f)
                        )
                    }
                )
                Text(
                    text = "Ví dụ: Quả lê, Đồng hồ cát, Tam giác ngược...",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 11.sp,
                    color = TextLightBlue,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = TextDarkBlue,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

fun Modifier.rotate(degrees: Float) = this.then(Modifier.graphicsLayer(rotationZ = degrees))

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    EditProfileScreen()
}