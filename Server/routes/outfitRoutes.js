const express = require('express');
const router = express.Router();
const outfitController = require('../controllers/outfitController');
const authMiddleware = require('../middleware/authMiddleware');

router.use(authMiddleware);

// Route lấy outfit theo user_id
router.get('/user/:userId', outfitController.getOutfitsByUser);

// Route lấy chi tiết outfit theo outfit_id
router.get('/:outfitId', outfitController.getOutfitById);

// Route thay đổi trạng thái yêu thích của outfit
router.put('/:id/favorite', outfitController.updateFavoriteStatus);

// Route tạo mới outfit
router.post('/', outfitController.createOutfit);

// Route cập nhật 
router.put('/:id', outfitController.updateOutfit);

// Route xóa outfit
router.delete('/:id', outfitController.deleteOutfit);

module.exports = router;