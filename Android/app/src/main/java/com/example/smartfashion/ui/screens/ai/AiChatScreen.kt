package com.example.smartfashion.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
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

// Màu chủ đạo cho AI
val AiPrimary = Color(0xFF6200EE)
val AiBubbleColor = Color(0xFFF3E5F5) // Tím nhạt

// Data Model tin nhắn
data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val outfitSuggestionUrl: String? = null // Nếu AI gợi ý đồ thì có thêm ảnh này
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onBackClick: () -> Unit = {}
) {
    // Dữ liệu giả lập cuộc hội thoại
    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "Xin chào! Mình là AI Stylist. Hôm nay bạn cần tư vấn gì không?", false),
            ChatMessage("2", "Mai mình đi đám cưới ngoài trời buổi tối, tư vấn giúp mình với!", true),
            ChatMessage("3", "Đám cưới ngoài trời thường thoáng đãng. Mình gợi ý bạn một chiếc váy midi lụa nhẹ nhàng, phối cùng sandal cao gót nhé.", false),
            ChatMessage("4", "Đây là một set đồ mẫu phù hợp với dáng người của bạn:", false, outfitSuggestionUrl = "https://i.postimg.cc/9MXZHYtp/3.jpg")
        )
    }

    var inputText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            // Header riêng cho màn hình Chat
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AI Stylist", fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Green))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Online", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Thanh nhập tin nhắn (Chat Input)
            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        messages.add(ChatMessage("x", inputText, true))
                        inputText = ""
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = false, // Tin nhắn mới nhất ở dưới
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(msg)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val bubbleShape = if (message.isUser) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    }

    val backgroundColor = if (message.isUser) AiPrimary else AiBubbleColor
    val textColor = if (message.isUser) Color.White else Color.Black

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        // Avatar AI
        if (!message.isUser) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = AiPrimary,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stylist", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Nội dung tin nhắn
        Surface(
            shape = bubbleShape,
            color = backgroundColor,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )

                // Nếu có gợi ý Outfit (Ảnh)
                if (message.outfitSuggestionUrl != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clickable { /* Xem chi tiết outfit */ },
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box {
                            AsyncImage(
                                model = message.outfitSuggestionUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Tag nhỏ trên ảnh
                            Surface(
                                color = Color.Black.copy(0.6f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                            ) {
                                Text(
                                    "Bấm để thử",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút gửi ảnh
        IconButton(onClick = {}) {
            Icon(Icons.Default.Image, contentDescription = "Image", tint = Color.Gray)
        }

        // Ô nhập liệu
        TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text("Hỏi Stylist...", fontSize = 14.sp) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF5F5F5),
                unfocusedContainerColor = Color(0xFFF5F5F5)
            ),
            singleLine = true,
            trailingIcon = {
                Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.Gray)
            }
        )

        // Nút gửi
        IconButton(
            onClick = onSend,
            modifier = Modifier
                .background(AiPrimary, CircleShape)
                .size(48.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    AiChatScreen()
}