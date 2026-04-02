const Wishlist = require('../models/Wishlist');
const SystemClothing = require('../models/SystemClothing');

// --- 1. THÊM VÀO WISHLIST ---
exports.addToWishlist = async (req, res) => {
    try {
        const { user_id, template_id, item_name, image_url, price_estimate, link_store } = req.body;

        if (!user_id || !item_name) {
            return res.status(400).json({ message: "Thiếu user_id hoặc item_name" });
        }

        if (template_id) {
            const existingItem = await Wishlist.findOne({ user_id: user_id, template_id: template_id });
            if (existingItem) {
                return res.status(400).json({ message: "Món đồ này đã có trong wishlist của bạn" });
            }
        }

        const newWishlistItem = new Wishlist({
            user_id,
            template_id: template_id || null,
            item_name,
            image_url: image_url || null,
            price_estimate: price_estimate || null,
            link_store: link_store || null,
            status: 'pending'
        });

        const savedItem = await newWishlistItem.save();
        res.status(201).json(savedItem);
    } catch (error) {
        console.error("LỖI ADD WISHLIST:", error); 
        res.status(500).json({ message: error.message });
    }
};

// --- 2. LẤY DANH SÁCH WISHLIST CỦA 1 USER ---
exports.getUserWishlist = async (req, res) => {
    try {
        const userId = parseInt(req.params.userId);
        const status = req.query.status;
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 10;
        const skip = (page - 1) * limit;

        let query = { user_id: userId };
        if (status) {
            query.status = status;
        }

        const wishlists = await Wishlist.find(query)
            .sort({ createdAt: -1 })
            .skip(skip)
            .limit(limit);

        const totalCount = await Wishlist.countDocuments(query);

        res.status(200).json({
            totalCount,
            totalPages: Math.ceil(totalCount / limit),
            currentPage: page,
            data: wishlists
        });
    } catch (error) {
        console.error("LỖI GET WISHLIST:", error);
        res.status(500).json({ message: error.message });
    }
};

// --- 3. XÓA KHỎI WISHLIST ("Bỏ thả tim") ---
exports.removeFromWishlist = async (req, res) => {
    try {
        const wishlistId = parseInt(req.params.id);
        const userId = parseInt(req.query.user_id);

        const deletedItem = await Wishlist.findOneAndDelete({ 
            wishlist_id: wishlistId,
            user_id: userId 
        });

        if (!deletedItem) {
            return res.status(404).json({ message: "Không tìm thấy món đồ hoặc bạn không có quyền xóa" });
        }

        res.status(200).json({ message: "Đã xóa khỏi wishlist thành công", deletedItem });
    } catch (error) {
        console.error("LỖI DELETE WISHLIST:", error);
        res.status(500).json({ message: error.message });
    }
};

// --- 4. CẬP NHẬT TRẠNG THÁI (Đã mua -> Chuyển vào tủ đồ) ---
exports.updateWishlistStatus = async (req, res) => {
    try {
        const wishlistId = parseInt(req.params.id);
        const { status } = req.body;

        const updatedItem = await Wishlist.findOneAndUpdate(
            { wishlist_id: wishlistId },
            { status: status },
            { new: true }
        );

        if (!updatedItem) {
            return res.status(404).json({ message: "Không tìm thấy món đồ" });
        }

        res.status(200).json(updatedItem);
    } catch (error) {
        res.status(400).json({ message: error.message });
    }
};