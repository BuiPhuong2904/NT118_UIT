package com.example.smartfashion.ui.screens.planner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo
val SchedulePrimary = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBottomSheet(
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    // State giả lập nhập liệu
    var eventName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("Feb 15, 2026") }
    var selectedTime by remember { mutableStateOf("08:00 AM") }
    var note by remember { mutableStateOf("") }

    // Dùng Surface để giả lập BottomSheet
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(), // Chiều cao tự động theo nội dung
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color.White,
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
                    text = "Schedule Outfit",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. PREVIEW BỘ ĐỒ ĐANG CHỌN (Outfit Preview)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ảnh thumb bộ đồ
                AsyncImage(
                    model = "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Tên bộ đồ
                Column {
                    Text("Summer Office Look", fontWeight = FontWeight.Bold)
                    Text("3 items • Created today", fontSize = 12.sp, color = Color.Gray)
                    Text("Tap to change", fontSize = 12.sp, color = SchedulePrimary, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. FORM NHẬP LIỆU (Event Name)
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name (e.g., Work, Party)") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Event, null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SchedulePrimary,
                    focusedLabelColor = SchedulePrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. CHỌN NGÀY & GIỜ (Date & Time Pickers)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Date Picker (Giả lập)
                OutlinedTextField(
                    value = selectedDate,
                    onValueChange = {},
                    label = { Text("Date") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    readOnly = true, // Không cho nhập tay, bấm vào thì hiện lịch
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                )

                // Time Picker (Giả lập)
                OutlinedTextField(
                    value = selectedTime,
                    onValueChange = {},
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.AccessTime, null) },
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Note (Ghi chú thêm)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 5. NÚT LƯU (Save Button)
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Add to Calendar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            // Khoảng trống an toàn dưới cùng (cho gesture bar của điện thoại)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Preview riêng lẻ component này để dễ chỉnh sửa
@Preview(showBackground = true)
@Composable
fun ScheduleSheetPreview() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Gray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        ScheduleBottomSheet()
    }
}