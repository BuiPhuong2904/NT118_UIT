package com.example.smartfashion.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartfashion.model.UpdateProfileRequest
import com.example.smartfashion.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit = {},
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current // Thêm focusManager để ẩn bàn phím
    
    val profileResponse by viewModel.profileResponse
    val isLoading by viewModel.isLoading
    val errorMessage by viewModel.errorMessage
    val updateSuccess by viewModel.updateSuccess

    val profile = profileResponse?.data

    // States
    var avatarUrl by remember { mutableStateOf("https://tuanluupiano.com/wp-content/uploads/2026/01/avatar-facebook-mac-dinh-6.jpg") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Khác") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var bodyShape by remember { mutableStateOf("Tam giác ngược") }
    var skinTone by remember { mutableStateOf("Trung bình") }
    var styleFavourite by remember { mutableStateOf("") }
    var colorsFavourite by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Biến flag để chỉ load dữ liệu từ API vào TextField một lần duy nhất
    var hasInitialized by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> selectedImageUri = uri }

    val skinToneOptions = listOf("Trắng sáng", "Trắng hồng", "Trung bình", "Ngăm", "Đen")

    // --- HELPERS ---
    fun skinToneToHex(value: String): String = when (value) {
        "Trắng sáng" -> "#FFE7C6"
        "Trắng hồng" -> "#F2C6A0"
        "Trung bình" -> "#D2A679"
        "Ngăm" -> "#8D5524"
        "Đen" -> "#3B2A1A"
        else -> "#D2A679"
    }

    fun hexToSkinTone(hex: String?): String = when (hex?.uppercase()) {
        "#FFE7C6" -> "Trắng sáng"
        "#F2C6A0" -> "Trắng hồng"
        "#D2A679" -> "Trung bình"
        "#8D5524" -> "Ngăm"
        "#3B2A1A" -> "Đen"
        else -> "Trung bình"
    }

    // --- EFFECTS ---
    LaunchedEffect(Unit) {
        viewModel.getMyProfile()
    }

    LaunchedEffect(profile) {
        if (profile != null && !hasInitialized) {
            avatarUrl = profile.avatarUrl ?: "https://tuanluupiano.com/wp-content/uploads/2026/01/avatar-facebook-mac-dinh-6.jpg"
            fullName = profile.username ?: ""
            email = profile.email ?: ""
            phone = profile.phoneNumber ?: ""
            gender = profile.gender ?: "Khác"
            height = if (profile.height != null && profile.height != 0.0) profile.height.toInt().toString() else ""
            weight = if (profile.weight != null && profile.weight != 0.0) profile.weight.toInt().toString() else ""
            bodyShape = profile.bodyShape ?: "Tam giác ngược"
            skinTone = hexToSkinTone(profile.skinTone)
            styleFavourite = profile.styleFavourite ?: ""
            colorsFavourite = profile.colorsFavourite ?: ""
            hasInitialized = true // Đánh dấu đã load xong dữ liệu ban đầu
        }
    }

    LaunchedEffect(updateSuccess) {
        if (updateSuccess) {
            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
            selectedImageUri = null
            viewModel.resetUpdateState()
            viewModel.getMyProfile() // Reload để lấy URL ảnh mới nhất
        }
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chỉnh sửa hồ sơ", 
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextDarkBlue)
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            focusManager.clearFocus() // Ẩn bàn phím khi nhấn lưu
                            val request = UpdateProfileRequest(
                                username = fullName.trim(),
                                phone_number = phone.trim(),
                                gender = gender,
                                height = height.toDoubleOrNull(),
                                weight = weight.toDoubleOrNull(),
                                body_shape = bodyShape,
                                skin_tone = skinToneToHex(skinTone),
                                style_favourite = styleFavourite.trim(),
                                colors_favourite = colorsFavourite.trim()
                            )
                            viewModel.updateMyProfile(request, selectedImageUri, context)
                        },
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = AccentBlue)
                        } else {
                            Text("Lưu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AccentBlue)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        }
    ) { paddingValues ->
        if (isLoading && !hasInitialized) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, modifier = Modifier.padding(horizontal = 24.dp))
                    Spacer(Modifier.height(12.dp))
                }

                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.size(110.dp)) {
                    AsyncImage(
                        model = selectedImageUri ?: if (avatarUrl.isNotEmpty()) "$avatarUrl?t=${System.currentTimeMillis()}" else "https://i.postimg.cc/9MXZHYtp/3.jpg", 
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        shape = CircleShape, 
                        color = AccentBlue, 
                        modifier = Modifier.size(36.dp).padding(2.dp).clickable { launcher.launch("image/*") },
                        border = androidx.compose.foundation.BorderStroke(2.dp, BgLight)
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.padding(6.dp))
                    }
                }

                Spacer(Modifier.height(32.dp))

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

                Column(Modifier.padding(horizontal = 24.dp).fillMaxWidth()) {
                    SectionLabel("Thông tin cá nhân")
                    OutlinedTextField(
                        value = email, onValueChange = {}, readOnly = true, label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray), colors = textFieldColors
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = fullName, onValueChange = { fullName = it }, label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = textFieldColors
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it }, label = { Text("Số điện thoại") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = textFieldColors
                    )
                    Spacer(Modifier.height(16.dp))
                    DropdownField(
                        label = "Giới tính", options = listOf("Nam", "Nữ", "Khác"),
                        selectedValue = gender, onValueChange = { gender = it }, colors = textFieldColors
                    )
                    Spacer(Modifier.height(32.dp))

                    SectionLabel("Thông số cơ thể")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = height, onValueChange = { height = it }, label = { Text("Cao (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = weight, onValueChange = { weight = it }, label = { Text("Nặng (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = textFieldColors
                        )
                    }
                    Spacer(Modifier.height(24.dp))

                    SectionLabel("Màu da")
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        skinToneOptions.forEach { tone ->
                            val isSelected = skinTone == tone
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(getSkinColor(tone))
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) AccentBlue else Color.LightGray,
                                        shape = CircleShape
                                    )
                                    .clickable { skinTone = tone },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, null, tint = if (tone == "Đen" || tone == "Ngăm") Color.White else Color.Black)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))

                    DropdownField(
                        label = "Dáng người",
                        options = listOf("Quả lê", "Đồng hồ cát", "Tam giác ngược", "Quả táo", "Hình chữ nhật"),
                        selectedValue = bodyShape, onValueChange = { bodyShape = it }, colors = textFieldColors
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = styleFavourite, onValueChange = { styleFavourite = it }, label = { Text("Phong cách yêu thích") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = textFieldColors
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = colorsFavourite, onValueChange = { colorsFavourite = it }, label = { Text("Màu sắc yêu thích") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = textFieldColors
                    )
                }
                Spacer(Modifier.height(50.dp))
            }
        }
    }
}

fun getSkinColor(tone: String): Color = when (tone) {
    "Trắng sáng" -> Color(0xFFFFE7C6)
    "Trắng hồng" -> Color(0xFFF2C6A0)
    "Trung bình" -> Color(0xFFD2A679)
    "Ngăm" -> Color(0xFF8D5524)
    "Đen" -> Color(0xFF3B2A1A)
    else -> Color.Gray
}

@Composable
fun SectionLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDarkBlue,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(label: String, options: List<String>, selectedValue: String, onValueChange: (String) -> Unit, colors: TextFieldColors) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedValue, onValueChange = {}, readOnly = true, label = { Text(label) },
            modifier = Modifier.menuAnchor().fillMaxWidth(), shape = RoundedCornerShape(16.dp),
            colors = colors, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onValueChange(option); expanded = false })
            }
        }
    }
}