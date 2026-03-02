package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {}
) {
    var destination by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Du lịch") }
    var selectedTransport by remember { mutableStateOf("Máy bay") }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tạo chuyến đi",
                        style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = TextDarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            val isEnabled = destination.isNotEmpty()
            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = onCreateClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            brush = if (isEnabled) GradientText else Brush.horizontalGradient(listOf(Color.LightGray, Color.LightGray)),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    ),
                    enabled = isEnabled
                ) {
                    Text(
                        text = "Tạo chuyến đi",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hãy nhập thông tin để AI Stylist gợi ý hành lý chuẩn xác nhất nhé!",
                style = MaterialTheme.typography.bodyLarge,
                color = TextLightBlue,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = TextLightBlue.copy(alpha = 0.3f),
                disabledBorderColor = TextLightBlue.copy(alpha = 0.3f),

                focusedLabelColor = AccentBlue,
                unfocusedLabelColor = TextLightBlue,
                disabledLabelColor = TextLightBlue,

                focusedTextColor = TextDarkBlue,
                unfocusedTextColor = TextDarkBlue,
                disabledTextColor = TextDarkBlue,

                focusedLeadingIconColor = TextLightBlue,
                unfocusedLeadingIconColor = TextLightBlue,
                disabledLeadingIconColor = TextLightBlue,

                focusedTrailingIconColor = TextLightBlue,
                unfocusedTrailingIconColor = TextLightBlue,

                cursorColor = AccentBlue,
                focusedContainerColor = SecWhite,
                unfocusedContainerColor = SecWhite
            )

            InputSectionTitle("Bạn đi đâu?")
            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                placeholder = { Text("Ví dụ: Đà Lạt, Paris, Tokyo...", style = MaterialTheme.typography.bodyLarge, color = TextLightBlue.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(24.dp))

            InputSectionTitle("Thời gian?")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Ngày đi
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Ngày đi", style = MaterialTheme.typography.bodyLarge) },
                    placeholder = { Text("dd/mm", style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = textFieldColors
                )
                // Ngày về
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Ngày về", style = MaterialTheme.typography.bodyLarge) },
                    placeholder = { Text("dd/mm", style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { Icon(Icons.Default.CalendarMonth, null) },
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = textFieldColors
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            InputSectionTitle("Mục đích chuyến đi?")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val types = listOf(
                    "Du lịch" to Icons.Default.BeachAccess,
                    "Công tác" to Icons.Default.BusinessCenter,
                    "Leo núi" to Icons.Default.Hiking,
                    "Khác" to Icons.Default.LocationOn
                )
                items(types) { (name, icon) ->
                    SelectionCard(
                        text = name,
                        icon = icon,
                        isSelected = selectedType == name,
                        onClick = { selectedType = name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            InputSectionTitle("Di chuyển bằng?")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val transports = listOf(
                    "Máy bay" to Icons.Default.Flight,
                    "Xe hơi" to Icons.Default.DirectionsCar,
                    "Tàu hỏa" to Icons.Default.Train
                )
                items(transports) { (name, icon) ->
                    SelectionCard(
                        text = name,
                        icon = icon,
                        isSelected = selectedTransport == name,
                        onClick = { selectedTransport = name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun InputSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextDarkBlue,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun SelectionCard(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AccentBlue.copy(alpha = 0.1f) else SecWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp),
        modifier = Modifier
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = if (isSelected) AccentBlue else TextLightBlue.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .widthIn(min = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AccentBlue else TextLightBlue,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AccentBlue else TextLightBlue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTripPreview() {
    CreateTripScreen()
}