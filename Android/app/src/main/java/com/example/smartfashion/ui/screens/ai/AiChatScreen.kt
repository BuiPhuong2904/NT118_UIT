package com.example.smartfashion.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    var isDrawerOpen by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            ChatMessage("1", "Xin chào! Mình là AI Stylist. Hôm nay bạn cần tư vấn gì không?", false),
            ChatMessage("2", "Mai mình đi đám cưới ngoài trời buổi tối, tư vấn giúp mình với!", true),
            ChatMessage("3", "Đám cưới ngoài trời thường thoáng đãng. Mình gợi ý bạn một chiếc váy midi lụa nhẹ nhàng, phối cùng sandal cao gót nhé.", false),
            ChatMessage("4", "Đây là một set đồ mẫu phù hợp với dáng người của bạn:", false, outfitSuggestionUrl = "https://i.postimg.cc/9MXZHYtp/3.jpg")
        )
    }

    val chatHistory = listOf(
        "Tư vấn đồ đi biển Phú Quốc",
        "Phối đồ mùa đông đi Đà Lạt",
        "Trang phục phỏng vấn công sở",
        "Váy dự tiệc sinh nhật"
    )

    var inputText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    actions = {
                        IconButton(onClick = { isDrawerOpen = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDarkBlue)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
                )
            },
            bottomBar = {
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

        // --- GIAO DIỆN RIGHT DRAWER MENU ---

        if (isDrawerOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { isDrawerOpen = false }
            )
        }

        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp),
                color = SecWhite,
                shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // Header của Menu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { isDrawerOpen = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AccentBlue),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Chat", tint = AccentBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cuộc trò chuyện mới",
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "HIỆN TẠI",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLightBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Box highlight cho chat hiện tại
                    Surface(
                        color = AccentBlue.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Tư vấn trang phục đám cưới ngoài trời",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextDarkBlue,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = TextLightBlue.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "LỊCH SỬ GẦN ĐÂY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLightBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Lịch sử các cuộc trò chuyện
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(chatHistory) { historyTitle ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { isDrawerOpen = false }
                                    .padding(vertical = 14.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = TextLightBlue.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = historyTitle,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextDarkBlue.copy(alpha = 0.8f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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