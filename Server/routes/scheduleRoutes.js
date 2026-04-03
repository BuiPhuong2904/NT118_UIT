const express = require('express');
const router = express.Router();
const scheduleController = require('../controllers/scheduleController');

// Thêm lịch mới
router.post('/', scheduleController.createSchedule);

// Lấy danh sách ngày có lịch trong tháng
router.get('/user/:userId/month', scheduleController.getPlannedDaysInMonth);

// Lấy chi tiết lịch của 1 ngày
router.get('/user/:userId/date', scheduleController.getSchedulesByDate);

// Xóa lịch trình
router.delete('/:id', scheduleController.deleteSchedule);

module.exports = router;