const express = require('express');
const router = express.Router();


const {
  getPackingItems,
  togglePackingItem,
  createPackingItems
} = require('../controllers/packingController');

// GET checklist theo trip
router.get('/trip/:tripId', getPackingItems);
// TOGGLE checkbox
router.post('/trip', createPackingItems);
// CREATE checklist từ AI
router.patch('/:id/toggle', togglePackingItem);


module.exports = router;