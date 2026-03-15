const express = require('express');
const router = express.Router();
const systemClothesController = require('../controllers/systemClothesController');

router.get('/', systemClothesController.getAllTemplates);
router.get('/favorites/list', systemClothesController.getFavoriteSystemClothes);
router.get('/:id', systemClothesController.getSystemClothingById);
router.put('/:id', systemClothesController.updateSystemClothing);

module.exports = router;