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

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientPrimaryButton
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val outfitSuggestionUrl: String? = null
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
        containerColor = BgLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "AI Stylist",
                            style = MaterialTheme.typography.titleLarge.copy(brush = GradientText),
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF4CAF50)))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Đang trực tuyến",
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 11.sp,
                                color = TextLightBlue
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDarkBlue)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
            )
        },
        bottomBar = {
            // Thanh nhập tin nhắn
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
                .padding(horizontal = 20.dp),
            reverseLayout = false,
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(msg)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val bubbleShape = if (message.isUser) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
    }

    val textColor = if (message.isUser) Color.White else TextDarkBlue

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        // Avatar AI
        if (!message.isUser) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = AccentBlue.copy(alpha = 0.1f),
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, null, tint = AccentBlue, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI Stylist", style = MaterialTheme.typography.titleMedium, fontSize = 12.sp, color = TextLightBlue)
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Nội dung tin nhắn
        Surface(
            shape = bubbleShape,
            color = if (message.isUser) Color.Transparent else SecWhite,
            shadowElevation = if (message.isUser) 0.dp else 2.dp,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .then(
                    if (message.isUser) Modifier.background(GradientText, bubbleShape)
                    else Modifier
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                if (message.outfitSuggestionUrl != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clickable { /* Xem chi tiết outfit */ },
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Box {
                            AsyncImage(
                                model = message.outfitSuggestionUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                color = SecWhite.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(topEnd = 12.dp, bottomStart = 8.dp),
                                modifier = Modifier.align(Alignment.BottomStart)
                            ) {
                                Text(
                                    text = "Thử ngay",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextDarkBlue,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgLight)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = SecWhite,
                shadowElevation = 1.dp,
                modifier = Modifier.size(48.dp)
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Image, contentDescription = "Image", tint = TextLightBlue)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Ô Nhập Liệu Chính
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = SecWhite,
                shadowElevation = 1.dp
            ) {
                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(
                            "Hỏi AI Stylist...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextLightBlue.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = TextDarkBlue,
                        unfocusedTextColor = TextDarkBlue,
                        cursorColor = AccentBlue
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = true,

                    trailingIcon = {
                        Icon(Icons.Default.Mic, contentDescription = "Voice", tint = AccentBlue)
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GradientPrimaryButton)
                    .clickable { onSend() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiChatScreenPreview() {
    AiChatScreen()
}