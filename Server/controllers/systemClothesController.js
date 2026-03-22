const SystemClothing = require('../models/SystemClothing'); 

exports.getAllTemplates = async (req, res) => {
    try {
        // 1. LẤY THAM SỐ TỪ ANDROID
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 7;
        const skip = (page - 1) * limit;
        
        // =========================================================
        // 🌟 SỬA Ở ĐÂY: Nhận MẢNG categoryId từ Android thay vì 1 số
        // =========================================================
        let categoryIdsFilter = [];
        if (req.query.categoryId) {
            categoryIdsFilter = Array.isArray(req.query.categoryId) 
                ? req.query.categoryId.map(Number) 
                : [Number(req.query.categoryId)];
        }

        let requestedTags = [];
        if (req.query.tags) {
            requestedTags = Array.isArray(req.query.tags) ? req.query.tags : [req.query.tags];
        }

        const pipeline = [
            // --- NỐI BẢNG CATEGORIES TRƯỚC ---
            {
                $lookup: {
                    from: "categories",           
                    localField: "category_id",    
                    foreignField: "category_id",  
                    as: "category_info"           
                }
            },
            {
                $unwind: {
                    path: "$category_info",
                    preserveNullAndEmptyArrays: true 
                }
            }
        ];

        // =========================================================
        // 🌟 SỬA Ở ĐÂY: LOGIC LỌC NHIỀU DANH MỤC "CHA BAO TRÙM CON"
        // Dùng $in để tìm tất cả quần áo thuộc 1 trong các category đó
        // =========================================================
        if (categoryIdsFilter.length > 0) {
            pipeline.push({
                $match: {
                    $or: [
                        { "category_info.category_id": { $in: categoryIdsFilter } },
                        { "category_info.parent_id": { $in: categoryIdsFilter } }
                    ]
                }
            });
        }

        // --- SAU KHI LỌC XONG, MỚI DỌN DẸP DỮ LIỆU ---
        pipeline.push(
            {
                $addFields: { category_name: "$category_info.name" }
            },
            {
                $project: { category_info: 0 }
            },

            // --- NỐI BẢNG TAGS VÀ ÉP THÀNH MẢNG ---
            {
                $lookup: {
                    from: "system_clothes_tags", 
                    localField: "template_id",
                    foreignField: "template_id",
                    as: "tag_mappings"
                }
            },
            {
                $lookup: {
                    from: "tags", 
                    localField: "tag_mappings.tag_id",
                    foreignField: "tag_id",
                    as: "tag_details"
                }
            },
            {
                $addFields: {
                    tags: {
                        $map: { input: "$tag_details", as: "tag", in: "$$tag.tag_name" }
                    }
                }
            },
            {
                $project: { tag_mappings: 0, tag_details: 0 }
            }
        );

        // --- LỌC THEO TAG ---
        if (requestedTags.length > 0) {
            pipeline.push({
                $match: { tags: { $all: requestedTags } } 
            });
        }

        // --- PHÂN TRANG (PAGINATION) ---
        pipeline.push(
            { $sort: { template_id: 1 } }, 
            { $skip: skip },                
            { $limit: limit }               
        );

        const templates = await SystemClothing.aggregate(pipeline);
        res.status(200).json(templates);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// --- API: Cập nhật thông tin kho mẫu (dùng để lưu trạng thái thả tim) ---
exports.updateSystemClothing = async (req, res) => {
    try {
        const targetId = parseInt(req.params.id);

        const updatedTemplate = await SystemClothing.findOneAndUpdate(
            { template_id: targetId }, 
            req.body,  
            { new: true }
        );
        
        if (!updatedTemplate) {
            return res.status(404).json({ message: "Không tìm thấy mẫu này trong kho" });
        }
        
        res.status(200).json(updatedTemplate);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// --- API: Lấy chi tiết 1 mẫu đồ theo ID ---
exports.getSystemClothingById = async (req, res) => {
    try {
        const targetId = parseInt(req.params.id);
        
        // Pipeline aggregate giống hệt lúc lấy danh sách, nhưng thêm $match id
        const pipeline = [
            { $match: { template_id: targetId } }, // Tìm đúng ID
            {
                $lookup: {
                    from: "categories",
                    localField: "category_id",
                    foreignField: "category_id",
                    as: "category_info"
                }
            },
            {
                $unwind: {
                    path: "$category_info",
                    preserveNullAndEmptyArrays: true
                }
            },
            {
                $addFields: { category_name: "$category_info.name" }
            },
            {
                $project: { category_info: 0 }
            },
            {
                $lookup: {
                    from: "system_clothes_tags",
                    localField: "template_id",
                    foreignField: "template_id",
                    as: "tag_mappings"
                }
            },
            {
                $lookup: {
                    from: "tags",
                    localField: "tag_mappings.tag_id",
                    foreignField: "tag_id",
                    as: "tag_details"
                }
            },
            {
                $addFields: {
                    tags: {
                        $map: { input: "$tag_details", as: "tag", in: "$$tag.tag_name" }
                    }
                }
            },
            {
                $project: { tag_mappings: 0, tag_details: 0 }
            }
        ];

        const template = await SystemClothing.aggregate(pipeline);
        
        if (!template || template.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy mẫu này" });
        }
        
        res.status(200).json(template[0]); // Trả về object đầu tiên
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// --- API: Lấy danh sách KHO MẪU YÊU THÍCH (Wishlist) ---
exports.getFavoriteSystemClothes = async (req, res) => {
    try {
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 7;
        const skip = (page - 1) * limit;

        // 1. Đếm tổng số lượng
        const totalCount = await SystemClothing.countDocuments({ is_favorite: true });

        // 2. Dùng Pipeline y hệt như getAllTemplates nhưng thêm $match is_favorite
        const pipeline = [
            { $match: { is_favorite: true } },
            {
                $lookup: {
                    from: "categories",
                    localField: "category_id",
                    foreignField: "category_id",
                    as: "category_info"
                }
            },
            {
                $unwind: { path: "$category_info", preserveNullAndEmptyArrays: true }
            },
            { $addFields: { category_name: "$category_info.name" } },
            { $project: { category_info: 0 } },
            { $sort: { template_id: 1 } }, // 1 là cũ nhất lên đầu, -1 là mới nhất lên đầu
            { $skip: skip },
            { $limit: limit }
        ];

        const templates = await SystemClothing.aggregate(pipeline);

        // 3. Gửi Header để Android tính tổng
        res.setHeader('X-Total-Count', totalCount);
        res.setHeader('Access-Control-Expose-Headers', 'X-Total-Count');

        res.status(200).json(templates);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};