require('dotenv').config();
const mongoose = require('mongoose');

const MONGO_URI = process.env.MONGO_URI;

if (!MONGO_URI) {
    console.error("LỖI: Không tìm thấy MONGO_URI trong file .env");
    process.exit(1);
}

mongoose.connect(MONGO_URI)
    .then(() => console.log('🔌 Đang kết nối Database...'))
    .catch(err => {
        console.error(' Lỗi kết nối:', err);
        process.exit(1);
    });

const Counter = require('./models/Counter');

async function syncCollection(modelName, modelPath, idField, counterId) {
    try {
        const Model = require(modelPath);
        
        const lastDoc = await Model.findOne().sort({ [idField]: -1 });
        
        let maxId = 0;
        if (lastDoc && lastDoc[idField]) {
            maxId = lastDoc[idField];
        }

        await Counter.findByIdAndUpdate(
            { _id: counterId },
            { seq: maxId }, 
            { new: true, upsert: true } 
        );

        console.log(` [${modelName}] ID lớn nhất hiện tại: ${maxId} -> Counter đã set thành ${maxId}`);
    } catch (error) {
        console.log(` [${modelName}] Bỏ qua (Chưa có dữ liệu hoặc lỗi file): ${error.message}`);
    }
}

async function run() {
    console.log("BẮT ĐẦU ĐỒNG BỘ DỮ LIỆU CŨ...");
    console.log("------------------------------------------------");

    // 1. Người dùng
    await syncCollection('User', './models/User', 'user_id', 'user_id');
    await syncCollection('UserProfile', './models/UserProfile', 'profiles_id', 'profiles_id');

    // 2. Kho đồ
    await syncCollection('Category', './models/Category', 'category_id', 'category_id');
    await syncCollection('Clothing', './models/Clothing', 'clothing_id', 'clothing_id');
    await syncCollection('Image', './models/Image', 'image_id', 'image_id');
    await syncCollection('Tag', './models/Tag', 'tag_id', 'tag_id');
    await syncCollection('SystemClothing', './models/SystemClothing', 'template_id', 'template_id');

    // 3. Các tính năng
    await syncCollection('Outfit', './models/Outfit', 'outfit_id', 'outfit_id');
    await syncCollection('OutfitItem', './models/OutfitItem', 'item_id', 'item_id');
    await syncCollection('Schedule', './models/Schedule', 'schedule_id', 'schedule_id');
    await syncCollection('TravelChecklist', './models/TravelChecklist', 'travel_id', 'travel_id');
    await syncCollection('Wishlist', './models/Wishlist', 'wishlist_id', 'wishlist_id');
    await syncCollection('UsageHistory', './models/UsageHistory', 'usage_id', 'usage_id');
    await syncCollection('Notification', './models/Notification', 'noti_id', 'noti_id');

    // 4. Các bảng còn lại
    await syncCollection('AIPromptLog', './models/AIPromptLog', 'ailog_id', 'ailog_id');
    await syncCollection('WeatherCache', './models/WeatherCache', 'weather_id', 'weather_id');
    await syncCollection('ClothingTag', './models/ClothingTag', 'clothing_tag_id', 'clothing_tag_id');
    await syncCollection('SystemClothesTag', './models/SystemClothesTag', 'system_tag_id', 'system_tag_id');
    await syncCollection('OutfitTag', './models/OutfitTag', 'outfit_tag_id', 'outfit_tag_id');

    console.log("------------------------------------------------");
    console.log("Bạn có thể bắt đầu thêm mới dữ liệu, ID sẽ tự động chạy tiếp.");
    process.exit();
}

run();