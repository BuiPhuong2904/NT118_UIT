package com.example.smartfashion.ui.screens.planner

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smartfashion.data.local.TokenManager
import com.example.smartfashion.data.api.ScheduleRequest

// Import bộ Theme
import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class OutfitPreviewInfo(
    val id: Int,
    val name: String,
    val imageUrl: String
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleBottomSheet(
    navController: NavController,
    viewModel: CalendarViewModel = hiltViewModel(),
    onDismiss: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val userId = remember { TokenManager(context).getUserId() }
    val coroutineScope = rememberCoroutineScope()

    // 1. Lấy ngày đang được chọn từ lịch ngoài kia làm mặc định
    val selectedDateObj by viewModel.selectedDate.collectAsState()
    val dateStringForUI = selectedDateObj.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    val dateStringForAPI = selectedDateObj.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    // State của form nhập
    var eventName by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("08:00") }
    var note by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var selectedOutfit by remember { mutableStateOf<OutfitPreviewInfo?>(null) }

    // 2. Lắng nghe kết quả trả về từ màn hình "select_outfit_calendar"
    val currentEntry = navController.currentBackStackEntry
    val savedStateHandle = currentEntry?.savedStateHandle

    LaunchedEffect(currentEntry) {
        val returnedOutfitId = savedStateHandle?.get<Int>("selectedOutfitId")
        if (returnedOutfitId != null) {
            val returnedOutfitName = savedStateHandle.get<String>("selectedOutfitName") ?: "Đã chọn"
            val returnedOutfitImage = savedStateHandle.get<String>("selectedOutfitImage") ?: ""

            selectedOutfit = OutfitPreviewInfo(
                id = returnedOutfitId,
                name = returnedOutfitName,
                imageUrl = returnedOutfitImage
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = SecWhite,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth()
        ) {
            // THANH TIÊU ĐỀ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Lên lịch trang phục", style = MaterialTheme.typography.titleLarge.copy(brush = GradientText), fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Đóng", tint = TextLightBlue) }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // KHU VỰC CHỌN ĐỒ
            if (selectedOutfit == null) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp).background(BgLight, RoundedCornerShape(16.dp))
                        .drawBehind {
                            drawRoundRect(color = AccentBlue.copy(alpha = 0.5f), style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)), cornerRadius = CornerRadius(16.dp.toPx()))
                        }
                        .clickable { navController.navigate("select_outfit_calendar") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(shape = CircleShape, color = AccentBlue.copy(alpha = 0.1f), modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Rounded.Add, contentDescription = "Thêm", tint = AccentBlue, modifier = Modifier.padding(6.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Chạm để chọn trang phục", style = MaterialTheme.typography.bodyLarge, color = AccentBlue, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp).background(BgLight, RoundedCornerShape(16.dp)).border(1.dp, TextLightBlue.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        .clickable { navController.navigate("select_outfit_calendar") }.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = selectedOutfit!!.imageUrl.ifEmpty { "https://i.postimg.cc/9MXZHYtp/3.jpg" },
                        contentDescription = null,
                        modifier = Modifier.size(76.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.Center) {
                        Text(selectedOutfit!!.name, style = MaterialTheme.typography.titleMedium, color = TextDarkBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Chạm để đổi trang phục", style = MaterialTheme.typography.bodyLarge, fontSize = 12.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FORM NHẬP LIỆU
            OutlinedTextField(
                value = eventName, onValueChange = { eventName = it },
                label = { Text("Tên sự kiện (VD: Đi làm, Dự tiệc)") },
                modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Event, null) },
                shape = RoundedCornerShape(16.dp), singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = dateStringForUI, onValueChange = {},
                    label = { Text("Ngày") }, modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.CalendarToday, null) }, readOnly = true, enabled = false,
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = selectedTime, onValueChange = { selectedTime = it },
                    label = { Text("Giờ (HH:mm)") }, modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.AccessTime, null) },
                    shape = RoundedCornerShape(16.dp), singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note, onValueChange = { note = it },
                label = { Text("Địa điểm/Ghi chú (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth(), maxLines = 3,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // NÚT LƯU CÓ GỌI API
            Button(
                onClick = {
                    if (selectedOutfit == null) {
                        Toast.makeText(context, "Vui lòng chọn 1 bộ trang phục", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (eventName.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập tên sự kiện", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // Ghép ngày và giờ lại thành 1 chuỗi chuẩn
                    val fullDateTimeString = "${dateStringForAPI}T${selectedTime}:00.000Z"

                    val newSchedule = ScheduleRequest(
                        user_id = userId,
                        outfit_id = selectedOutfit!!.id,
                        date = fullDateTimeString,
                        event_name = eventName,
                        event_type = "Daily",
                        location = note
                    )

                    isSaving = true

                    coroutineScope.launch {
                        // Đổi tên biến thành resultMessage cho dễ hiểu
                        val resultMessage = viewModel.createNewSchedule(newSchedule)
                        isSaving = false

                        if (resultMessage == "SUCCESS") {
                            Toast.makeText(context, "Đã lưu lịch trình!", Toast.LENGTH_SHORT).show()
                            savedStateHandle?.remove<Int>("selectedOutfitId")
                            onSaveSuccess()
                        } else {
                            // In thẳng cái lỗi chi tiết ra màn hình để biết tại sao Server không cho lưu
                            Toast.makeText(context, resultMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp).background(GradientText, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.LightGray),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Lưu vào Lịch", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}