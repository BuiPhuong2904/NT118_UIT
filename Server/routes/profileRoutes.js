const express = require('express');
const router = express.Router();

const { getMyProfile, updateMyProfile } = require('../controllers/profileController');
const authMiddleware = require('../middleware/authMiddleware');

router.get('/me', authMiddleware, getMyProfile);
router.put('/me', authMiddleware, updateMyProfile);

module.exports = router;
