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

val EditPrimary = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    // State nhập liệu
    var fullName by remember { mutableStateOf("Nguyễn Văn A") }
    var phone by remember { mutableStateOf("0909123456") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("65") }
    var bodyShape by remember { mutableStateOf("Tam giác ngược") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onSaveClick) {
                        Text("Lưu", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EditPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
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
                modifier = Modifier.size(100.dp)
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
                // Nút Camera nhỏ
                Surface(
                    shape = CircleShape,
                    color = EditPrimary,
                    modifier = Modifier.size(32.dp).padding(2.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
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

            // 2. THÔNG TIN CÁ NHÂN
            Column(modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()) {
                SectionLabel("Thông tin cá nhân")

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 3. SỐ ĐO CƠ THỂ (Quan trọng)
                SectionLabel("Thông số cơ thể (Dùng cho AI Stylist)")

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Chiều cao (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Cân nặng (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Giả lập Dropdown chọn dáng người
                OutlinedTextField(
                    value = bodyShape,
                    onValueChange = {}, // ReadOnly
                    label = { Text("Dáng người") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true,
                    trailingIcon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.rotate(270f)) } // Mũi tên xuống
                )
                Text(
                    "Ví dụ: Quả lê, Đồng hồ cát, Tam giác ngược...",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
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
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = EditPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

// Extension xoay icon mũi tên
fun Modifier.rotate(degrees: Float) = this.then(Modifier.graphicsLayer(rotationZ = degrees))
// Cần import: androidx.compose.ui.graphics.graphicsLayer

@Preview(showBackground = true)
@Composable
fun EditProfilePreview() {
    EditProfileScreen()
}