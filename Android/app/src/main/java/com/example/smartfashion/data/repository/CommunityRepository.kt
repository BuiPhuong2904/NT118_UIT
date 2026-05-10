package com.example.smartfashion.data.repository

import com.example.smartfashion.data.api.ApiService
import com.example.smartfashion.data.api.CreatePostRequest
import com.example.smartfashion.data.api.ToggleLikeRequest
import javax.inject.Inject

class CommunityRepository @Inject constructor(
    private val apiService: ApiService
) {
    // Lấy danh sách bài đăng trên Cộng đồng (có phân trang và lọc theo Tag)
    suspend fun getCommunityPosts(
        page: Int,
        limit: Int,
        tag: String? = null,
        mode: String? = null
    ) = apiService.getCommunityPosts(page, limit, tag, mode)

    // Tạo bài đăng mới (Chia sẻ Outfit)
    suspend fun createCommunityPost(request: CreatePostRequest) =
        apiService.createCommunityPost(request)

    // Thả tim hoặc Hủy thả tim bài viết
    suspend fun toggleLikePost(postId: Int, userId: Int) =
        apiService.toggleLikePost(postId, ToggleLikeRequest(user_id = userId))

    // Xóa bài đăng
    suspend fun deleteCommunityPost(postId: Int) =
        apiService.deleteCommunityPost(postId)
}