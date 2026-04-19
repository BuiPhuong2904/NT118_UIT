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
    private val clothingRepository: ClothingRepository
) : ViewModel() {

    // State lưu bộ phối đồ gợi ý hôm nay
    private val _recommendedOutfit = MutableStateFlow<Outfit?>(null)
    val recommendedOutfit: StateFlow<Outfit?> = _recommendedOutfit.asStateFlow()

    // State lưu danh sách xu hướng
    private val _trendingItems = MutableStateFlow<List<SystemClothing>>(emptyList())
    val trendingItems: StateFlow<List<SystemClothing>> = _trendingItems.asStateFlow()

    // lưu thời tiết hiện tại
    private val _currentWeather = MutableStateFlow<WeatherCache?>(null)
    val currentWeather: StateFlow<WeatherCache?> = _currentWeather.asStateFlow()

    fun loadHomeData(userId: Int, lat: Double = 10.8231, lon: Double = 106.6297) {
        viewModelScope.launch {
            try {
                // 1. Lấy danh sách "Xu hướng"
                val trendRes = apiService.getSystemClothesPaginated(page = 1, limit = 5, tags = null, categoryId = null, search = null)
                if (trendRes.isSuccessful) {
                    _trendingItems.value = trendRes.body() ?: emptyList()
                }

                // 2. Lấy dữ liệu thời tiết và gọi AI
                val weatherRes = weatherRepository.getCurrentWeather(lat, lon)
                var isAiGenerated = false

                if (weatherRes.isSuccessful && weatherRes.body()?.success == true) {
                    val weatherData = weatherRes.body()?.data
                    _currentWeather.value = weatherData

                    if (weatherData != null) {
                        // Trả về true nếu AI ghép thành công, false nếu tủ đồ trống/AI lỗi
                        isAiGenerated = generateOutfitForWeather(userId, weatherData)
                    }
                }

                // 3. Nếu AI thất bại (do mạng lỗi, hoặc user chưa có quần áo nào)
                // -> Quay về lấy ngẫu nhiên 1 bộ đã lưu như ý bạn
                if (!isAiGenerated) {
                    val outfitRes = outfitRepository.getOutfitsByUser(userId)
                    if (outfitRes.isSuccessful && outfitRes.body()?.success == true) {
                        val outfits = outfitRes.body()?.data ?: emptyList()
                        if (outfits.isNotEmpty()) {
                            _recommendedOutfit.value = outfits.random() // Lấy ngẫu nhiên
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Lỗi tải dữ liệu Home: ${e.message}")
            }
        }
    }

    // --- CÁC HÀM XỬ LÝ AI & THỜI TIẾT ---

    private suspend fun generateOutfitForWeather(userId: Int, weather: WeatherCache): Boolean {
        try {
            // Lấy danh sách quần áo của User để gửi cho AI
            val clothesRes = clothingRepository.fetchClothesByUserId(userId, 0, 1, 100, null)
            val clothes = clothesRes.body() ?: emptyList()

            if (clothes.isEmpty()) {
                Log.d("HomeViewModel", "Tủ đồ trống, chuyển về logic Random cũ.")
                return false // Trả về false để kích hoạt logic fallback lấy random ở trên
            }

            // Ép tủ đồ thành chuỗi JSON
            val closetString = clothes.joinToString(separator = ",\n") { item ->
                """{"id": ${item.clothingId}, "name": "${item.name}", "material": "${item.material ?: ""}"}"""
            }

            val systemPrompt = """
                Bạn là AI Stylist. Thời tiết hiện tại ở ${weather.locationName} là ${weather.temp} độ C, tình trạng: ${weather.condition}.
                Dựa vào tủ đồ sau: [$closetString].
                Hãy chọn ra 1 bộ đồ phối hợp lý nhất cho thời tiết này.
                BẮT BUỘC trả về JSON theo định dạng sau (không giải thích thêm):
                {
                    "outfit_name": "Tên bộ đồ (ngắn gọn)",
                    "clothing_ids": [id1, id2]
                }
            """.trimIndent()

            val config = generationConfig { responseMimeType = "application/json" }
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
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
            for (i in 0 until clothingIdsArray.length()) {
                val id = clothingIdsArray.getInt(i)
                val matchedCloth = clothes.find { it.clothingId == id }
                if (matchedCloth?.imageUrl != null) {
                    imageUrlsToStitch.add(matchedCloth.imageUrl)
                }
            }

            // Gọi hàm ghép ảnh dưới dạng Local File
            val localCollagePath = createLocalCollage(imageUrlsToStitch)

            // Đẩy ra UI (Dùng ID -1 để đánh dấu đây là bộ AI tạo)
            val tempOutfit = Outfit(
                outfitId = -1,
                userId = userId,
                name = outfitName,
                description = "Gợi ý dựa theo thời tiết: ${weather.temp}°C",
                imagePreviewUrl = localCollagePath,
                isAiSuggested = true
            )

            _recommendedOutfit.value = tempOutfit
            return true // Thành công

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