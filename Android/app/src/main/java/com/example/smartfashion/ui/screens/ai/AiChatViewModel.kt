package com.example.smartfashion.ui.screens.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.BuildConfig
import com.example.smartfashion.data.api.AiLogSaveRequest
import com.example.smartfashion.model.AiSession
import com.example.smartfashion.data.repository.AiRepository
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.model.Clothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import org.json.JSONObject

import com.example.smartfashion.data.api.CreateOutfitRequest
import com.example.smartfashion.data.api.OutfitItemRequest
import com.example.smartfashion.data.repository.OutfitRepository

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val aiRepository: AiRepository,
    private val clothingRepository: ClothingRepository,
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    // Danh sách tin nhắn trên màn hình chính
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Danh sách lịch sử hiển thị ở Drawer Menu (Đã đổi thành model AiSession chuẩn)
    private val _chatHistory = MutableStateFlow<List<AiSession>>(emptyList())
    val chatHistory: StateFlow<List<AiSession>> = _chatHistory.asStateFlow()

    // Biến lưu trữ tạm tủ đồ để map ID lấy ảnh
    private var currentCloset: List<Clothing> = emptyList()

    // Quản lý phiên chat hiện tại
    private var currentSessionId: String = UUID.randomUUID().toString()

    // Tải danh sách lịch sử ở Sidebar
    fun fetchRecentSessions(userId: Int) {
        viewModelScope.launch {
            try {
                val response = aiRepository.getAiSessions(userId)
                if (response.isSuccessful) {
                    _chatHistory.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Tải lại 1 cuộc trò chuyện cũ khi user bấm vào Sidebar
    fun loadSessionMessages(userId: Int, sessionId: String) {
        currentSessionId = sessionId
        viewModelScope.launch {
            try {
                if (currentCloset.isEmpty()) {
                    getUserClosetContext(userId)
                }

                val response = aiRepository.getSessionMessages(userId, sessionId)
                if (response.isSuccessful) {
                    val logs = response.body()?.data ?: emptyList()
                    val loadedMessages = mutableListOf<ChatMessage>()

                    logs.forEach { log ->
                        // Add tin nhắn của User
                        loadedMessages.add(ChatMessage(id = "u_${log.aiLogId}", text = log.inputPrompt, isUser = true))

                        // Add tin nhắn của AI
                        log.geminiRawResponse?.let { aiText ->
                            try {
                                val json = JSONObject(aiText)
                                val textReply = json.getString("chat_reply")

                                // Bóc tách lại Outfit Card để hiển thị cho lịch sử
                                var suggestionData: OutfitSuggestion? = null
                                if (json.has("suggested_outfit")) {
                                    val outfitObj = json.getJSONObject("suggested_outfit")
                                    val outfitName = outfitObj.getString("outfit_name")
                                    val description = outfitObj.getString("description")
                                    val clothingIdsArray = outfitObj.getJSONArray("clothing_ids")

                                    val ids = mutableListOf<Int>()
                                    val imageUrls = mutableListOf<String>()

                                    for (i in 0 until clothingIdsArray.length()) {
                                        val id = clothingIdsArray.getInt(i)
                                        ids.add(id)
                                        val matchedCloth = currentCloset.find { it.clothingId == id }
                                        if (matchedCloth?.imageUrl != null) {
                                            imageUrls.add(matchedCloth.imageUrl)
                                        }
                                    }
                                    suggestionData = OutfitSuggestion(outfitName, description, ids, imageUrls)
                                }

                                loadedMessages.add(
                                    ChatMessage(
                                        id = "ai_${log.aiLogId}",
                                        text = textReply,
                                        isUser = false,
                                        suggestion = suggestionData // Phục hồi lại Card gợi ý
                                    )
                                )
                            } catch (e: Exception) {
                                loadedMessages.add(
                                    ChatMessage(id = "ai_${log.aiLogId}", text = aiText, isUser = false)
                                )
                            }
                        }
                    }
                    _messages.value = loadedMessages
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Nút tạo cuộc trò chuyện mới
    fun startNewSession() {
        currentSessionId = UUID.randomUUID().toString()
        _messages.value = emptyList()
    }

    // HÀM BÓC TÁCH TỦ ĐỒ THÀNH CHUỖI JSON NHẸ GÀNG CHO GEMINI ĐỌC
    private suspend fun getUserClosetContext(userId: Int): String {
        return try {
            val response = clothingRepository.fetchClothesByUserId(
                userId = userId,
                categoryId = 0,
                page = 1,
                limit = 100,
                search = null
            )

            if (response.isSuccessful) {
                val clothes = response.body() ?: emptyList()
                currentCloset = clothes

                if (clothes.isEmpty()) return "Tủ đồ của người dùng hiện đang trống."

                val closetString = clothes.joinToString(separator = ",\n") { item ->
                    val tagsJson = item.tags?.joinToString(prefix = "[", postfix = "]", separator = ", ") { "\"$it\"" } ?: "[]"

                    """
                    {
                        "id": ${item.clothingId},
                        "name": "${item.name}",
                        "color": "${item.colorFamily ?: "Không phân loại"}",
                        "material": "${item.material ?: "Không phân loại"}",
                        "tags": $tagsJson
                    }
                    """.trimIndent()
                }
                "[\n$closetString\n]"
            } else {
                "Không thể truy cập dữ liệu tủ đồ lúc này."
            }
        } catch (e: Exception) {
            "Lỗi truy xuất tủ đồ: ${e.message}"
        }
    }

    // GỬI TIN NHẮN (CHUẨN BỊ PROMPT)
    fun sendMessage(userId: Int, prompt: String) {
        val userMsg = ChatMessage(id = UUID.randomUUID().toString(), text = prompt, isUser = true)
        _messages.value = _messages.value + userMsg

        viewModelScope.launch {
            try {
                val closetContext = getUserClosetContext(userId)
                val systemPrompt = """
                    Bạn là một AI Stylist thông minh của ứng dụng Smart Fashion. Người dùng đang yêu cầu: "$prompt".
                    
                    Dưới đây là danh sách quần áo hiện có trong tủ đồ của người dùng (định dạng JSON):
                    $closetContext

                    Nhiệm vụ của bạn:
                    1. Phân tích yêu cầu, thời tiết, sự kiện từ câu hỏi của người dùng.
                    2. Lọc và chọn ra các món đồ phù hợp nhất từ danh sách tủ đồ trên để phối thành một bộ trang phục (Outfit).
                    3. Tuyệt đối không tự bịa ra món đồ không có trong danh sách trên.
                    4. BẮT BUỘC TRẢ VỀ kết quả theo định dạng JSON chính xác như sau (không thêm dấu markdown ```json, không thêm bất kỳ văn bản ngoài JSON này):
                    {
                        "chat_reply": "Câu trả lời tư vấn thân thiện, giải thích lý do bạn chọn bộ đồ này",
                        "suggested_outfit": {
                            "outfit_name": "Tên bộ trang phục (VD: Đi biển năng động)",
                            "description": "Mô tả ngắn gọn về phong cách",
                            "clothing_ids": [id_1, id_2] 
                        }
                    }
                """.trimIndent()

                val config = generationConfig {
                    responseMimeType = "application/json"
                }

                val generativeModel = GenerativeModel(
                    modelName = "gemini-3-flash-preview",
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    generationConfig = config
                )

                val response = generativeModel.generateContent(systemPrompt)
                val responseText = response.text ?: "{}"

                // Dọn dẹp các ký tự markdown thừa (nếu AI trả về) trước khi đưa vào JSONObject
                val cleanJsonString = responseText.replace(Regex("```json|```"), "").trim()

                val jsonObject = JSONObject(cleanJsonString)
                val chatReply = jsonObject.getString("chat_reply")

                var suggestionData: OutfitSuggestion? = null

                if (jsonObject.has("suggested_outfit")) {
                    val outfitObj = jsonObject.getJSONObject("suggested_outfit")
                    val outfitName = outfitObj.getString("outfit_name")
                    val description = outfitObj.getString("description")
                    val clothingIdsArray = outfitObj.getJSONArray("clothing_ids")

                    val ids = mutableListOf<Int>()
                    val imageUrls = mutableListOf<String>()

                    for (i in 0 until clothingIdsArray.length()) {
                        val id = clothingIdsArray.getInt(i)
                        ids.add(id)
                        val matchedCloth = currentCloset.find { it.clothingId == id }
                        if (matchedCloth?.imageUrl != null) {
                            imageUrls.add(matchedCloth.imageUrl)
                        }
                    }
                    suggestionData = OutfitSuggestion(outfitName, description, ids, imageUrls)
                }

                val aiMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = chatReply,
                    isUser = false,
                    suggestion = suggestionData
                )
                _messages.value = _messages.value + aiMsg

                // LƯU LOG XUỐNG DATABASE NODE.JS
                val title = if (_messages.value.size <= 2) prompt.take(30) + "..." else null
                val logRequest = AiLogSaveRequest(
                    user_id = userId,
                    session_id = currentSessionId,
                    title = title,
                    input_prompt = prompt,
                    gemini_raw_response = cleanJsonString
                )
                aiRepository.saveAiLog(logRequest)

            } catch (e: Exception) {
                // In lỗi màu đỏ ra Logcat để mình biết chính xác nó chết ở đâu
                e.printStackTrace()
                android.util.Log.e("AI_CHAT_ERROR", "Lỗi thực thi AI: ${e.message}")

                // BỊ LỖI VẪN HIỂN THỊ LÊN UI
                val fallbackText = "Xin lỗi, hiện tại hệ thống AI đang quá tải hoặc gặp sự cố. Bạn thử lại sau nhé!"
                val errorMsg = ChatMessage(id = UUID.randomUUID().toString(), text = fallbackText, isUser = false)
                _messages.value = _messages.value + errorMsg

                // LƯU XUỐNG DATABASE ĐỂ KHÔNG BỊ MẤT LỊCH SỬ CỦA USER
                val title = if (_messages.value.size <= 2) prompt.take(30) + "..." else null

                val fakeErrorJson = JSONObject().apply {
                    put("chat_reply", fallbackText)
                }.toString()

                val logRequest = AiLogSaveRequest(
                    user_id = userId,
                    session_id = currentSessionId,
                    title = title,
                    input_prompt = prompt,
                    gemini_raw_response = fakeErrorJson
                )

                try {
                    aiRepository.saveAiLog(logRequest)
                } catch (dbError: Exception) {
                    dbError.printStackTrace()
                }
            }
        }
    }

    // HÀM LƯU BỘ ĐỒ GỢI Ý VÀO DATABASE
    fun saveSuggestedOutfit(
        userId: Int,
        suggestion: OutfitSuggestion,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val itemsToSave = suggestion.clothingIds.mapIndexed { index, id ->
                    OutfitItemRequest(
                        clothing_id = id,
                        position_x = 0f,
                        position_y = (index * 50).toFloat(),
                        scale = 1f,
                        rotation = 0f,
                        z_index = index + 1
                    )
                }

                val request = CreateOutfitRequest(
                    user_id = userId,
                    name = suggestion.name,
                    description = suggestion.description + "\n(✨ Gợi ý từ AI Stylist)",
                    image_preview_url = suggestion.imageUrls.firstOrNull() ?: "",
                    items = itemsToSave
                )

                val response = outfitRepository.createOutfit(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    val newOutfitId = response.body()?.data?.outfitId ?: 0
                    onSuccess(newOutfitId)
                } else {
                    onError("Lỗi máy chủ, không thể lưu bộ đồ.")
                }
            } catch (e: Exception) {
                onError("Lỗi kết nối: ${e.message}")
            }
        }
    }
}