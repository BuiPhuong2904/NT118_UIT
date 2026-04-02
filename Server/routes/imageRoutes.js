const express = require('express');
const router = express.Router();
const multer = require('multer');
const imageController = require('../controllers/imageController');
const authMiddleware = require('../middleware/authMiddleware'); 

// Cấu hình Multer để lưu ảnh tạm vào RAM (MemoryStorage) giúp tăng tốc độ xử lý
const storage = multer.memoryStorage();
const upload = multer({ storage: storage });

router.post('/upload', authMiddleware, upload.single('image'), imageController.uploadAndProcessImage);

module.exports = router;