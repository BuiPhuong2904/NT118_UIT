package com.example.smartfashion.ui.screens.hub

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfashion.data.repository.CommunityRepository
import com.example.smartfashion.model.CommunityPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPostsViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    private val _postsList = MutableStateFlow<List<CommunityPost>>(emptyList())
    val postsList: StateFlow<List<CommunityPost>> = _postsList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10
    private var isLastPage = false
    private var isFetching = false

    fun fetchMyPosts(userId: Int, isRefresh: Boolean = false) {
        if (isFetching) return
        if (isRefresh) { currentPage = 1; isLastPage = false }
        if (isLastPage) return

        viewModelScope.launch {
            isFetching = true
            if (isRefresh) _isLoading.value = true

            try {
                val response = communityRepository.getMyCommunityPosts(userId, currentPage, pageSize)
                if (response.isSuccessful) {
                    response.body()?.data?.let { newList ->
                        if (newList.size < pageSize) isLastPage = true

                        if (isRefresh) _postsList.value = newList
                        else _postsList.value = _postsList.value + newList

                        currentPage++
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi tải My Posts: ", e)
            } finally {
                isFetching = false
                _isLoading.value = false
            }
        }
    }

    fun toggleLikeStatus(post: CommunityPost, userId: Int) {
        viewModelScope.launch {
            try {
                val postId = post.postId ?: return@launch

                // 1. Cập nhật UI ngay lập tức để mượt mà
                val newLikeStatus = !post.isLiked
                val newLikesCount = if (newLikeStatus) post.likesCount + 1 else maxOf(0, post.likesCount - 1)

                updatePostInList(post.copy(isLiked = newLikeStatus, likesCount = newLikesCount))

                // 2. Gọi API ngầm phía sau
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
                }
            } catch (e: Exception) {
                updatePostInList(post)
                Log.e("API_ERROR", "Lỗi thả tim: ", e)
            }
        }
    }

    // Hàm hỗ trợ cập nhật 1 item trong danh sách
    private fun updatePostInList(updatedPost: CommunityPost) {
        val currentList = _postsList.value.toMutableList()
        val index = currentList.indexOfFirst { it.postId == updatedPost.postId }
        if (index != -1) {
            currentList[index] = updatedPost
            _postsList.value = currentList
        }
    }

    fun deletePost(postId: Int) {
        viewModelScope.launch {
            try {
                val response = communityRepository.deleteCommunityPost(postId)
                if (response.isSuccessful) {
                    _postsList.value = _postsList.value.filter { it.postId != postId }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi xóa My Posts: ", e)
            }
        }
    }
}