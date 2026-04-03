const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const MONGO_URI = process.env.MONGO_URI;

if (!MONGO_URI) {
    console.error("LỖI: Chưa cấu hình biến MONGO_URI trong file .env");
    process.exit(1);
}

mongoose.connect(MONGO_URI)
    .then(() => {
        console.log('Đã kết nối MongoDB thành công!');
        console.log('-------------------------------------');
    })
    .catch((err) => {
        console.error('Lỗi kết nối:', err);
    });

require('./models/Counter');       
require('./models/User');
require('./models/UserProfile');
require('./models/Category');
require('./models/Tag');
require('./models/Image');
require('./models/Clothing');
require('./models/SystemClothing');
require('./models/Outfit');
require('./models/OutfitItem');
require('./models/Schedule');
require('./models/TravelChecklist');
require('./models/Wishlist');
require('./models/UsageHistory');
require('./models/Notification');
require('./models/AIPromptLog');
require('./models/WeatherCache');
require('./models/ClothingTag');
require('./models/SystemClothesTag');
require('./models/OutfitTag');

const imageRoutes = require('./routes/imageRoutes');
app.use('/api/images', imageRoutes);

const outfitRoutes = require('./routes/outfitRoutes');
app.use('/api/outfits', outfitRoutes);

const clothesRoutes = require('./routes/clothesRoutes');
app.use('/api/clothes', clothesRoutes);

const aiRoutes = require('./routes/aiRoutes');
app.use('/api/ai', aiRoutes);

const systemClothesRoutes = require('./routes/systemClothesRoutes');
app.use('/api/system-clothes', systemClothesRoutes);

const categoryRoutes = require('./routes/categoryRoutes');
app.use('/api/categories', categoryRoutes);

const tagsRoutes = require('./routes/tagsRoutes');
app.use('/api/tags', tagsRoutes);

const wishlistRoutes = require('./routes/wishlistRoutes');
app.use('/api/wishlists', wishlistRoutes);

app.use("/api/auth", require("./routes/authRoutes"));

const scheduleRoutes = require('./routes/scheduleRoutes');
app.use('/api/schedules', scheduleRoutes);


app.get('/', (req, res) => {
    res.send('Server Smart Fashion (Full Models) đang chạy!');
});

app.use(express.static("public"));

app.listen(PORT, () => {
    console.log(`Server đang chạy tại http://localhost:${PORT}`);
});