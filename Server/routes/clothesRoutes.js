const express = require('express');
const router = express.Router();
const clothesController = require('../controllers/clothesController');
const authMiddleware = require('../middleware/authMiddleware');


router.use(authMiddleware); 

router.get('/', clothesController.getAllClothes);
router.get('/user/:userId', clothesController.getClothesByUser);
router.get('/user/:userId/favorites', clothesController.getFavoriteClothesByUser);
router.get('/user/:userId/declutter', clothesController.getDeclutterClothesByUser);
router.get('/:id', clothesController.getClothingById);
router.post('/', clothesController.createClothes);
router.put('/:id', clothesController.updateClothes);
router.delete('/:id', clothesController.deleteClothes);

module.exports = router;