// controllers/weatherController.js

const axios = require('axios');
const WeatherCache = require('../models/WeatherCache');

exports.getCurrentWeather = async (req, res) => {
    try {
        const lat = parseFloat(req.query.lat || 10.8231);
        const lon = parseFloat(req.query.lon || 106.6297);
        const locationName = req.query.location_name || "Hồ Chí Minh";

        const now = new Date();

        // Tìm cache
        let weather = await WeatherCache.findOne({
            latitude: lat,
            longitude: lon,
            expired_at: { $gt: now }
        });

        if (weather) {
            return res.status(200).json({ success: true, data: weather });
        }

        const apiKey = process.env.OPENWEATHER_API_KEY;
        if (!apiKey) {
             return res.status(500).json({ success: false, message: "Chưa cấu hình API Key" });
        }

        const url = `https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&appid=${apiKey}&units=metric&lang=vi`;
        
        const response = await axios.get(url);
        const data = response.data;

        const expiredAt = new Date(now.getTime() + 2 * 60 * 60 * 1000); 

        // Ép kiểu dữ liệu cực kỳ cẩn thận trước khi tạo Object
        const newWeather = new WeatherCache({
            location_name: String(data.name || locationName),
            latitude: Number(lat),
            longitude: Number(lon),
            temp: Number(data.main.temp),
            condition: String(data.weather[0].description),
            icon_url: String(`https://openweathermap.org/img/wn/${data.weather[0].icon}@4x.png`),
            expired_at: expiredAt
        });

        await newWeather.save();

        res.status(200).json({ success: true, data: newWeather });

    } catch (error) {
        if (error.name === 'ValidationError') {
            console.error("Chi tiết lỗi Validation:", error.errors);
        } else {
            console.error("Lỗi lấy thời tiết:", error.message);
        }
        res.status(500).json({ success: false, message: "Lỗi hệ thống khi lưu thời tiết" });
    }
};