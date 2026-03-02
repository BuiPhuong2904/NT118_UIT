package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage

// Import bộ Theme và Typography của bạn
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

data class OutfitPreviewInfo(
    val id: String,
    val name: String,
    val imageUrl: String,
    val itemCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBottomSheet(
    navController: NavController,
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    var eventName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("15/02/2026") }
    var selectedTime by remember { mutableStateOf("08:00 AM") }
    var note by remember { mutableStateOf("") }

    var selectedOutfit by remember { mutableStateOf<OutfitPreviewInfo?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = SecWhite,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // 1. THANH TIÊU ĐỀ & NÚT ĐÓNG
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lên lịch trang phục",
                    style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Đóng", tint = TextLightBlue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. KHU VỰC CHỌN BỘ ĐỒ
            if (selectedOutfit == null) {
                // TRẠNG THÁI 1: CHƯA CHỌN ĐỒ
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(BgLight, RoundedCornerShape(16.dp))
                        .drawBehind {
                            drawRoundRect(
                                color = AccentBlue.copy(alpha = 0.5f),
                                style = Stroke(
                                    width = 3f,
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                ),
                                cornerRadius = CornerRadius(16.dp.toPx())
                            )
                        }
                        .clickable {
                            navController.navigate("select_outfit_calendar")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = CircleShape,
                            color = AccentBlue.copy(alpha = 0.1f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Thêm", tint = AccentBlue, modifier = Modifier.padding(6.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Chạm để chọn trang phục",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // TRẠNG THÁI 2: ĐÃ CHỌN ĐỒ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(BgLight, RoundedCornerShape(16.dp))
                        .border(1.dp, TextLightBlue.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable {
                            navController.navigate("select_outfit_calendar")
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = selectedOutfit!!.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = selectedOutfit!!.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextDarkBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${selectedOutfit!!.itemCount} món • Cập nhật hôm nay",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 12.sp,
                            color = TextLightBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Chạm để đổi/xóa",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 12.sp,
                            color = AccentBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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

                cursorColor = AccentBlue
            )

            // 3. FORM NHẬP LIỆU
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Tên sự kiện (VD: Đi làm, Dự tiệc)", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Event, null) },
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. CHỌN NGÀY & GIỜ
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    label = { Text("Ngày", style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.weight(1f).clickable { /* Mở DatePicker */ },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = textFieldColors
                )

                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = {},
                    label = { Text("Giờ", style = MaterialTheme.typography.bodyLarge) },
                    modifier = Modifier.weight(1f).clickable { /* Mở TimePicker */ },
                    leadingIcon = { Icon(Icons.Default.AccessTime, null) },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = textFieldColors
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú (Tùy chọn)", style = MaterialTheme.typography.bodyLarge) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. NÚT LƯU
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(GradientText, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text(
                    text = "Lưu vào Lịch",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScheduleSheetPreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        ScheduleBottomSheet(navController = rememberNavController())
    }
}