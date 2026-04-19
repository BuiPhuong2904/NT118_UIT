const Outfit = require('../models/Outfit');
const OutfitItem = require('../models/OutfitItem'); 
const Clothing = require('../models/Clothing');    
const Image = require('../models/Image');   

const OutfitTag = require('../models/OutfitTag');
const Tag = require('../models/Tag');

// Lấy danh sách outfits của người dùng theo user_id, sắp xếp mới nhất lên đầu
exports.getOutfitsByUser = async (req, res) => {
    try {
        const userId = req.params.userId;
        
        let { is_favorite, tags } = req.query; 

        let query = { user_id: userId };

        // Xử lý lọc Yêu thích
        if (is_favorite === 'true') {
            query.is_favorite = true;
        }

        // 2. Xử lý lọc theo nhiều Tag (Multi-select)
        if (tags) {
            if (!Array.isArray(tags)) {
                tags = [tags];
            }

            // Bước A: Tìm tag_id của TẤT CẢ các tag_name được gửi lên
            const tagDocs = await Tag.find({ tag_name: { $in: tags } }).lean();
            
            if (tagDocs.length === 0) {
                return res.status(200).json({ success: true, data: [] }); 
            }

            const tagIds = tagDocs.map(t => t.tag_id);

            // Bước B: Tìm tất cả các mapping outfit_tag có chứa các tag_id này
            const outfitTags = await OutfitTag.find({ tag_id: { $in: tagIds } }).lean();

            // Bước C: Dùng Logic AND - Đếm xem mỗi outfit_id thỏa mãn được bao nhiêu tag
            const outfitCounts = {};
            outfitTags.forEach(ot => {
                outfitCounts[ot.outfit_id] = (outfitCounts[ot.outfit_id] || 0) + 1;
            });

            const validOutfitIds = Object.keys(outfitCounts)
                .filter(id => outfitCounts[id] === tagIds.length) 
                .map(Number); 

            if (validOutfitIds.length === 0) {
                return res.status(200).json({ success: true, data: [] });
            }

            query.outfit_id = { $in: validOutfitIds };
        }

        // 3. Thực thi query lấy Outfits
        const outfits = await Outfit.find(query).sort({ created_at: -1 }).lean(); 
        
        for (let outfit of outfits) {
            const items = await OutfitItem.find({ outfit_id: outfit.outfit_id }).lean();
            
            outfit.clothes = items; 
        }

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
        
        const outfit = await Outfit.findOne({ outfit_id: outfitId }).lean();
        if (!outfit) {
            return res.status(404).json({ success: false, message: 'Không tìm thấy bộ phối đồ này' });
        }

        // Tìm danh sách các món đồ (OutfitItems) thuộc bộ này
        const outfitItems = await OutfitItem.find({ outfit_id: outfitId }).lean();
        const clothingIds = outfitItems.map(item => item.clothing_id);

        // Tìm chi tiết Quần áo (Clothes) dựa vào danh sách ID ở trên
        const clothes = await Clothing.find({ clothing_id: { $in: clothingIds } }).lean();

        // Tìm link ảnh cho từng món đồ
        for (let clothing of clothes) {
            if (clothing.image_id) {
                const image = await Image.findOne({ image_id: clothing.image_id }).lean();
                clothing.image_url = image ? image.url_no_bg : null;
            }
        }

        // Tìm danh sách tag của outfit
        const outfitTagMappings = await OutfitTag.find({ outfit_id: outfitId }).lean();
        const tagIds = outfitTagMappings.map(ot => ot.tag_id);
        const tags = await Tag.find({ tag_id: { $in: tagIds } }).lean();
        outfit.tagNames = tags.map(t => t.tag_name);

        // Gắn danh sách quần áo vào outfit trước khi trả về
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

// Cập nhật trạng thái Yêu thích 
exports.updateFavoriteStatus = async (req, res) => {
    try {
        const outfitId = parseInt(req.params.id);
        const { is_favorite } = req.body; 

        // Tìm bộ đồ và cập nhật lại trường is_favorite
        const updatedOutfit = await Outfit.findOneAndUpdate(
            { outfit_id: outfitId },
            { is_favorite: is_favorite },
            { new: true } 
        ).lean();

        if (!updatedOutfit) {
            return res.status(404).json({ success: false, message: 'Không tìm thấy bộ phối đồ này' });
        }

        res.status(200).json({
            success: true,
            data: updatedOutfit
        });
    } catch (error) {
        console.error("Lỗi cập nhật yêu thích:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// Tạo mới một outfit, bao gồm cả việc lưu các món đồ kèm theo tọa độ
exports.createOutfit = async (req, res) => {
    try {
        const { user_id, name, description, image_preview_url, items, tags } = req.body;

        if (!user_id || !items || items.length === 0) {
            return res.status(400).json({ success: false, message: 'Thiếu dữ liệu người dùng hoặc chưa chọn món đồ nào' });
        }

        const newOutfit = new Outfit({
            user_id: user_id,
            name: name || 'Outfit mới tạo',
            description: description || '',
            image_preview_url: image_preview_url || '', 
            is_favorite: false
        });

        const savedOutfit = await newOutfit.save();

        for (let item of items) {
            const newOutfitItem = new OutfitItem({
                outfit_id: savedOutfit.outfit_id,
                clothing_id: item.clothing_id,
                position_x: item.position_x || 0,
                position_y: item.position_y || 0,
                scale: item.scale || 1,
                rotation: item.rotation || 0, 
                z_index: item.z_index || 1
            });

            await newOutfitItem.save(); 
        }

        // LƯU TAG CHO OUTFIT (CHỈ LẤY TAG ĐÃ CÓ TRONG DB)
        if (tags && Array.isArray(tags) && tags.length > 0) {
            for (let tagName of tags) {
                let tagStr = tagName.trim();
                if (!tagStr) continue;

                // Tìm tag trong bảng Tag chung
                let tagObj = await Tag.findOne({ tag_name: tagStr });
                
                // NẾU TÌM THẤY (TAG TỒN TẠI) THÌ MỚI LƯU, KHÔNG THÌ BỎ QUA
                if (tagObj) {
                    await OutfitTag.create({ outfit_id: savedOutfit.outfit_id, tag_id: tagObj.tag_id });
                }
            }
        }

        res.status(201).json({
            success: true,
            message: 'Đã lưu bộ phối đồ thành công',
            data: savedOutfit
        });

    } catch (error) {
        console.error("Lỗi createOutfit:", error);
        res.status(500).json({ success: false, message: "Lỗi Server: " + error.message });
    }
};

// Cập nhật tên và mô tả bộ đồ
exports.updateOutfit = async (req, res) => {
    try {
        const { name, description, tags } = req.body;
        const outfitId = parseInt(req.params.id);

        const updatedOutfit = await Outfit.findOneAndUpdate(
            { outfit_id: outfitId },
            { name, description },
            { new: true }
        );

        if (!updatedOutfit) {
            return res.status(404).json({ success: false, message: "Không tìm thấy bộ đồ" });
        }

        // Xử lý Cập nhật Tag
        if (tags && Array.isArray(tags)) {
            await OutfitTag.deleteMany({ outfit_id: outfitId });

            for (let tagName of tags) {
                let tagStr = tagName.trim();
                if (!tagStr) continue;

                let tagObj = await Tag.findOne({ tag_name: tagStr });
                
                // CHỈ TẠO MAPPING NẾU TAG CÓ TRONG DB
                if (tagObj) {
                    await OutfitTag.create({ outfit_id: outfitId, tag_id: tagObj.tag_id });
                }
            }
        }

        res.status(200).json({ success: true, data: updatedOutfit });
    } catch (error) {
        console.error("Lỗi updateOutfit:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};

// Xóa bộ đồ
exports.deleteOutfit = async (req, res) => {
    try {
        const outfitId = parseInt(req.params.id);

        const OutfitItem = require('../models/OutfitItem');
        const OutfitTag = require('../models/OutfitTag');
        await OutfitItem.deleteMany({ outfit_id: outfitId });
        await OutfitTag.deleteMany({ outfit_id: outfitId });

        await Outfit.findOneAndDelete({ outfit_id: outfitId });
        
        res.status(200).json({ success: true, message: "Đã xóa thành công" });
    } catch (error) {
        res.status(500).json({ success: false, message: error.message });
    }
};