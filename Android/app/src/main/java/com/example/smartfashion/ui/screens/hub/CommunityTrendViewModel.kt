package com.example.smartfashion.ui.screens.hub

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.CommunityRepository
import com.example.smartfashion.data.repository.OutfitRepository
import com.example.smartfashion.data.repository.TagRepository
import com.example.smartfashion.model.CommunityPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    private val _selectedMode = MutableStateFlow("Dành cho bạn")
    val selectedMode: StateFlow<String> = _selectedMode.asStateFlow()

    private val _filterGroups = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val filterGroups: StateFlow<Map<String, List<String>>> = _filterGroups.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val selectedFilters: StateFlow<Map<String, List<String>>> = _selectedFilters.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    private var searchJob: Job? = null

    private var currentPage = 1
    private val pageSize = 10
    private var isLastPage = false
    private var isFetching = false

    init {
        fetchTagsData()
    }

    fun clearErrorMessage() { _errorMessage.value = null }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            fetchPosts(isRefresh = true)
        }
    }

    fun onModeSelected(modeName: String) {
        if (_selectedMode.value == modeName) return
        _selectedMode.value = modeName
        fetchPosts(isRefresh = true)
    }

    fun updateTagFilter(groupName: String, option: String) {
        val currentFilters = _selectedFilters.value.toMutableMap()
        val currentOptionsInGroup = currentFilters[groupName]?.toMutableList() ?: mutableListOf()

        if (currentOptionsInGroup.contains(option)) currentOptionsInGroup.remove(option)
        else currentOptionsInGroup.add(option)

        if (currentOptionsInGroup.isEmpty()) currentFilters.remove(groupName)
        else currentFilters[groupName] = currentOptionsInGroup

        _selectedFilters.value = currentFilters
        fetchPosts(isRefresh = true)
    }

    fun clearAllTagFilters() {
        if (_selectedFilters.value.isEmpty()) return
        _selectedFilters.value = emptyMap()
        fetchPosts(isRefresh = true)
    }

    private fun fetchTagsData() {
        viewModelScope.launch {
            try {
                val tagRes = tagRepository.getTags()
                if (tagRes.isSuccessful) {
                    val tags = tagRes.body() ?: emptyList()
                    val groupedMap = linkedMapOf<String, MutableList<String>>(
                        "Mùa" to mutableListOf(), "Thời tiết" to mutableListOf(),
                        "Dịp" to mutableListOf(), "Phong cách" to mutableListOf()
                    )
                    tags.forEach { tag ->
                        val groupName = when (tag.tagGroup) {
                            "Season" -> "Mùa"
                            "Weather" -> "Thời tiết"
                            "Occasion" -> "Dịp"
                            "Style" -> "Phong cách"
                            else -> tag.tagGroup
                        }
                        groupedMap[groupName]?.add(tag.tagName)
                    }
                    _filterGroups.value = groupedMap
                }
            } catch (e: Exception) { Log.e("API_ERROR", "Lỗi tải Tags", e) }
        }
    }

    fun fetchPosts(isRefresh: Boolean = false) {
        if (isFetching) return
        if (isRefresh) { currentPage = 1; isLastPage = false }
        if (isLastPage) return

        viewModelScope.launch {
            isFetching = true
            if (isRefresh) _isLoading.value = true

            try {
                val selectedTagsList = _selectedFilters.value.values.flatten()
                val tagsString = if (selectedTagsList.isNotEmpty()) selectedTagsList.joinToString(",") else null

                val currentSearch = _searchQuery.value.takeIf { it.isNotBlank() }

                val response = communityRepository.getCommunityPosts(
                    page = currentPage,
                    limit = pageSize,
                    tag = tagsString,
                    mode = _selectedMode.value,
                    search = currentSearch
                )

                if (response.isSuccessful) {
                    response.body()?.data?.let { newList ->
                        if (newList.size < pageSize) isLastPage = true

                        if (isRefresh) _postsList.value = newList
                        else _postsList.value = _postsList.value + newList
                        currentPage++
                    }
                } else _errorMessage.value = "Hệ thống bận."
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi getCommunityPosts", e)
                _errorMessage.value = "Mất kết nối mạng."
            } finally {
                isFetching = false
                _isLoading.value = false
            }
        }
    }

    fun loadMore() { fetchPosts(isRefresh = false) }

    fun toggleLikeStatus(post: CommunityPost, userId: Int) {
        viewModelScope.launch {
            try {
                val postId = post.postId ?: return@launch
                val newLikeStatus = !post.isLiked
                val newLikesCount = if (newLikeStatus) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
                updatePostInList(post.copy(isLiked = newLikeStatus, likesCount = newLikesCount))

                val response = communityRepository.toggleLikePost(postId = postId, userId = userId)
                if (response.isSuccessful) {
                    response.body()?.let {
                        updatePostInList(post.copy(isLiked = it.is_liked, likesCount = it.likes_count))
                    }
                } else updatePostInList(post)
            } catch (e: Exception) { updatePostInList(post) }
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

    fun createPost(userId: Int, outfitId: Int, imageUrl: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val outfitDetailResponse = outfitRepository.getOutfitById(outfitId)
                val extractedTags = if (outfitDetailResponse.isSuccessful) outfitDetailResponse.body()?.data?.tagNames else null
                val randomHeightRatio = (10..15).random() / 10f

                val request = com.example.smartfashion.data.api.CreatePostRequest(
                    user_id = userId, outfit_id = outfitId, image_url = imageUrl,
                    description = description, height_ratio = randomHeightRatio, tags = extractedTags
                )

                val response = communityRepository.createCommunityPost(request)
                if (response.isSuccessful) {
                    fetchPosts(isRefresh = true); onSuccess()
                }
                else _errorMessage.value = "Không thể đăng bài."
            } catch (e: Exception) { Log.e("API_ERROR", "Lỗi createPost", e) }
            finally { _isLoading.value = false }
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            try {
                val response = communityRepository.deleteCommunityPost(postId)
                if (response.isSuccessful) _postsList.value = _postsList.value.filter { it.postId != postId }
            } catch (e: Exception) { Log.e("API_ERROR", "Lỗi deletePost", e) }
        }
    }
}