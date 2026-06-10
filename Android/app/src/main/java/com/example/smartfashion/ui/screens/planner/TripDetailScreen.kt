package com.example.smartfashion.ui.screens.planner

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartfashion.ui.theme.*
import com.example.smartfashion.model.PackingItem
import java.time.format.DateTimeFormatter
import androidx.navigation.NavController
import androidx.compose.material3.Checkbox
import com.example.smartfashion.data.api.DayPlan
import com.example.smartfashion.ui.viewmodel.TripDetailViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TripDetailScreen(
    navController: NavController,
    tripId: String,
    onBackClick: () -> Unit = {},
    onAddOutfitClick: (DayPlan) -> Unit = {},
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var itemToEdit by remember { mutableStateOf<PackingItem?>(null) }
    var itemToDelete by remember { mutableStateOf<PackingItem?>(null) }
    var editItemName by remember { mutableStateOf("") }

    val trip = viewModel.trip
    val isLoading = viewModel.isLoading
    val tabs = listOf("Trang phục", "Checklist")

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val selectedOutfitId = savedStateHandle?.get<Int>("selectedOutfitId")
    val selectedOutfitImage = savedStateHandle?.get<String>("selectedOutfitImage")
    val selectedDay = savedStateHandle?.get<Int>("selectedDay")

    LaunchedEffect(selectedOutfitId, selectedDay, selectedOutfitImage){
        if (selectedOutfitId != null && selectedDay != null && selectedOutfitImage != null) {
            viewModel.assignOutfitToDay(selectedDay, selectedOutfitId, selectedOutfitImage)
            savedStateHandle.remove<Int>("selectedOutfitId")
            savedStateHandle.remove<String>("selectedOutfitImage")
            savedStateHandle.remove<Int>("selectedDay")
        }
    }

    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId.toIntOrNull() ?: 0)
    }

    val dayPlans = viewModel.dayPlans

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Xóa chuyến đi", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Bạn có chắc chắn muốn xóa chuyến đi đến ${trip?.destination} không? Thao tác này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteTrip(onSuccess = { onBackClick() })
                }) { Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy", color = TextLightBlue) }
            },
            containerColor = SecWhite
        )
    }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Xóa món đồ", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn xóa '${itemToDelete!!.name}' khỏi danh sách chuẩn bị không?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePackingItem(itemToDelete!!.id, onSuccess = {
                        Toast.makeText(context, "Đã xóa đồ khỏi danh sách!", Toast.LENGTH_SHORT).show()
                    })
                    itemToDelete = null
                }) { Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("Hủy", color = TextLightBlue) }
            },
            containerColor = SecWhite
        )
    }

    if (itemToEdit != null) {
        AlertDialog(
            onDismissRequest = { itemToEdit = null },
            title = { Text("Chỉnh sửa món đồ", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = editItemName,
                    onValueChange = { editItemName = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.editPackingItem(itemToEdit!!.id, editItemName)
                    Toast.makeText(context, "Đã cập nhật món đồ!", Toast.LENGTH_SHORT).show()
                    itemToEdit = null
                }) { Text("Lưu", fontWeight = FontWeight.Bold, color = AccentBlue) }
            },
            dismissButton = {
                TextButton(onClick = { itemToEdit = null }) { Text("Hủy", color = TextLightBlue) }
            },
            containerColor = SecWhite
        )
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AccentBlue)
        }
    } else {
        Scaffold(
            containerColor = BgLight
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                item {
                    val formatDisplayDate = { isoString: String ->
                        try {
                            if (isoString.isNotBlank()) {
                                val parts = isoString.substringBefore("T").split("-")
                                if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else isoString
                            } else ""
                        } catch (e: Exception) { isoString }
                    }

                    val displayImageUrl = getDefaultImageForTripType(trip?.trip_type)

                    TripHeroHeader(
                        title = trip?.destination ?: "Chuyến đi",
                        startDate = formatDisplayDate(trip?.start_date ?: ""),
                        endDate = formatDisplayDate(trip?.end_date ?: ""),
                        imageUrl = displayImageUrl,
                        onBack = onBackClick,
                        onDeleteClick = { showDeleteDialog = true }
                    )
                }

                stickyHeader {
                    Surface(color = SecWhite, shadowElevation = 2.dp) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = SecWhite,
                            contentColor = AccentBlue,
                            indicator = { tabPositions ->
                                SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = AccentBlue,
                                    height = 3.dp
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(title, color = if (selectedTab == index) TextPink else TextLightBlue) }
                                )
                            }
                        }
                    }
                }

                if (selectedTab == 0) {
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    itemsIndexed(dayPlans) { index, plan ->
                        DayOutfitItem(
                            plan = plan,
                            isLastItem = index == dayPlans.size - 1,
                            onAddClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("selectedDay", plan.dayNumber)
                                onAddOutfitClick(plan)
                            },
                            onDeleteOutfit = { outfitId ->
                                viewModel.removeOutfitFromDay(plan.dayNumber, outfitId)
                            }
                        )
                    }
                } else {
                    item {
                        var newItemText by remember { mutableStateOf("") }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newItemText,
                                onValueChange = { newItemText = it },
                                placeholder = { Text("Thêm đồ cá nhân...", fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = TextLightBlue.copy(0.3f)
                                )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    if (newItemText.isNotBlank()) {
                                        viewModel.addPackingItem(newItemText, onSuccess = {
                                            Toast.makeText(context, "Đã thêm vào danh sách!", Toast.LENGTH_SHORT).show()
                                        })
                                        newItemText = ""
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                            ) {
                                Text("Thêm", color = Color.White)
                            }
                        }
                    }

                    itemsIndexed(viewModel.packingItems) { _, item ->
                        PackingChecklistItem(
                            item = item,
                            onToggle = { viewModel.togglePacked(item.id) },
                            onEditClick = {
                                editItemName = item.name
                                itemToEdit = item
                            },
                            onDeleteClick = {
                                itemToDelete = item
                            }
                        )
                    }

                    if (viewModel.packingItems.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Chưa có đồ nào trong danh sách.", color = TextLightBlue)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun DayOutfitItem(
    plan: DayPlan,
    isLastItem: Boolean,
    onAddClick: () -> Unit,
    onDeleteOutfit: (Int) -> Unit
) {
    val displayDate = remember(plan.date) {
        val formatter = DateTimeFormatter.ofPattern("dd 'Th' MM")
        plan.date.format(formatter)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.width(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = AccentBlue.copy(alpha = 0.2f),
                modifier = Modifier.size(24.dp).padding(top = 4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(AccentBlue))
                }
            }
            if (!isLastItem) {
                Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(AccentBlue.copy(alpha = 0.3f)))
            }
        }

        Column(modifier = Modifier.weight(1f).padding(start = 12.dp, bottom = 32.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ngày ${plan.dayNumber}", color = TextPink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("  •  ", color = TextLightBlue, fontSize = 16.sp)
                Text(displayDate, color = TextDarkBlue, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, null, tint = TextLightBlue.copy(0.8f), modifier = Modifier.size(16.dp))
                Text(" ${plan.location}   ", color = TextDarkBlue, fontSize = 14.sp)

                val weatherIcon = if (plan.isSunny) Icons.Outlined.WbSunny else Icons.Outlined.CloudQueue
                Icon(weatherIcon, null, tint = if (plan.isSunny) Color(0xFFFFB300) else AccentBlue, modifier = Modifier.size(16.dp))
                Text(" ${plan.weatherTemp}", color = TextDarkBlue, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            plan.outfits.forEach { outfit ->
                var expanded by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SecWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = outfit.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                            Text("Set trang phục đã chọn", style = MaterialTheme.typography.titleMedium, color = TextDarkBlue, fontSize = 14.sp)
                        }
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, null, tint = TextLightBlue)
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(SecWhite)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Xóa", color = Color.Red) },
                                    onClick = {
                                        expanded = false
                                        onDeleteOutfit(outfit.outfitId)
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Rounded.Delete, contentDescription = null, tint = Color.Red)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(SecWhite, RoundedCornerShape(16.dp))
                    .drawBehind {
                        drawRoundRect(
                            color = AccentBlue.copy(alpha = 0.4f),
                            style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)),
                            cornerRadius = CornerRadius(16.dp.toPx())
                        )
                    }
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, tint = AccentBlue, modifier = Modifier.size(20.dp))
                    Text(
                        text = if (plan.outfits.isEmpty()) " Thêm trang phục ngày ${plan.dayNumber}" else " Thêm bộ khác",
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun TripHeroHeader(
    title: String,
    startDate: String,
    endDate: String,
    imageUrl: String,
    onBack: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.8f)))))

        Column(modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CalendarMonth, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(16.dp))
                Text(" $startDate - $endDate", color = Color.White.copy(0.9f))
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars).padding(8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(8.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Xóa chuyến đi", tint = Color.White)
        }
    }
}

@Composable
fun PackingChecklistItem(
    item: PackingItem,
    onToggle: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecWhite)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isPacked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = AccentBlue)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    color = TextDarkBlue,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.category,
                    color = TextLightBlue,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Rounded.Edit, contentDescription = "Sửa", tint = TextLightBlue, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Rounded.Delete, contentDescription = "Xóa", tint = Color.Red.copy(0.7f), modifier = Modifier.size(20.dp))
            }
        }
    }
}