const express = require('express');
const router = express.Router();
const tripController = require('../controllers/tripController');
const auth = require('../middleware/authMiddleware'); 

// 1. Tạo chuyến đi (cần auth)
router.post('/', auth, tripController.createTrip);

// 2. Lấy danh sách trip của user (/me)
router.get('/me', auth, tripController.getMyTrips);

// 3. Lấy chi tiết (có check user trong controller)
router.get('/:id', auth, tripController.getTripById);

// 4. Update (có check user)
router.put('/:id', auth, tripController.updateTrip);

// 5. Delete (có check user)
router.delete('/:id', auth, tripController.deleteTrip);

module.exports = router;
