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

// --- API: Lấy danh sách kho mẫu (có phân trang, tìm kiếm, lọc) ---
exports.getAllTemplates = async (req, res) => {
    try {
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 7;
        const skip = (page - 1) * limit;
        const search = req.query.search || '';

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

        const pipeline = [];

        if (search.trim() !== '') {
            const regexPattern = createVietnameseRegex(search.trim());
            pipeline.push({
                $match: { name: { $regex: regexPattern, $options: 'i' } }
            });
        }

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

        if (requestedTags.length > 0) {
            pipeline.push({
                $match: { tags: { $all: requestedTags } } 
            });
        }

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