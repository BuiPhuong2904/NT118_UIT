const express = require('express');
const router = express.Router();
const wishlistController = require('../controllers/wishlistController');
const authMiddleware = require('../middleware/authMiddleware');

router.use(authMiddleware); 

router.post('/', wishlistController.addToWishlist);
router.get('/user/:userId', wishlistController.getUserWishlist);
router.patch('/:id/status', wishlistController.updateWishlistStatus);
router.delete('/:id', wishlistController.removeFromWishlist);

module.exports = router;