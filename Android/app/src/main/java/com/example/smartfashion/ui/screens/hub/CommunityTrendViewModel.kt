package com.example.smartfashion.ui.screens.hub

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.CommunityRepository
import com.example.smartfashion.data.repository.OutfitRepository
import com.example.smartfashion.data.repository.TagRepository
import com.example.smartfashion.model.CommunityPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityTrendViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val tagRepository: TagRepository,
    private val outfitRepository: OutfitRepository
) : ViewModel() {

    private val _postsList = MutableStateFlow<List<CommunityPost>>(emptyList())
    val postsList: StateFlow<List<CommunityPost>> = _postsList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Chế độ xem hiện tại ("Dành cho bạn", "Đang hot", "Mới nhất")
    private val _selectedMode = MutableStateFlow("Dành cho bạn")
    val selectedMode: StateFlow<String> = _selectedMode.asStateFlow()

    // --- LOGIC TAGS ---
    private val _filterGroups = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val filterGroups: StateFlow<Map<String, List<String>>> = _filterGroups.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val selectedFilters: StateFlow<Map<String, List<String>>> = _selectedFilters.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10
    private var isLastPage = false
    private var isFetching = false

    init {
        fetchTagsData()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // 1. CHỌN CHẾ ĐỘ XEM
    fun onModeSelected(modeName: String) {
        if (_selectedMode.value == modeName) return
        _selectedMode.value = modeName
        fetchPosts(isRefresh = true)
    }

    // 2. CHỌN/BỎ CHỌN TAG (Từ các dropdown Mùa, Dịp, Phong cách)
    fun updateTagFilter(groupName: String, option: String) {
        val currentFilters = _selectedFilters.value.toMutableMap()
        val currentOptionsInGroup = currentFilters[groupName]?.toMutableList() ?: mutableListOf()

        if (currentOptionsInGroup.contains(option)) {
            currentOptionsInGroup.remove(option)
        } else {
            currentOptionsInGroup.add(option)
        }

        if (currentOptionsInGroup.isEmpty()) {
            currentFilters.remove(groupName)
        } else {
            currentFilters[groupName] = currentOptionsInGroup
        }

        _selectedFilters.value = currentFilters
        fetchPosts(isRefresh = true)
    }

    // 3. XÓA TẤT CẢ FILTER TAGS
    fun clearAllTagFilters() {
        if (_selectedFilters.value.isEmpty()) return
        _selectedFilters.value = emptyMap()
        fetchPosts(isRefresh = true)
    }

    // Load dữ liệu bảng Tag từ DB lên
    private fun fetchTagsData() {
        viewModelScope.launch {
            try {
                val tagRes = tagRepository.getTags()
                if (tagRes.isSuccessful) {
                    val tags = tagRes.body() ?: emptyList()
                    val groupedMap = linkedMapOf<String, MutableList<String>>(
                        "Mùa" to mutableListOf(),
                        "Thời tiết" to mutableListOf(),
                        "Dịp" to mutableListOf(),
                        "Phong cách" to mutableListOf()
                    )
                    tags.forEach { tag ->
                        val groupName = when (tag.tagGroup) {
                            "Season" -> "Mùa"
                            "Weather" -> "Thời tiết"
                            "Occasion" -> "Dịp"
                            "Style" -> "Phong cách"
                            else -> tag.tagGroup
                        }
                        if (groupedMap.containsKey(groupName)) {
                            groupedMap[groupName]?.add(tag.tagName)
                        } else {
                            groupedMap[groupName] = mutableListOf(tag.tagName)
                        }
                    }
                    _filterGroups.value = groupedMap
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi tải Tags ở Community: ${e.message}")
            }
        }
    }

    // Lấy danh sách bài đăng từ Backend
    fun fetchPosts(isRefresh: Boolean = false) {
        if (isFetching) return

        if (isRefresh) {
            currentPage = 1
            isLastPage = false
        }

        if (isLastPage) return

        viewModelScope.launch {
            isFetching = true
            if (isRefresh) _isLoading.value = true

            try {
                val selectedTagsList = _selectedFilters.value.values.flatten()
                val tagsString = if (selectedTagsList.isNotEmpty()) selectedTagsList.joinToString(",") else null

                val response = communityRepository.getCommunityPosts(
                    page = currentPage,
                    limit = pageSize,
                    tag = tagsString,
                    mode = _selectedMode.value
                )

                if (response.isSuccessful) {
                    response.body()?.data?.let { newList ->
                        if (newList.size < pageSize) {
                            isLastPage = true
                        }

                        if (isRefresh) {
                            _postsList.value = newList
                        } else {
                            val currentList = _postsList.value.toMutableList()
                            currentList.addAll(newList)
                            _postsList.value = currentList
                        }
                        currentPage++
                    }
                } else {
                    _errorMessage.value = "Hệ thống đang bận. Không thể tải bài đăng."
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi ở CommunityTrendViewModel: ", e)
                _errorMessage.value = "Mất kết nối mạng. Vui lòng thử lại."
            } finally {
                isFetching = false
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        fetchPosts(isRefresh = false)
    }

    // Xử lý Thả tim / Hủy thả tim
    fun toggleLikeStatus(post: CommunityPost, userId: Int) {
        viewModelScope.launch {
            try {
                val postId = post.postId ?: return@launch

                val newLikeStatus = !post.isLiked
                val newLikesCount = if (newLikeStatus) post.likesCount + 1 else maxOf(0, post.likesCount - 1)

                updatePostInList(post.copy(isLiked = newLikeStatus, likesCount = newLikesCount))

                val response = communityRepository.toggleLikePost(postId = postId, userId = userId)

                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        val finalPost = post.copy(
                            isLiked = apiResponse.is_liked,
                            likesCount = apiResponse.likes_count
                        )
                        updatePostInList(finalPost)
                    }
                } else {
                    updatePostInList(post)
                    _errorMessage.value = "Không thể thả tim lúc này."
                }
            } catch (e: Exception) {
                updatePostInList(post)
                _errorMessage.value = "Mất mạng. Không thể thả tim."
            }
        }
    }

    private fun updatePostInList(updatedPost: CommunityPost) {
        val currentList = _postsList.value.toMutableList()
        val index = currentList.indexOfFirst { it.postId == updatedPost.postId }
        if (index != -1) {
            currentList[index] = updatedPost
            _postsList.value = currentList
        }
    }

    fun createPost(
        userId: Int,
        outfitId: Int,
        imageUrl: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val outfitDetailResponse = outfitRepository.getOutfitById(outfitId)
                val extractedTags = if (outfitDetailResponse.isSuccessful) {
                    outfitDetailResponse.body()?.data?.tagNames
                } else null

                // Gắn Tag vừa lấy được vào request và Đăng bài lên Cộng đồng
                val randomHeightRatio = (10..15).random() / 10f

                val request = com.example.smartfashion.data.api.CreatePostRequest(
                    user_id = userId,
                    outfit_id = outfitId,
                    image_url = imageUrl,
                    description = description,
                    height_ratio = randomHeightRatio,
                    tags = extractedTags
                )

                val response = communityRepository.createCommunityPost(request)
                if (response.isSuccessful) {
                    fetchPosts(isRefresh = true)
                    onSuccess()
                } else {
                    _errorMessage.value = "Hệ thống bận, không thể đăng bài lúc này."
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi createPost: ", e)
                _errorMessage.value = "Mất kết nối mạng. Vui lòng thử lại."
            } finally {
                _isLoading.value = false
            }
        }
    }
}