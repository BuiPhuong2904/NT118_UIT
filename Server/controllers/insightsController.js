const Clothing = require('../models/Clothing');
const Outfit = require('../models/Outfit');
const Schedule = require('../models/Schedule');

exports.getUserInsights = async (req, res) => {
    try {
        const userId = parseInt(req.params.userId);

        // --- 1. TÍNH ĐỘ NĂNG ĐỘNG TỦ ĐỒ ---
        const totalClothes = await Clothing.countDocuments({ user_id: userId });
        
        const thirtyDaysAgo = new Date();
        thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
        
        // Đồ đang mặc: Có last_worn và mặc trong vòng 30 ngày qua
        const activeClothes = await Clothing.countDocuments({
            user_id: userId,
            last_worn: { $gte: thirtyDaysAgo }
        });
        
        const utilizationPercent = totalClothes === 0 ? 0 : (activeClothes / totalClothes);
        const inactiveClothes = totalClothes - activeClothes;

        // --- 2. TÍNH ĐIỂM PHỐI ĐỒ (AI vs MANUAL) ---
        const outfits = await Outfit.find({ user_id: userId }).lean();
        let aiCount = 0, manualCount = 0;
        let aiRatingSum = 0, manualRatingSum = 0;

        outfits.forEach(o => {
            if (o.is_ai_suggested) {
                aiCount++;
                if (o.rating) aiRatingSum += o.rating;
            } else {
                manualCount++;
                if (o.rating) manualRatingSum += o.rating;
            }
        });

        const aiAvg = aiCount === 0 || aiRatingSum === 0 ? "0.0" : (aiRatingSum / aiCount).toFixed(1);
        const manualAvg = manualCount === 0 || manualRatingSum === 0 ? "0.0" : (manualRatingSum / manualCount).toFixed(1);

        // --- 3. TÍNH SỰ KIỆN PHỔ BIẾN ---
        const schedules = await Schedule.aggregate([
            { $match: { user_id: userId } },
            { $group: { _id: "$event_type", count: { $sum: 1 } } },
            { $sort: { count: -1 } }
        ]);
        
        const totalEvents = schedules.reduce((sum, item) => sum + item.count, 0);
        const eventBreakdown = schedules.map(s => ({
            label: s._id || "Khác",
            percent: totalEvents === 0 ? 0 : (s.count / totalEvents)
        }));

        // --- 4. TÍNH ĐẶC ĐIỂM TỦ ĐỒ (DNA) ---
        const materials = await Clothing.aggregate([
            { $match: { user_id: userId, material: { $ne: null, $ne: "" } } },
            { $group: { _id: "$material", count: { $sum: 1 } } },
            { $sort: { count: -1 } },
            { $limit: 3 }
        ]);

        const brands = await Clothing.aggregate([
            { $match: { user_id: userId, brand_name: { $ne: null, $ne: "" } } },
            { $group: { _id: "$brand_name", count: { $sum: 1 } } },
            { $sort: { count: -1 } },
            { $limit: 3 }
        ]);

        // TRẢ VỀ JSON GỘP
        res.status(200).json({
            success: true,
            data: {
                utilization: { percent: utilizationPercent, active: activeClothes, inactive: inactiveClothes },
                ratings: {
                    ai: { avg: aiAvg, count: aiCount },
                    manual: { avg: manualAvg, count: manualCount }
                },
                events: eventBreakdown,
                dna: {
                    materials: materials.map(m => ({ name: m._id, count: m.count })),
                    brands: brands.map(b => ({ name: b._id, count: b.count }))
                }
            }
        });
    } catch (error) {
        console.error("Lỗi getUserInsights:", error);
        res.status(500).json({ success: false, message: error.message });
    }
};