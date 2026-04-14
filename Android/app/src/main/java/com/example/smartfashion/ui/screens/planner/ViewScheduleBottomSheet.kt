package com.example.smartfashion.ui.screens.planner

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smartfashion.model.Schedule
import com.example.smartfashion.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScheduleBottomSheet(
    schedule: Schedule,
    viewModel: CalendarViewModel,
    onDismiss: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Các state cho Delete
    var isDeleting by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Các state cho Edit
    var isEditing by remember { mutableStateOf(false) }
    var editEventName by remember { mutableStateOf(schedule.eventName ?: "") }
    var editLocation by remember { mutableStateOf(schedule.location ?: "") }
    var isSaving by remember { mutableStateOf(false) }

    val timeFormatted = try {
        val dateStr = schedule.date
        if (!dateStr.isNullOrEmpty() && dateStr.length >= 16) dateStr.substring(11, 16) else "08:00"
    } catch (e: Exception) { "08:00" }

    val dateFormatted = try {
        val dateStr = schedule.date
        if (!dateStr.isNullOrEmpty() && dateStr.length >= 10) {
            val p = dateStr.substring(0, 10).split("-")
            "${p[2]}/${p[1]}/${p[0]}"
        } else ""
    } catch (e: Exception) { "" }

    Surface(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = SecWhite,
        shadowElevation = 16.dp
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
            // Thanh Tiêu đề
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isEditing) "Sửa lịch trình" else "Chi tiết lịch trình",
                    style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null, tint = TextLightBlue) }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ảnh và Tên Outfit
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = schedule.outfitInfo?.image_preview_url ?: "https://i.postimg.cc/9MXZHYtp/3.jpg",
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    if (!isEditing) {
                        Text(schedule.eventName ?: "Lịch trình cá nhân", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Bộ đồ: ${schedule.outfitInfo?.name ?: "Chưa rõ"}", style = MaterialTheme.typography.bodyLarge, color = AccentBlue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ngày và Thời gian
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = AccentBlue.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.AccessTime, null, tint = AccentBlue, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Thời gian", style = MaterialTheme.typography.bodyMedium, color = TextLightBlue)
                    Text("$timeFormatted - Ngày $dateFormatted", style = MaterialTheme.typography.bodyLarge, color = TextDarkBlue, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = editEventName,
                    onValueChange = { editEventName = it },
                    label = { Text("Tên lịch trình") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = editLocation,
                    onValueChange = { editLocation = it },
                    label = { Text("Địa điểm / Ghi chú") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { isEditing = false },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0))
                    ) {
                        Text("Hủy", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue)
                    }

                    Button(
                        onClick = {
                            isSaving = true
                            schedule.scheduleId?.let { id ->
                                coroutineScope.launch {
                                    val success = viewModel.updateSchedule(id, editEventName, editLocation)
                                    isSaving = false
                                    if (success) {
                                        Toast.makeText(context, "Đã cập nhật lịch trình", Toast.LENGTH_SHORT).show()
                                        onDeleteSuccess()
                                    } else {
                                        Toast.makeText(context, "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        enabled = !isSaving && editEventName.isNotBlank()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Lưu", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        }
                    }
                }
            } else {
                if (!schedule.location.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = AccentBlue.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.LocationOn, null, tint = AccentBlue, modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Địa điểm / Ghi chú", style = MaterialTheme.typography.bodyMedium, color = TextLightBlue)
                            Text(schedule.location, style = MaterialTheme.typography.bodyLarge, color = TextDarkBlue, fontWeight = FontWeight.Medium)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { isEditing = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue.copy(alpha = 0.1f))
                    ) {
                        Icon(Icons.Default.Edit, null, tint = AccentBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sửa", style = MaterialTheme.typography.titleMedium, color = AccentBlue)
                    }

                    Button(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftPink.copy(alpha = 0.1f)),
                        enabled = !isDeleting && schedule.scheduleId != null
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = SoftPink)
                        } else {
                            Icon(Icons.Default.DeleteOutline, null, tint = SoftPink)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Xóa", style = MaterialTheme.typography.titleMedium, color = SoftPink)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Xác nhận xóa", style = MaterialTheme.typography.titleLarge, color = TextDarkBlue, fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa lịch trình này không? Hành động này không thể hoàn tác.", style = MaterialTheme.typography.bodyLarge, color = TextLightBlue) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        schedule.scheduleId?.let { id ->
                            isDeleting = true
                            coroutineScope.launch {
                                val isSuccess = viewModel.deleteSchedule(id)
                                isDeleting = false
                                if (isSuccess) {
                                    Toast.makeText(context, "Đã xóa lịch trình", Toast.LENGTH_SHORT).show()
                                    onDeleteSuccess()
                                } else {
                                    Toast.makeText(context, "Lỗi khi xóa lịch!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                ) {
                    Text("Xóa lịch", color = SoftPink, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Hủy", color = TextBlue, fontWeight = FontWeight.Medium)
                }
            },
            containerColor = SecWhite,
            shape = RoundedCornerShape(24.dp)
        )
    }
}