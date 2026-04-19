package com.example.smartfashion.ui.screens.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.smartfashion.BuildConfig
import com.example.smartfashion.data.api.AiLogSaveRequest
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.model.AiSession
import com.example.smartfashion.data.repository.AiRepository
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.model.Clothing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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
    private val outfitRepository: OutfitRepository,
    private val apiService: ApiService
) : ViewModel() {

    // Danh sách tin nhắn trên màn hình chính
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // Danh sách lịch sử hiển thị ở Drawer Menu
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
                        loadedMessages.add(ChatMessage(id = "u_${log.aiLogId}", text = log.inputPrompt, isUser = true))

                        log.geminiRawResponse?.let { aiText ->
                            try {
                                val json = JSONObject(aiText)
                                val textReply = json.getString("chat_reply")

                                var suggestionData: OutfitSuggestion? = null
                                if (json.has("suggested_outfit")) {
                                    val outfitObj = json.getJSONObject("suggested_outfit")
                                    val outfitName = outfitObj.getString("outfit_name")
                                    val description = outfitObj.getString("description")
                                    val clothingIdsArray = outfitObj.getJSONArray("clothing_ids")

                                    // Bóc tách mảng tags từ lịch sử cũ nếu có
                                    val tagsList = mutableListOf<String>()
                                    if (outfitObj.has("tags")) {
                                        val tagsArray = outfitObj.getJSONArray("tags")
                                        for (j in 0 until tagsArray.length()) {
                                            tagsList.add(tagsArray.getString(j))
                                        }
                                    }

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
                                    // Truyền tagsList vào OutfitSuggestion
                                    suggestionData = OutfitSuggestion(outfitName, description, ids, imageUrls, tagsList)
                                }

                                loadedMessages.add(
                                    ChatMessage(
                                        id = "ai_${log.aiLogId}",
                                        text = textReply,
                                        isUser = false,
                                        suggestion = suggestionData
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

    // HÀM BÓC TÁCH TỦ ĐỒ THÀNH CHUỖI JSON CHO GEMINI ĐỌC
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
                    1. Phân tích yêu cầu, thời tiết, sự kiện từ câu hỏi của người dùng để quyết định cách trả lời:
                       - Nếu là lời chào: Chào lại thân thiện và hỏi người dùng có cần tư vấn trang phục không. (KHÔNG tạo suggested_outfit).
                       - Nếu hỏi về kiến thức thời trang hoặc ứng dụng: Trả lời nhiệt tình, đúng trọng tâm. (KHÔNG tạo suggested_outfit trừ khi họ nhờ phối đồ).
                       - Nếu hỏi kiến thức ngoài lề (toán, lịch sử, code...): Khéo léo từ chối, nhắc nhẹ nhàng rằng bạn là AI Stylist chuyên về thời trang, và hướng họ hỏi về quần áo. (KHÔNG tạo suggested_outfit).
                       - Nếu yêu cầu phối đồ/gợi ý outfit: Lọc và chọn ra các món đồ phù hợp nhất từ danh sách tủ đồ trên để phối thành một bộ trang phục (Outfit).
                       
                    2. Tuyệt đối không tự bịa ra món đồ không có trong danh sách trên.
                    
                    3. BẮT BUỘC TRẢ VỀ kết quả theo định dạng JSON chính xác như sau (không thêm dấu markdown ```json, không thêm bất kỳ văn bản ngoài JSON này):
                    {
                        "chat_reply": "Câu trả lời tư vấn thân thiện, lời chào, hoặc lời từ chối khéo léo",
                        
                        // CHÚ Ý: CHỈ THÊM phần "suggested_outfit" này NẾU người dùng thực sự yêu cầu phối đồ. NẾU KHÔNG, HÃY BỎ QUA KEY NÀY.
                        "suggested_outfit": {
                            "outfit_name": "Tên bộ trang phục (VD: Đi biển năng động)",
                            "description": "Mô tả ngắn gọn về phong cách",
                            "clothing_ids": [id_1, id_2],
                            "tags": ["Tag 1", "Tag 2", "Tag 3"]
                        }
                    }
                    
                    4. Mảng "tags" (trong suggested_outfit) BẮT BUỘC chọn 3-5 tags phù hợp nhất từ danh sách cho phép sau đây (KHÔNG tự bịa tag mới):
                    - Mùa: Mùa Xuân, Mùa Hạ, Mùa Thu, Mùa Đông, Bốn Mùa
                    - Thời tiết: Nắng Nóng, Mát Mẻ, Lạnh, Mưa
                    - Dịp: Đi Làm, Đi Học, Đi Chơi, Tiệc Tùng, Thể Thao, Mặc Nhà
                    - Phong cách: Cơ Bản, Thanh Lịch, Năng Động, Nữ Tính, Cá Tính, Vintage
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

                if (jsonObject.has("suggested_outfit") && !jsonObject.isNull("suggested_outfit")) {
                    val outfitObj = jsonObject.getJSONObject("suggested_outfit")
                    val outfitName = outfitObj.getString("outfit_name")
                    val description = outfitObj.getString("description")
                    val clothingIdsArray = outfitObj.getJSONArray("clothing_ids")

                    // Đọc mảng tags do AI gợi ý
                    val tagsList = mutableListOf<String>()
                    if (outfitObj.has("tags")) {
                        val tagsArray = outfitObj.getJSONArray("tags")
                        for (j in 0 until tagsArray.length()) {
                            tagsList.add(tagsArray.getString(j))
                        }
                    }

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
                    suggestionData = OutfitSuggestion(outfitName, description, ids, imageUrls, tagsList)
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
                // In lỗi màu đỏ ra Logcat để biết chính xác nó chết ở đâu
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

    // Tự động tải ảnh, ghép lại thành 1 tấm Collage tự động co giãn theo số lượng
    fun saveSuggestedOutfit(
        userId: Int,
        suggestion: OutfitSuggestion,
        context: Context,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        // Chạy ngầm trong IO Thread để không làm đơ giao diện khi xử lý ảnh nặng
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Tải ảnh gợi ý về (Lấy tối đa 9 món)
                val imageLoader = ImageLoader(context)
                val bitmaps = mutableListOf<Bitmap>()
                val urlsToDownload = suggestion.imageUrls.take(9)

                for (url in urlsToDownload) {
                    val request = ImageRequest.Builder(context)
                        .data(url)
                        .allowHardware(false)
                        .build()
                    val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
                    (result as? BitmapDrawable)?.bitmap?.let { bitmaps.add(it) }
                }

                // 2. Tạo một ảnh Canvas màu trắng (Kích thước 800x800)
                val canvasSize = 800
                val collageBitmap = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(collageBitmap)
                canvas.drawColor(Color.WHITE)
                val paint = Paint(Paint.FILTER_BITMAP_FLAG)

                // 3. Tính toán và vẽ ảnh ghép tự co giãn
                val totalImages = bitmaps.size
                if (totalImages > 0) {
                    val cols = kotlin.math.ceil(kotlin.math.sqrt(totalImages.toDouble())).toInt()
                    val rows = kotlin.math.ceil(totalImages.toDouble() / cols).toInt()

                    val cellWidth = canvasSize / cols
                    val cellHeight = canvasSize / rows

                    for (i in 0 until totalImages) {
                        val col = i % cols
                        val row = i / cols

                        val left = col * cellWidth
                        val top = row * cellHeight
                        val right = left + cellWidth
                        val bottom = top + cellHeight

                        canvas.drawBitmap(bitmaps[i], null, Rect(left, top, right, bottom), paint)
                    }
                }

                // 4. Lưu Bitmap thành file tạm
                val file = File(context.cacheDir, "ai_collage_${System.currentTimeMillis()}.png")
                val outputStream = FileOutputStream(file)
                collageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // 5. Upload tấm ảnh ghép này lên Cloudinary
                val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val userIdPart = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val uploadResponse = apiService.uploadImage(imagePart, userIdPart)
                var finalImageUrl = ""
                if (uploadResponse.isSuccessful && uploadResponse.body()?.success == true) {
                    finalImageUrl = uploadResponse.body()?.data?.url_original ?: ""
                }

                if (file.exists()) file.delete()

                if (finalImageUrl.isEmpty()) {
                    finalImageUrl = suggestion.imageUrls.firstOrNull() ?: ""
                }

                // 6. Lưu Outfit xuống Database với link ảnh ghép
                val itemsToSave = suggestion.clothingIds.mapIndexed { index, id ->
                    OutfitItemRequest(
                        clothing_id = id, position_x = 0f, position_y = (index * 50).toFloat(), scale = 1f, rotation = 0f, z_index = index + 1
                    )
                }

                // Gói mảng tags từ AI gợi ý vào Request tạo Outfit
                val request = CreateOutfitRequest(
                    user_id = userId,
                    name = suggestion.name,
                    description = suggestion.description + "\n(✨ Gợi ý từ AI Stylist)",
                    image_preview_url = finalImageUrl,
                    items = itemsToSave,
                    tags = suggestion.tags // GỬI TAG XUỐNG API Ở ĐÂY
                )

                val response = outfitRepository.createOutfit(request)

                // Báo kết quả về UI
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val newOutfitId = response.body()?.data?.outfitId ?: 0
                        onSuccess(newOutfitId)
                    } else {
                        onError("Lỗi máy chủ, không thể lưu bộ đồ.")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Lỗi ghép ảnh: ${e.message}")
                }
            }
        }
    }
}