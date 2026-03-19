const Clothes = require('../models/Clothing'); 
const Image = require('../models/Image');

// --- HÀM HỖ TRỢ ---
function getCategoryIdsForParent(parentId) {
    const id = parseInt(parentId);
    switch (id) {
        case 1: return [1, 7, 8, 9, 10, 11];
        case 2: return [2, 12, 13, 14, 15, 16, 17, 18];
        case 3: return [3, 19, 20, 21];
        case 4: return [4, 22, 23, 24, 25, 26, 27];
        case 5: return [5, 28, 29, 30, 31, 32];
        case 6: return [6, 33, 34, 35, 36];
        default: return [id];
    }
}

// --- API: Lấy danh sách quần áo CỦA MỘT USER CỤ THỂ (Phân Trang & Lọc Danh Mục) ---
exports.getClothesByUser = async (req, res) => {
    try {
        const targetUserId = parseInt(req.params.userId); 

        const categoryId = parseInt(req.query.categoryId) || 0; 
        const page = parseInt(req.query.page) || 1;             
        const limit = parseInt(req.query.limit) || 7;           

        const skip = (page - 1) * limit;

        let queryCondition = { user_id: targetUserId };

        if (categoryId !== 0) {
            const validIds = getCategoryIdsForParent(categoryId);
            queryCondition.category_id = { $in: validIds }; 
        }

        const clothes = await Clothes.find(queryCondition)
            .sort({ clothing_id: -1 }) 
            .skip(skip)   
            .limit(limit) 
            .lean(); 
        
        const imageIds = clothes.map(c => c.image_id);
        const images = await Image.find({ image_id: { $in: imageIds } }).lean();

        const clothesWithImages = clothes.map(cloth => {
            const matchedImage = images.find(img => img.image_id === cloth.image_id);
            return {
                ...cloth,
                image_url: matchedImage ? matchedImage.url_no_bg : null 
            };
        });

        res.status(200).json(clothesWithImages);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// Lấy danh sách tất cả đồ
exports.getAllClothes = async (req, res) => {
    try {
        const clothes = await Clothes.find().sort({ clothing_id: -1 }).lean(); 
        const imageIds = clothes.map(c => c.image_id);
        const images = await Image.find({ image_id: { $in: imageIds } }).lean();

        const clothesWithImages = clothes.map(cloth => {
            const matchedImage = images.find(img => img.image_id === cloth.image_id);
            return {
                ...cloth,
                image_url: matchedImage ? matchedImage.url_no_bg : null
            };
        });
        res.status(200).json(clothesWithImages);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// Thêm đồ mới
exports.createClothes = async (req, res) => {
    try {
        const newClothes = new Clothes(req.body);
        const savedClothes = await newClothes.save();
        res.status(201).json(savedClothes);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// Cập nhật thông tin đồ
exports.updateClothes = async (req, res) => {
    try {
        const updatedClothes = await Clothes.findOneAndUpdate(
            { clothing_id: req.params.id }, 
            req.body, 
            { new: true } 
        );
        
        if (!updatedClothes) return res.status(404).json({ message: "Không tìm thấy đồ cần sửa" });
        res.status(200).json(updatedClothes);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};

// Xóa đồ 
exports.deleteClothes = async (req, res) => {
    try {
        const deletedClothes = await Clothes.findOneAndDelete({ clothing_id: req.params.id });
        
        if (!deletedClothes) return res.status(404).json({ message: "Không tìm thấy đồ cần xóa" });
        res.status(200).json({ message: "Đã xóa thành công" });
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// Lấy chi tiết 1 món đồ theo ID
exports.getClothingById = async (req, res) => {
    try {
        const targetId = parseInt(req.params.id);
        const clothing = await Clothes.findOne({ clothing_id: targetId }).lean();
        
        if (!clothing) {
            return res.status(404).json({ message: "Không tìm thấy món đồ này" });
        }
        
        const image = await Image.findOne({ image_id: clothing.image_id }).lean();
        clothing.image_url = image ? image.url_no_bg : null; 
        
        res.status(200).json(clothing);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// --- API: Lấy danh sách đồ YÊU THÍCH (Có Phân Trang) ---
exports.getFavoriteClothesByUser = async (req, res) => {
    try {
        const targetUserId = parseInt(req.params.userId);
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 7;
        const skip = (page - 1) * limit;

        // 1. ĐẾM NHANH TỔNG SỐ ĐỒ TRONG DATABASE
        const totalCount = await Clothes.countDocuments({ user_id: targetUserId, is_favorite: true });

        // 2. Tải 7 món đồ như bình thường
        const clothes = await Clothes.find({ user_id: targetUserId, is_favorite: true })
            .sort({ clothing_id: -1 })
            .skip(skip)
            .limit(limit)
            .lean();
        
        const imageIds = clothes.map(c => c.image_id);
        const images = await Image.find({ image_id: { $in: imageIds } }).lean();

        const clothesWithImages = clothes.map(cloth => {
            const matchedImage = images.find(img => img.image_id === cloth.image_id);
            return {
                ...cloth,
                image_url: matchedImage ? matchedImage.url_no_bg : null 
            };
        });

        // 3. GẮN TỔNG SỐ LƯỢNG VÀO "HEADER" (Để Android đọc được ngay lập tức)
        res.setHeader('X-Total-Count', totalCount);
        res.setHeader('Access-Control-Expose-Headers', 'X-Total-Count');

        res.status(200).json(clothesWithImages);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};

// --- API: Lấy danh sách đồ dọn tủ (Chưa mặc > 30 ngày) ---
exports.getDeclutterClothesByUser = async (req, res) => {
    try {
        const targetUserId = parseInt(req.params.userId);
        const thirtyDaysAgo = new Date();
        thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);

        // Tìm đồ: (Đã mặc nhưng > 30 ngày) HOẶC (Chưa từng mặc và tạo > 30 ngày)
        const clothes = await Clothes.find({
            user_id: targetUserId,
            $or: [
                { last_worn: { $lt: thirtyDaysAgo } },
                { last_worn: null, createdAt: { $lt: thirtyDaysAgo } }
            ]
        }).lean();

        // Gắn ảnh
        const imageIds = clothes.map(c => c.image_id);
        const images = await Image.find({ image_id: { $in: imageIds } }).lean();

        const clothesWithImages = clothes.map(cloth => {
            const matchedImage = images.find(img => img.image_id === cloth.image_id);
            return { ...cloth, image_url: matchedImage ? matchedImage.url_no_bg : null };
        });

        res.status(200).json(clothesWithImages);
    } catch (error) {
        res.status(500).json({ message: error.message });
    }
};
