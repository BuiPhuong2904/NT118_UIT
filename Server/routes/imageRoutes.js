const express = require('express');
const router = express.Router();
const multer = require('multer');
const imageController = require('../controllers/imageController');

// Cấu hình Multer để lưu ảnh tạm vào RAM (MemoryStorage) giúp tăng tốc độ xử lý
const storage = multer.memoryStorage();
const upload = multer({ storage: storage });

// hứng file có tên là 'image' từ Android
router.post('/upload', upload.single('image'), imageController.uploadAndProcessImage);

module.exports = router;