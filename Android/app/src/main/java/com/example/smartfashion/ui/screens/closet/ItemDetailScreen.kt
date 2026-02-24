package com.example.smartfashion.ui.screens.closet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.* // Dùng bộ icon Rounded cho đồng bộ
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Màu chủ đạo (Lấy từ AddItemScreen của bạn để đồng bộ)
val DetailScreenPrimary = Color(0xFF6200EE)

// Data Model (Giữ nguyên)
data class ClosetItem(
    val id: String = "1",
    val name: String = "Midi Skirt", // Tên ngắn gọn
    val brand: String = "Uniqlo",
    val imageUrl: String = "https://i.postimg.cc/9MXZHYtp/3.jpg",
    val category: String = "Skirts > Midi Skirt",
    val season: List<String> = listOf("Spring", "Summer"),
    val color: Color = Color.Black, // Màu thực tế của món đồ
    val colorName: String = "Black",
    val occasion: String = "Daily, Work",
    val material: String = "Cotton",
    val pattern: String = "Solid",
    val size: String = "M",
    val price: String = "299.000 đ",
    val purchaseDate: String = "12/01/2024"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: ClosetItem = ClosetItem(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onMixMatchClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    // Có thể để trống hoặc để tên App
                    Text("Item Details", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(20.dp))
                    }
                },
                actions = {
                    // Nút Edit và Delete ở góc trên phải
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = Color.Gray)
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Nút "Phối đồ" (Mix Match) - Style giống nút Save
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                color = Color.Transparent
            ) {
                Button(
                    onClick = onMixMatchClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A))
                ) {
                    Icon(Icons.Rounded.Style, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mix & Match", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            // 1. KHU VỰC HIỂN THỊ ẢNH (Đồng bộ với AddItemScreen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Chiều cao bằng với màn hình Add
                    .background(Color.White), // Nền trắng
                contentAlignment = Alignment.Center
            ) {
                // Ảnh món đồ
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .fillMaxHeight(0.9f) // Ảnh chiếm 90% chiều cao box
                        .fillMaxWidth(0.9f), // Không tràn viền, để lại khoảng trắng
                    contentScale = ContentScale.Fit, // Fit để thấy toàn bộ dáng đồ
                    error = painterResource(android.R.drawable.ic_menu_report_image),
                    placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                )

                // Tag "Mùa" hiển thị nổi trên ảnh (Optional)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.05f)
                ) {
                    Text(
                        text = item.season.joinToString(", "),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 8.dp)

            // 2. THÔNG TIN CƠ BẢN
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = item.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.brand} • ${item.price}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            HorizontalDivider(color = Color(0xFFF5F5F5))

            // 3. CHI TIẾT (Sử dụng lại hàm DetailRow giống InfoRow của AddItem)
            DetailRow(label = "Category", value = item.category, highlightValue = true)
            DetailRow(label = "Occasion", value = item.occasion)

            // Dòng Màu sắc (Custom)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Color", fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.colorName, fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(item.color)
                            .border(1.dp, Color.LightGray, CircleShape)
                    )
                }
            }
            HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(horizontal = 20.dp))

            DetailRow(label = "Material", value = item.material)
            DetailRow(label = "Pattern", value = item.pattern)
            DetailRow(label = "Size", value = item.size)
            DetailRow(label = "Purchase Date", value = item.purchaseDate)

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// Hàm hiển thị dòng thông tin (Tương tự InfoRow nhưng không có mũi tên Dropdown vì đây là màn hình xem)
@Composable
fun DetailRow(
    label: String,
    value: String,
    highlightValue: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontWeight = FontWeight.SemiBold, color = Color.Gray, modifier = Modifier.weight(1f))
            Text(
                text = value,
                color = if (highlightValue) DetailScreenPrimary else Color.Black,
                fontWeight = if (highlightValue) FontWeight.Medium else FontWeight.Normal,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp) // Để text không dính sát nếu label dài
            )
        }
        HorizontalDivider(color = Color(0xFFF5F5F5), modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailPreview() {
    ItemDetailScreen()
}