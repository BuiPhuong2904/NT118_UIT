const Outfit = require('../models/Outfit');
const OutfitItem = require('../models/OutfitItem'); 
const Clothing = require('../models/Clothing');    
const Image = require('../models/Image');        

// Lấy danh sách outfits của người dùng theo user_id, sắp xếp mới nhất lên đầu
exports.getOutfitsByUser = async (req, res) => {
    try {
        const userId = req.params.userId;
        const outfits = await Outfit.find({ user_id: userId }).sort({ created_at: -1 });
        
        res.status(200).json({
            success: true,
            data: outfits
        });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};

// Lấy chi tiết một outfit theo outfit_id, bao gồm danh sách quần áo và link ảnh
exports.getOutfitById = async (req, res) => {
    try {
        const outfitId = parseInt(req.params.outfitId);
        
        // 1. Lấy thông tin cơ bản của bộ phối đồ (Vỏ)
        const outfit = await Outfit.findOne({ outfit_id: outfitId }).lean();
        if (!outfit) {
            return res.status(404).json({ success: false, message: 'Không tìm thấy bộ phối đồ này' });
        }

        // 2. Tìm danh sách các món đồ (OutfitItems) thuộc bộ này
        const outfitItems = await OutfitItem.find({ outfit_id: outfitId }).lean();
        const clothingIds = outfitItems.map(item => item.clothing_id);

        // 3. Tìm chi tiết Quần áo (Clothes) dựa vào danh sách ID ở trên
        const clothes = await Clothing.find({ clothing_id: { $in: clothingIds } }).lean();

        // 4. Tìm link ảnh cho từng món đồ
        for (let clothing of clothes) {
            if (clothing.image_id) {
                const image = await Image.findOne({ image_id: clothing.image_id }).lean();
                clothing.image_url = image ? image.url_no_bg : null;
            }
        }

        // 5. Gắn danh sách quần áo vào cục outfit trả về
        outfit.clothes = clothes;

        res.status(200).json({
            success: true,
            data: outfit
        });
    } catch (error) {
        console.error("Lỗi lấy chi tiết outfit:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};