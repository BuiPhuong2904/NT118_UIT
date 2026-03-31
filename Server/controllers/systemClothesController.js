const SystemClothing = require('../models/SystemClothing'); 

// --- HÀM HỖ TRỢ ---
function createVietnameseRegex(keyword) {
    let str = keyword.toLowerCase();
    str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
    str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
    str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
    str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
    str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
    str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
    str = str.replace(/đ/g, "d");
    str = str.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
    str = str.replace(/a/g, "[aáàảãạăắằẳẵặâấầẩẫậ]");
    str = str.replace(/e/g, "[eéèẻẽẹêếềểễệ]");
    str = str.replace(/i/g, "[iíìỉĩị]");
    str = str.replace(/o/g, "[oóòỏõọôốồổỗộơớờởỡợ]");
    str = str.replace(/u/g, "[uúùủũụưứừửữự]");
    str = str.replace(/y/g, "[yýỳỷỹỵ]");
    str = str.replace(/d/g, "[dđ]");
    return str;
}

exports.getAllTemplates = async (req, res) => {
    try {
        // LẤY THAM SỐ TỪ ANDROID
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 7;
        const skip = (page - 1) * limit;
        const search = req.query.search || '';

        // Nhận MẢNG categoryId từ Android
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

        // KHỞI TẠO PIPELINE
        const pipeline = [];

        // 1. TÌM KIẾM THEO TÊN
        if (search.trim() !== '') {
            const regexPattern = createVietnameseRegex(search.trim());
            pipeline.push({
                $match: { name: { $regex: regexPattern, $options: 'i' } }
            });
        }

        // 2. NỐI BẢNG CATEGORIES TRƯỚC
        pipeline.push(
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
        );

        // 3. LOGIC LỌC NHIỀU DANH MỤC "CHA BAO TRÙM CON"
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

        // 4. DỌN DẸP DỮ LIỆU & NỐI BẢNG TAGS
        pipeline.push(
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
        );

        // 5. LỌC THEO TAG 
        if (requestedTags.length > 0) {
            pipeline.push({
                $match: { tags: { $all: requestedTags } } 
            });
        }

        // 6. PHÂN TRANG (PAGINATION)
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
        const pipeline = [
            { $match: { template_id: targetId } },
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
        
        res.status(200).json(template[0]);
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

        // Đếm tổng số lượng
        const totalCount = await SystemClothing.countDocuments({ is_favorite: true });

        // Dùng Pipeline y hệt như getAllTemplates nhưng thêm $match is_favorite
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
            { $sort: { template_id: 1 } },
            { $skip: skip },
            { $limit: limit }
        ];

        const templates = await SystemClothing.aggregate(pipeline);

        // Gửi Header để Android tính tổng
        res.setHeader('X-Total-Count', totalCount);
        res.setHeader('Access-Control-Expose-Headers', 'X-Total-Count');

        res.status(200).json(templates);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};