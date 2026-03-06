const express = require('express');
const router = express.Router();
const outfitController = require('../controllers/outfitController');

// Route lấy outfit theo user_id
router.get('/user/:userId', outfitController.getOutfitsByUser);

// Route lấy chi tiết outfit theo outfit_id
router.get('/:outfitId', outfitController.getOutfitById);

module.exports = router;