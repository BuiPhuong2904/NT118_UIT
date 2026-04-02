const express = require('express');
const router = express.Router();
const systemClothesController = require('../controllers/systemClothesController');

router.get('/', systemClothesController.getAllTemplates);
router.get('/:id', systemClothesController.getSystemClothingById);

module.exports = router;