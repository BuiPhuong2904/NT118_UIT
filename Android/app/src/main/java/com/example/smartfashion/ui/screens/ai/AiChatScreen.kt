package com.example.smartfashion.ui.screens.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.smartfashion.data.local.TokenManager
import androidx.navigation.NavController

import com.example.smartfashion.ui.theme.AccentBlue
import com.example.smartfashion.ui.theme.BgLight
import com.example.smartfashion.ui.theme.GradientPrimaryButton
import com.example.smartfashion.ui.theme.GradientText
import com.example.smartfashion.ui.theme.SecWhite
import com.example.smartfashion.ui.theme.TextDarkBlue
import com.example.smartfashion.ui.theme.TextLightBlue
import kotlinx.coroutines.launch

data class OutfitSuggestion(
    val name: String,
    val description: String,
    val clothingIds: List<Int>,
    val imageUrls: List<String>,
    val tags: List<String> = emptyList()
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val suggestion: OutfitSuggestion? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    navController: NavController,
    onBackClick: () -> Unit = {},
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val currentUserId = tokenManager.getUserId()

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isDrawerOpen by remember { mutableStateOf(false) }

    val messages by viewModel.messages.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()

    var inputText by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    LaunchedEffect(currentUserId) {
        if (currentUserId != -1) {
            viewModel.fetchRecentSessions(currentUserId)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.imePadding(),
            containerColor = BgLight,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                Column(
                    modifier = Modifier.background(BgLight)
                ) {
                    // --- PHẦN GỢI Ý ---
                    val suggestions = listOf(
                        "Gợi ý cho tôi một bộ đồ để mặc hôm nay!",
                        "Phối giúp tôi một outfit đi chơi cuối tuần.",
                        "Tìm cho tôi một bộ đồ lịch sự nhưng vẫn thoải mái."
                    )

                    // Chỉ hiển thị gợi ý khi thanh chat đang trống để đỡ rối mắt
                    AnimatedVisibility(visible = inputText.isEmpty()) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(suggestions) { sug ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            // Khi ấn vào thì gửi tin nhắn luôn
                                            if (currentUserId != -1) {
                                                viewModel.sendMessage(userId = currentUserId, prompt = sug)
                                            }
                                        }
                                ) {
                                    Text(
                                        text = sug,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        color = AccentBlue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Thanh nhập tin nhắn
                    ChatInputBar(
                        text = inputText,
                        onTextChange = { inputText = it },
                        onSend = {
                            if (inputText.isNotBlank() && currentUserId != -1) {
                                viewModel.sendMessage(userId = currentUserId, prompt = inputText)
                                inputText = ""
                            }
                        }
                    )
                }
            }
        ) { paddingValues ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                reverseLayout = false,
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        MessageBubble(
                            message = ChatMessage(
                                id = "welcome",
                                text = "Xin chào! Mình là AI Stylist. Hãy mô tả dịp bạn sắp tham gia, mình sẽ lục tủ đồ và chọn ra bộ cánh đẹp nhất cho bạn nhé!",
                                isUser = false
                            ),
                            onSaveClick = {}
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                items(messages) { msg ->
                    MessageBubble(
                        message = msg,
                        onSaveClick = { suggestionData ->

                            // Báo cho người dùng biết là đang tiến hành xử lý ảnh
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Đang ghép ảnh và lưu...",
                                    duration = SnackbarDuration.Short
                                )
                            }

                            viewModel.saveSuggestedOutfit(
                                userId = currentUserId,
                                suggestion = suggestionData,
                                context = context, // FIX: Truyền Context vào đây
                                onSuccess = { newOutfitId ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Đã lưu vào Tủ Đồ!")
                                    }
                                    navController.navigate("outfit_detail_screen/$newOutfitId")
                                },
                                onError = { errorMsg ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(errorMsg)
                                    }
                                }
                            )
                        }
                    )
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isDrawerOpen = false }
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
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            isDrawerOpen = false
                            viewModel.startNewSession()
                        },
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
                        text = "LỊCH SỬ GẦN ĐÂY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextLightBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (chatHistory.isEmpty()) {
                        Text(
                            text = "Chưa có lịch sử trò chuyện nào.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextLightBlue.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 20.dp)
                        ) {
                            items(chatHistory) { session ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            isDrawerOpen = false
                                            viewModel.loadSessionMessages(currentUserId, session.sessionId)
                                        }
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
                                        text = session.title ?: "Trò chuyện mới",
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
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onSaveClick: (OutfitSuggestion) -> Unit
) {
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
        if (!message.isUser && message.id != "welcome") {
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
                .widthIn(max = 300.dp)
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

                val suggestion = message.suggestion

                if (suggestion != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BgLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TextLightBlue.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = suggestion.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextDarkBlue,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = suggestion.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextLightBlue,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(suggestion.imageUrls) { imgUrl ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        modifier = Modifier.size(64.dp)
                                    ) {
                                        AsyncImage(
                                            model = imgUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.padding(4.dp).fillMaxSize()
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onSaveClick(suggestion) },
                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text("Lưu bộ này", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
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