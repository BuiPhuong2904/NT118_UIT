const express = require('express');
const router = express.Router();
const tripController = require('../controllers/tripController');
// const auth = require('../middleware/authMiddleware'); // bật lại sau

// 1. Tạo chuyến đi
router.post('/', tripController.createTrip);

// 2. Lấy danh sách theo user
router.get('/user/:userId', tripController.getTripsByUser);

// 3. Lấy chi tiết
router.get('/:id', tripController.getTripById);

// 4. Update
router.put('/:id', tripController.updateTrip);

// 5. Delete
router.delete('/:id', tripController.deleteTrip);

module.exports = router;
