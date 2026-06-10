const express = require('express');
const router = express.Router();

const {
  getPackingItems,
  togglePackingItem,
  createPackingItems,
  addPackingItem,
  updatePackingItem,
  deletePackingItem
} = require('../controllers/packingController');

// GET checklist theo trip
router.get('/trip/:tripId', getPackingItems);
// CREATE checklist từ AI 
router.post('/trip', createPackingItems);
// TOGGLE checkbox
router.patch('/:id/toggle', togglePackingItem);
// Thêm 1 item thủ công
router.post('/item', addPackingItem);
// Cập nhật tên 1 item
router.put('/:id', updatePackingItem);
// Xóa 1 item
router.delete('/:id', deletePackingItem);

module.exports = router;