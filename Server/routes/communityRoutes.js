const express = require('express');
const router = express.Router();
const communityController = require('../controllers/communityController');
const authMiddleware = require('../middleware/authMiddleware');

router.use(authMiddleware);
router.get('/', communityController.getAllPosts);
router.post('/', communityController.createPost);
router.post('/:id/like', communityController.toggleLikePost);
router.delete('/:id', communityController.deletePost);

module.exports = router;