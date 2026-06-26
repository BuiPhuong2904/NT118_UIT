package com.example.smartfashion.ui.screens.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.smartfashion.BuildConfig
import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.repository.ClothingRepository
import com.example.smartfashion.data.repository.OutfitRepository
import com.example.smartfashion.data.repository.WeatherRepository
import com.example.smartfashion.model.Outfit
import com.example.smartfashion.model.SystemClothing
import com.example.smartfashion.model.WeatherCache
import com.example.smartfashion.data.repository.CommunityRepository
import com.example.smartfashion.model.Clothing
import com.example.smartfashion.model.CommunityPost
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val outfitRepository: OutfitRepository,
    private val apiService: ApiService,
    private val weatherRepository: WeatherRepository,
    private val clothingRepository: ClothingRepository,
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _recommendedOutfit = MutableStateFlow<Outfit?>(null)
    val recommendedOutfit: StateFlow<Outfit?> = _recommendedOutfit.asStateFlow()

    private val _trendingItems = MutableStateFlow<List<CommunityPost>>(emptyList())
    val trendingItems: StateFlow<List<CommunityPost>> = _trendingItems.asStateFlow()

    private val _currentWeather = MutableStateFlow<WeatherCache?>(null)
    val currentWeather: StateFlow<WeatherCache?> = _currentWeather.asStateFlow()

    fun loadHomeData(userId: Int, lat: Double = 10.8231, lon: Double = 106.6297) {
        viewModelScope.launch {
            try {
                val trendRes = communityRepository.getCommunityPosts(1, 7, "Đang hot")
                if (trendRes.isSuccessful) {
                    _trendingItems.value = trendRes.body()?.data ?: emptyList()
                }

                val weatherRes = weatherRepository.getCurrentWeather(lat, lon)
                var isAiGenerated = false

                if (weatherRes.isSuccessful && weatherRes.body()?.success == true) {
                    val weatherData = weatherRes.body()?.data
                    _currentWeather.value = weatherData

                    if (weatherData != null) {
                        isAiGenerated = generateOutfitForWeather(userId, weatherData)
                    }
                }

                if (!isAiGenerated) {
                    val outfitRes = outfitRepository.getOutfitsByUser(userId)
                    if (outfitRes.isSuccessful && outfitRes.body()?.success == true) {
                        val outfits = outfitRes.body()?.data ?: emptyList()
                        if (outfits.isNotEmpty()) {
                            _recommendedOutfit.value = outfits.random()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Lỗi tải dữ liệu Home: ${e.message}")
            }
        }
    }

    private suspend fun generateOutfitForWeather(userId: Int, weather: WeatherCache): Boolean {
        try {
            // 1. LẤY THÔNG TIN NGƯỜI DÙNG
            var userGender = "Khác"
            try {
                val profileRes = apiService.getMyProfile()
                if (profileRes.isSuccessful) {
                    userGender = profileRes.body()?.data?.gender ?: "Khác"
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Lỗi lấy Profile (Giới tính): ${e.message}")
            }

            // 2. Lấy đồ cá nhân
            val clothesRes = clothingRepository.fetchClothesByUserId(userId, 0, 1, 100, null)
            val personalClothes = clothesRes.body() ?: emptyList()

            // 3. Lấy đồ từ Kho mẫu Hệ thống
            val sysRes = apiService.getSystemClothesPaginated(page = 1, limit = 139, tags = null, categoryId = null, search = null)
            val systemClothes = if (sysRes.isSuccessful) sysRes.body() ?: emptyList() else emptyList()

            if (personalClothes.isEmpty() && systemClothes.isEmpty()) {
                return false
            }

            // 4. Ép kiểu 2 danh sách thành chuỗi JSON và gắn nhãn (P_ cho cá nhân, W_ cho hệ thống)
            val personalString = personalClothes.joinToString(separator = ",\n") { item ->
                """{"id": "P_${item.clothingId}", "name": "${item.name}", "source": "Tủ đồ cá nhân"}"""
            }

            val systemString = systemClothes.joinToString(separator = ",\n") { item ->
                """{"id": "W_${item.templateId}", "name": "${item.name}", "source": "Kho mẫu hệ thống"}"""
            }

            val combinedCloset = listOf(personalString, systemString).filter { it.isNotBlank() }.joinToString(",\n")

            val systemPrompt = """
                Bạn là AI Stylist thời trang. 
                - Thời tiết hiện tại ở ${weather.locationName} là ${weather.temp} độ C, tình trạng: ${weather.condition}.
                - Giới tính của khách hàng: $userGender. (QUY TẮC QUAN TRỌNG: Nếu khách hàng là Nam hoặc Nữ, BẮT BUỘC chọn các món đồ có kiểu dáng phù hợp với giới tính này. Nếu giới tính là "Khác" hoặc không xác định, hãy phối phong cách unisex, phi giới tính).
                
                Dựa vào danh sách quần áo (gồm đồ cá nhân và kho mẫu) sau: 
                [$combinedCloset]
                
                Hãy chọn ra 1 bộ đồ phối hợp lý nhất cho thời tiết và giới tính trên. Bạn nên ưu tiên kết hợp đồ cá nhân với đồ kho mẫu để tạo ra phong cách mới lạ.
                BẮT BUỘC trả về JSON theo định dạng sau (không giải thích thêm):
                {
                    "outfit_name": "Tên bộ đồ (ngắn gọn, VD: Năng động ngày nắng)",
                    "clothing_ids": ["P_1", "W_3"] 
                }
                Lưu ý: clothing_ids là mảng các chuỗi ID y hệt như đầu vào.
            """.trimIndent()

            val config = generationConfig { responseMimeType = "application/json" }
            val generativeModel = GenerativeModel(
                modelName = "gemini-3-flash-preview",
                apiKey = BuildConfig.GEMINI_API_KEY,
                generationConfig = config
            )

            val response = generativeModel.generateContent(systemPrompt)
            val responseText = response.text

            if (responseText.isNullOrEmpty()) return false

            val cleanJson = responseText.replace("```json", "").replace("```", "").trim()
            val jsonObject = JSONObject(cleanJson)

            val outfitName = jsonObject.getString("outfit_name")
            val clothingIdsArray = jsonObject.getJSONArray("clothing_ids")

            val imageUrlsToStitch = mutableListOf<String>()
            val matchedClothes = mutableListOf<Clothing>()

            for (i in 0 until clothingIdsArray.length()) {
                val stringId = clothingIdsArray.getString(i)

                if (stringId.startsWith("P_")) {
                    val realId = stringId.removePrefix("P_").toIntOrNull()
                    val matchedCloth = personalClothes.find { it.clothingId == realId }
                    if (matchedCloth != null) {
                        matchedClothes.add(matchedCloth)
                        if (matchedCloth.imageUrl != null) {
                            imageUrlsToStitch.add(matchedCloth.imageUrl)
                        }
                    }
                } else if (stringId.startsWith("W_")) {
                    val realId = stringId.removePrefix("W_").toIntOrNull()
                    val matchedSys = systemClothes.find { it.templateId == realId }
                    if (matchedSys != null) {
                        val convertedSys = Clothing(
                            clothingId = matchedSys.templateId,
                            userId = userId,
                            imageId = 0,
                            categoryId = matchedSys.categoryId ?: 0,
                            name = matchedSys.name ?: "Mẫu hệ thống",
                            imageUrl = matchedSys.imageUrl,
                            itemType = "system"
                        )
                        matchedClothes.add(convertedSys)
                        if (matchedSys.imageUrl != null) {
                            imageUrlsToStitch.add(matchedSys.imageUrl)
                        }
                    }
                }
            }

            // Gọi hàm ghép ảnh dưới dạng Local File
            val localCollagePath = createLocalCollage(imageUrlsToStitch)

            val tempOutfit = Outfit(
                outfitId = -1,
                userId = userId,
                name = outfitName,
                description = "Gợi ý thông minh: Kết hợp Tủ đồ & Kho mẫu",
                imagePreviewUrl = localCollagePath,
                isAiSuggested = true,
                clothes = matchedClothes
            )

            _recommendedOutfit.value = tempOutfit
            return true

        } catch (e: Exception) {
            Log.e("HomeViewModel", "Lỗi AI gợi ý thời tiết: ${e.message}")
            return false
        }
    }

    private suspend fun createLocalCollage(imageUrls: List<String>): String = withContext(Dispatchers.IO) {
        try {
            if (imageUrls.isEmpty()) return@withContext ""

            val imageLoader = ImageLoader(context)
            val bitmaps = mutableListOf<Bitmap>()

            for (url in imageUrls.take(4)) {
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build()
                val result = (imageLoader.execute(request) as? SuccessResult)?.drawable
                (result as? BitmapDrawable)?.bitmap?.let { bitmaps.add(it) }
            }

            if (bitmaps.isEmpty()) return@withContext ""

            val canvasSize = 800
            val collageBitmap = Bitmap.createBitmap(canvasSize, canvasSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(collageBitmap)
            canvas.drawColor(Color.WHITE)
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)

            val totalImages = bitmaps.size
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

                val padding = 20
                val rect = Rect(left + padding, top + padding, right - padding, bottom - padding)
                canvas.drawBitmap(bitmaps[i], null, rect, paint)
            }

            val file = File(context.cacheDir, "weather_ootd_temp.png")
            val outputStream = FileOutputStream(file)
            collageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return@withContext file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext ""
        }
    }
}