const CommunityPost = require('../models/CommunityPost');
const CommunityPostTag = require('../models/CommunityPostTag');
const CommunityPostLike = require('../models/CommunityPostLike');
const User = require('../models/User');
const Tag = require('../models/Tag');

// Lấy danh sách Bài đăng (Trang Feed mạng xã hội có phân trang & lọc Tag)
exports.getAllPosts = async (req, res) => {
    try {
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 10;
        const skip = (page - 1) * limit;
        
        let { tag, mode } = req.query; 
        const userId = req.user?.user_id;

        let query = {};
        let sortCondition = { created_at: -1 };

        // TRƯỜNG HỢP 1: LỌC THEO TAG CỤ THỂ
        if (tag) {
            const tagArray = tag.split(',').map(t => t.trim());
            const tagObjs = await Tag.find({ tag_name: { $in: tagArray } }).lean();
            
            if (tagObjs.length === 0) 
                return res.status(200).json({ success: true, data: [] });

            const tagIds = tagObjs.map(t => t.tag_id);
            const postTags = await CommunityPostTag.find({ tag_id: { $in: tagIds } }).lean();
            const validPostIds = [...new Set(postTags.map(pt => pt.post_id))];
            
            if (validPostIds.length === 0) return res.status(200).json({ success: true, data: [] });
            query.post_id = { $in: validPostIds };
        }
        // TRƯỜNG HỢP 2: LỌC THEO CHẾ ĐỘ XEM (Mode)
        else {
            if (mode === 'Đang hot') {
                const sevenDaysAgo = new Date();
                sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);
                query.created_at = { $gte: sevenDaysAgo };
                sortCondition = { likes_count: -1, created_at: -1 };
            } 
            else if (mode === 'Dành cho bạn' && userId) {
                const userLikes = await CommunityPostLike.find({ user_id: userId }).lean();
                
                if (userLikes.length > 0) {
                    const likedPostIds = userLikes.map(l => l.post_id);
                    const likedTags = await CommunityPostTag.find({ post_id: { $in: likedPostIds } }).lean();
                    const favoriteTagIds = [...new Set(likedTags.map(t => t.tag_id))];
                    const recommendedPostTags = await CommunityPostTag.find({ tag_id: { $in: favoriteTagIds } }).lean();
                    const recommendedPostIds = recommendedPostTags.map(pt => pt.post_id);
                    
                    query.post_id = { $in: recommendedPostIds, $nin: likedPostIds };
                } else {
                    sortCondition = { likes_count: -1, created_at: -1 };
                }
            }
        }

        // Thực thi Query lấy bài đăng
        const posts = await CommunityPost.find(query)
            .sort(sortCondition)
            .skip(skip)
            .limit(limit)
            .lean();

        if (posts.length === 0) {
            return res.status(200).json({ success: true, data: [] });
        }

        // Lấy thông tin Tác giả và check trạng thái Like của User hiện tại
        const userIds = [...new Set(posts.map(p => p.user_id))];
        const users = await User.find({ user_id: { $in: userIds } }).lean();
        
        let likedPostIdsByUser = [];
        if (userId) {
            const likes = await CommunityPostLike.find({ user_id: userId, post_id: { $in: posts.map(p => p.post_id) } }).lean();
            likedPostIdsByUser = likes.map(l => l.post_id);
        }

        // Ráp dữ liệu
        const postsWithAuthor = posts.map(post => {
            const author = users.find(u => u.user_id === post.user_id);
            return {
                post_id: post.post_id,
                image_url: post.image_url,
                description: post.description,
                likes_count: post.likes_count,
                height_ratio: post.height_ratio,
                author_name: author ? author.username : "Người dùng ẩn danh",
                author_avatar: author ? author.avatar_url : null,
                is_liked: likedPostIdsByUser.includes(post.post_id),
                created_at: post.created_at
            };
        });

        res.status(200).json({ success: true, data: postsWithAuthor });
    } catch (error) {
        console.error("Lỗi getAllPosts:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// Thêm bài đăng mới
exports.createPost = async (req, res) => {
    try {
        const { user_id, outfit_id, image_url, description, height_ratio, tags } = req.body;

        if (!user_id || !outfit_id || !image_url || !height_ratio) {
            return res.status(400).json({ success: false, message: "Thiếu dữ liệu bắt buộc để đăng bài" });
        }

        const newPost = new CommunityPost({
            user_id,
            outfit_id,
            image_url,
            description: description || '',
            height_ratio: height_ratio,
            likes_count: 0
        });

        const savedPost = await newPost.save();

        // Xử lý lưu Tags
        if (tags && Array.isArray(tags) && tags.length > 0) {
            for (let tagName of tags) {
                let tagStr = tagName.trim();
                if (!tagStr) continue;

                let tagObj = await Tag.findOne({ tag_name: tagStr });
                if (tagObj) {
                    await CommunityPostTag.create({ post_id: savedPost.post_id, tag_id: tagObj.tag_id });
                }
            }
        }

        res.status(201).json({ success: true, message: "Đăng bài thành công", data: savedPost });
    } catch (error) {
        console.error("Lỗi createPost:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// Xử lý logic Thả tim / Bỏ thả tim (Toggle Like)
exports.toggleLikePost = async (req, res) => {
    try {
        const postId = parseInt(req.params.id);
        const { user_id } = req.body;

        if (!user_id) return res.status(400).json({ success: false, message: "Thiếu user_id" });

        const post = await CommunityPost.findOne({ post_id: postId });
        if (!post) {
            return res.status(404).json({ success: false, message: "Không tìm thấy bài đăng" });
        }

        const existingLike = await CommunityPostLike.findOne({ user_id: user_id, post_id: postId });

        if (existingLike) {
            await CommunityPostLike.findOneAndDelete({ post_like_id: existingLike.post_like_id });
            post.likes_count = Math.max(0, post.likes_count - 1);
            await post.save();
            
            return res.status(200).json({ success: true, message: "Đã hủy thích bài viết", likes_count: post.likes_count, is_liked: false });
        } else {
            const newLike = new CommunityPostLike({ user_id, post_id: postId });
            await newLike.save();
            post.likes_count += 1;
            await post.save();

            return res.status(200).json({ success: true, message: "Đã thích bài viết", likes_count: post.likes_count, is_liked: true });
        }
    } catch (error) {
        console.error("Lỗi toggleLikePost:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// Xóa bài đăng
exports.deletePost = async (req, res) => {
    try {
        const postId = parseInt(req.params.id);

        await CommunityPostTag.deleteMany({ post_id: postId });
        await CommunityPostLike.deleteMany({ post_id: postId });

        const deletedPost = await CommunityPost.findOneAndDelete({ post_id: postId });

        if (!deletedPost) {
            return res.status(404).json({ success: false, message: "Không tìm thấy bài đăng để xóa" });
        }

        res.status(200).json({ success: true, message: "Đã xóa bài đăng thành công" });
    } catch (error) {
        console.error("Lỗi deletePost:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};